package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.divers.Notify;
import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.options.server.VerifyTokenOptionEntry;
import ch.realmtech.server.packet.clientPacket.*;
import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.serialize.player.PlayerSerializerConfig;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableIntBag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.physics.box2d.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerManagerServer extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(PlayerManagerServer.class);
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private final IntBag players;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<LifeComponent> mLife;
    private ComponentMapper<QuestPlayerPropertyComponent> mQuestPlayer;
    private ComponentMapper<ItemComponent> mItem;
    private VerifyTokenOptionEntry verifyTokenOptionEntry;

    public PlayerManagerServer() {
        players = new IntBag();
    }

    @Override
    protected void initialize() {
        super.initialize();
        verifyTokenOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), VerifyTokenOptionEntry.class);
    }

    public ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg createPlayerServer(Channel channel, UUID playerUuid, String playerUsername) {
        final float playerWorldWith = 0.9f;
        final float playerWorldHigh = 0.9f;
        int playerId = world.create();
        players.add(playerId);

        // player connexion component
        PlayerConnexionComponent playerConnexionComponent = world.edit(playerId).create(PlayerConnexionComponent.class);
        playerConnexionComponent.set(channel);
        systemsAdminServer.getUuidEntityManager().registerEntityIdWithUuid(playerUuid, playerId);

        boolean hasPlayerSaveSuccess = false;
        float posX;
        float posY;

        int chestId;
        int heart = -1;

        try {
            loadPlayerSave(playerUsername, playerId);
            hasPlayerSaveSuccess = true;
        } catch (IOException e) {
            logger.warn("Can not load player save. Username: {}, {}", playerUsername, e.getMessage());
        }

        if (hasPlayerSaveSuccess) {
            posX = mPosition.get(playerId).x;
            posY = mPosition.get(playerId).y;

            chestId = systemsAdminServer.getInventoryManager().getChestInventoryId(playerId);

            // player serializer v2
            if (mLife.has(playerId)) {
                heart = mLife.get(playerId).getHeart();
            }
        } else {
            posX = 0;
            posY = 0;

            // position component
            PositionComponent positionComponent = world.edit(playerId).create(PositionComponent.class);
            positionComponent.set(posX, posY);

            // default inventory
            chestId = systemsAdminServer.getInventoryManager().createChest(playerId, UUID.randomUUID(), InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW);
        }

        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyPlayer = physicWorld.createBody(bodyDef);
        bodyPlayer.setUserData(playerId);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(playerWorldWith / 2f, playerWorldHigh / 2f);
        fixtureDef.shape = playerShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_PLAYER;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
        bodyPlayer.createFixture(fixtureDef);

        playerShape.dispose();

        // box2d component
        Box2dComponent box2dComponent = world.edit(playerId).create(Box2dComponent.class);
        box2dComponent.set(playerWorldWith, playerWorldHigh, bodyPlayer);
        box2dComponent.body.setTransform(posX, posY, box2dComponent.body.getAngle());

        // inventory
        InventoryComponent chestInventoryComponent = mInventory.get(chestId);

        // inventory cursor
        int inventoryCursorId = systemsAdminServer.getInventoryManager().createCursorInventory(playerId, UUID.randomUUID(), 1, 1);

        // movement component
        PlayerMovementComponent playerMovementComponent = world.edit(playerId).create(PlayerMovementComponent.class);
        playerMovementComponent.set(10, 10);


        // default crafting table
        int[] craftingInventories = systemsAdminServer.getInventoryManager().createCraftingTable(playerId, UUID.randomUUID(), 2, 2, UUID.randomUUID());

        // pick up item component
        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
        pickerGroundItemComponent.set(10);

        // life component
        if (heart == -1) {
            heart = 10;
        }
        mLife.create(playerId).set(heart);

        if (!mQuestPlayer.has(playerId)) {
            List<? extends QuestEntry> allQuestsEntry = systemsAdminServer.getQuestManagerServer().getQuestManagerEntry().getQuests();
            List<QuestPlayerProperty> questPlayerProperties = new ArrayList<>(allQuestsEntry.size());
            for (QuestEntry questEntry : allQuestsEntry) {
                questPlayerProperties.add(new QuestPlayerProperty(questEntry));
            }
            mQuestPlayer.create(playerId).set(questPlayerProperties);
            logger.debug("Can not read quests from Player {}. He get default quests", playerUsername);
        }

        serverContext.getEcsEngineServer().nextTick(() -> {
            serverContext.getServerConnexion().sendPacketTo(new PlayerCreateQuestPacket(serverContext.getSerializerController().getQuestSerializerController().encode(playerId)), channel);
        });

        return new ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg(posX, posY, playerUuid,
                serverContext.getSerializerController().getInventorySerializerManager().encode(chestInventoryComponent),
                systemsAdminServer.getUuidEntityManager().getEntityUuid(chestId),
                systemsAdminServer.getUuidEntityManager().getEntityUuid(craftingInventories[0]),
                systemsAdminServer.getUuidEntityManager().getEntityUuid(craftingInventories[1]),
                systemsAdminServer.getUuidEntityManager().getEntityUuid(inventoryCursorId)
        );
    }

    private void deletePlayer(int playerId) {
        int chestInventoryId = systemsAdminServer.getInventoryManager().getChestInventoryId(playerId);
        int craftingInventoryId = systemsAdminServer.getInventoryManager().getCraftingInventoryId(playerId);
        int craftingResultInventoryId = systemsAdminServer.getInventoryManager().getCraftingResultInventoryId(playerId);

        systemsAdminServer.getInventoryManager().removeInventoryUi(chestInventoryId);
        systemsAdminServer.getInventoryManager().removeInventoryUi(craftingInventoryId);
        systemsAdminServer.getInventoryManager().removeInventoryUi(craftingResultInventoryId);
        systemsAdminServer.getUuidEntityManager().deleteRegisteredEntity(playerId);

        world.delete(playerId);
    }

    public IntBag getPlayers() {
        return players;
    }

    public List<PlayerConnexionComponent> getPlayersConnexion() {
        List<PlayerConnexionComponent> ret = new ArrayList<>();
        IntBag playersIntBag = getPlayers();
        int[] data = playersIntBag.getData();
        for (int i = 0; i < playersIntBag.size(); i++) {
            ret.add(mPlayerConnexion.get(data[i]));
        }
        return ret;
    }

    public void setPlayerUsername(UUID uuid, String username) {
        int playerByUuid = getPlayerByUuid(uuid);
        mPlayerConnexion.get(playerByUuid).setUsername(username);
    }

    public void savePlayers() throws IOException {
        int[] playersData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            savePlayer(playersData[i]);
        }
    }

    public void savePlayer(int playerId) throws IOException {
        File playerFile;
        try {
            String playerUsername = mPlayerConnexion.get(playerId).getUsername();
            Path playerDir = getPlayerDir(playerUsername);
            if (!playerDir.toFile().exists()) Files.createDirectories(playerDir);
            playerFile = getPLayerFile(playerDir).toFile();
        } catch (Exception e) {
            logger.warn("can not save player inventory.", e);
            return;
        }

        SerializedApplicationBytes playerBytes = serverContext.getSerializerController().getPlayerSerializerController().encode(new PlayerSerializerConfig().playerId(playerId).writeInventory(true).writeQuestProperty(true));

        playerFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(playerFile)) {
            fos.write(playerBytes.applicationBytes());
        }
    }

    public void loadPlayerSave(String playerUsername, int playerId) throws IOException {
        try (FileInputStream fis = new FileInputStream(getPLayerFile(getPlayerDir(playerUsername)).toFile())) {
            byte[] rawInventoryBytes = fis.readAllBytes();
            Consumer<Integer> setPlayer = serverContext.getSerializerController().getPlayerSerializerController().decode(new SerializedApplicationBytes(rawInventoryBytes));
            setPlayer.accept(playerId);
        }
    }

    private static Path getPLayerFile(Path playerDir) {
        return Path.of(playerDir.toString(), "player.ps");
    }

    private Path getPlayerDir(String playerUsername) {
        return Path.of(DataCtrl.getPlayersDir(systemsAdminServer.getSaveInfManager().getSaveName()).toPath().toString(), playerUsername);
    }

    public int getPlayerByUsername(String username) {
        IntBag players = getPlayers();
        int[] playersData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            if (mPlayerConnexion.get(playerId).getUsername().equals(username)) {
                return playerId;
            }
        }
        return -1;
    }

    public int getPlayerByChannel(Channel clientChannel) {
        int[] playersData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            if (mPlayerConnexion.get(playersData[i]).channel.equals(clientChannel)) {
                return playersData[i];
            }
        }
        return -1;
    }

    public int getPlayerByUuid(UUID uuid) {
        int[] playersData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            if (systemsAdminServer.getUuidEntityManager().getEntityUuid(playerId).equals(uuid)) {
                return playerId;
            }
        }
        return -1;
    }

    public PlayerConnexionComponent getPlayerConnexionComponentByChannel(Channel clientChannel) {
        int playerId = getPlayerByChannel(clientChannel);
        return mPlayerConnexion.get(playerId);
    }

    public PlayerConnexionComponent getPlayerConnexionComponentById(int playerId) {
        return mPlayerConnexion.get(playerId);
    }

    public void removePlayer(Channel channel) {
        int[] playersConnexionData = players.getData();
        for (int i = 0; i < playersConnexionData.length; i++) {
            int playerId = playersConnexionData[i];
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
            if (playerConnexionComponent.channel == channel) {
                players.removeValue(playerId);
                deletePlayer(playerId);
                break;
            }
        }
    }

    public int getPlayerByUsernameOrUuid(String playerIdentifier) {
        int playerId;
        try {
            UUID playerUuid = UUID.fromString(playerIdentifier);
            playerId = getPlayerByUuid(playerUuid);
        } catch (IllegalArgumentException e) {
            // uuid not valid
            playerId = systemsAdminServer.getPlayerManagerServer().getPlayerByUsername(playerIdentifier);
        }
        return playerId;
    }

    public void askPlayerRespawn(Channel clientChannel) {
        int playerId = getPlayerByChannel(clientChannel);
        if (playerId != -1) {
            mBox2d.get(playerId).body.setTransform(0, 0, 0);
            mLife.get(playerId).set(10);
        }
    }

    public void askPlayerConnexion(Channel clientChanel, String username) {
        logger.info("Player {} try to login. {}", username, clientChanel);

        UUID playerUuid;
        try {
            // check if username is already on this server
            if (getPlayerByUsername(username) != -1)
                throw new IllegalArgumentException("A Player with this username already existe on the server");
            playerUuid = UUID.fromString(serverContext.getAuthController().verifyAccessToken(username));
            if (getPlayerByUuid(playerUuid) != -1) {
                throw new IllegalArgumentException("A player with the same uuid already existe on the server");
            }
        } catch (Exception e) {
            serverContext.getServerConnexion().sendPacketTo(new DisconnectMessagePacket(e.getMessage()), clientChanel);
            logger.info("Player {} has failed to been authenticated. Cause : {}, {}", username, e.getMessage(), clientChanel);
            return;
        }

        logger.info("Player: {} with uuid: {} has successfully been authenticated. chanel: {}. Verify access token: {}", username, playerUuid, clientChanel, verifyTokenOptionEntry.getValue());

        ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg connexionJoueurReussitArg;
        try {
            connexionJoueurReussitArg = createPlayerServer(clientChanel, playerUuid, username);
            serverContext.getServerConnexion().sendPacketTo(new ConnexionJoueurReussitPacket(connexionJoueurReussitArg), clientChanel);
            setPlayerUsername(playerUuid, username);
        } catch (Exception e) {
            serverContext.getServerConnexion().sendPacketTo(new DisconnectMessagePacket(e.getMessage()), clientChanel);
            removePlayer(clientChanel);
            logger.info("Player {} has failed to been created. Cause : {}, {}", username, e.getMessage(), clientChanel);
            return;
        }

        serverContext.getEcsEngineServer().nextTickSchedule(60 * 3, () -> {
            serverContext.getServerConnexion().sendPacketTo(new NotifySendPacket(serverContext, new Notify("Advice", "Open c\nto open quest menu", 5)), clientChanel);
        });

        // new player with new connexion get all players.
        // players already on server get this player.
        int chunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(connexionJoueurReussitArg.x()));
        int chunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(connexionJoueurReussitArg.y()));
        int thisPlayerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChanel);
        ImmutableIntBag<?> players = systemsAdminServer.getPlayerSubscriptionSystem().getPlayersInRangeForChunkPos(new Position(chunkPosX, chunkPosY));
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            if (thisPlayerId == playerId) continue;
            UUID uuid = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(playerId);
            serverContext.getServerConnexion().sendPacketTo(new PlayerCreateConnexion(uuid), clientChanel);
        }

        serverContext.getServerConnexion().sendPacketToSubscriberForChunkPosExcept(new PlayerCreateConnexion(playerUuid), chunkPosX, chunkPosY, clientChanel);

        // Subscription to his inventory
        systemsAdminServer.getPlayerSubscriptionSystem().addEntitySubscriptionToPlayer(thisPlayerId, connexionJoueurReussitArg.inventoryUuid());
        systemsAdminServer.getPlayerSubscriptionSystem().addEntitySubscriptionToPlayer(thisPlayerId, connexionJoueurReussitArg.inventoryCraftResultUuid());
        systemsAdminServer.getPlayerSubscriptionSystem().addEntitySubscriptionToPlayer(thisPlayerId, connexionJoueurReussitArg.inventoryCraftUuid());
        systemsAdminServer.getPlayerSubscriptionSystem().addEntitySubscriptionToPlayer(thisPlayerId, connexionJoueurReussitArg.inventoryCursorUuid());
    }

    public void eatItem(Channel clientChannel, UUID itemUuid) {
        int playerId = getPlayerByChannel(clientChannel);
        int itemId = systemsAdminServer.getUuidEntityManager().getEntityId(itemUuid);
        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        if (itemId == -1) {
            logger.info("Can not find item {} for eating. Requested by {}", itemUuid, playerConnexionComponent.getUsername());
            return;
        }

        LifeComponent lifeComponent = mLife.get(playerId);
        if (lifeComponent == null) {
            logger.info("Can not find player life component {}", playerConnexionComponent.getUsername());
            return;
        }

        ItemComponent itemComponent = mItem.get(itemId);
        if (itemComponent == null) {
            logger.info("Id {} is not a item. Requested for eating by {}", itemId, playerConnexionComponent.getUsername());
            return;
        }

        int heartToRestore = itemComponent.itemRegisterEntry.getItemBehavior().getEatRestore();
        if (heartToRestore <= 0) {
            logger.info("Can not eat this item: {}. Heart to restore is invalide: {} must be positive", itemUuid, heartToRestore);
            return;
        }

        int chestInventoryId = systemsAdminServer.getInventoryManager().getChestInventoryId(playerId);
        if (chestInventoryId == -1) {
            logger.info("Can not find player chest inventory. player {}", playerConnexionComponent.getUsername());
            return;
        }

        lifeComponent.increaseHeart(heartToRestore);
        if (!systemsAdminServer.getInventoryManager().deleteItemInInventory(chestInventoryId, itemId)) {
            logger.info("Fail to delete item eaten. player {}", playerConnexionComponent.getUsername());
        }

        serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(serverContext, chestInventoryId), clientChannel);
    }
}
