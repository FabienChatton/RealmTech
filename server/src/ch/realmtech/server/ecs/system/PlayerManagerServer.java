package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
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
    private ComponentMapper<UuidComponent> mUuid;
    private ComponentMapper<InventoryComponent> mInventory;

    public PlayerManagerServer() {
        players = new IntBag();
    }

    public ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg createPlayerServer(Channel channel, UUID playerUuid) {
        final float playerWorldWith = 0.9f;
        final float playerWorldHigh = 0.9f;
        int playerId = world.create();
        players.add(playerId);
        float x = 0, y = 0;

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

        // player connexion component
        PlayerConnexionComponent playerConnexionComponent = world.edit(playerId).create(PlayerConnexionComponent.class);
        playerConnexionComponent.set(channel);
        systemsAdminServer.uuidComponentManager.createRegisteredComponent(playerUuid, playerId);

        // inventory
        int chestId;
        try {
            serverContext.getSystem(PlayerManagerServer.class).loadPlayerInventory(playerUuid);
            chestId = systemsAdminServer.inventoryManager.getChestInventoryId(playerId);
        } catch (Exception e) {
            chestId = systemsAdminServer.inventoryManager.createChest(playerId, UUID.randomUUID(), InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW);
        }
        InventoryComponent chestInventoryComponent = mInventory.get(chestId);

        // inventory cursor
        int inventoryCursorId = systemsAdminServer.inventoryManager.createCursorInventory(playerId, UUID.randomUUID(), 1, 1);

        // movement component
        MovementComponent movementComponent = world.edit(playerId).create(MovementComponent.class);
        movementComponent.set(10, 10);

        // position component
        PositionComponent positionComponent = world.edit(playerId).create(PositionComponent.class);
        positionComponent.set(box2dComponent, x, y);

        // default crafting table
        int[] craftingInventories = systemsAdminServer.inventoryManager.createCraftingTable(playerId, UUID.randomUUID(), 2,2, UUID.randomUUID());

        // pick up item component
        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
        pickerGroundItemComponent.set(10);

        return new ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg(x, y, playerUuid,
                serverContext.getSerializerController().getInventorySerializerManager().encode(chestInventoryComponent),
                mUuid.get(chestId).getUuid(),
                mUuid.get(craftingInventories[0]).getUuid(),
                mUuid.get(craftingInventories[1]).getUuid(),
                mUuid.get(inventoryCursorId).getUuid()
        );
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

    public TousLesJoueursArg getTousLesJoueurs() {
        int[] playersData = players.getData();
        Vector2[] poss = new Vector2[players.size()];
        UUID[] uuids = new UUID[players.size()];
        ComponentMapper<PlayerConnexionComponent> mPlayer = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class);
        ComponentMapper<PositionComponent> mPos = serverContext.getEcsEngineServer().getWorld().getMapper(PositionComponent.class);
        for (int i = 0; i < players.size(); i++) {
            int playerId = playersData[i];
            PositionComponent positionComponent = mPos.get(playerId);
            if (positionComponent == null) return null;
            PlayerConnexionComponent playerComponent = mPlayer.get(playerId);
            poss[i] = new Vector2(positionComponent.x, positionComponent.y);
            UuidComponent uuidComponent = systemsAdminServer.uuidComponentManager.getRegisteredComponent(playerId);
            uuids[i] = uuidComponent.getUuid();
        }
        return new TousLesJoueursArg(players.size(), poss, uuids);
    }

    public void setPlayerUsername(UUID uuid, String username) {
        int playerByUuid = getPlayerByUuid(uuid);
        mPlayerConnexion.get(playerByUuid).setUsername(username);
    }

    public void savePlayersInventory() throws IOException {
        int[] playersData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            savePlayerInventory(playersData[i]);
        }
    }

    public void savePlayerInventory(int playerId) throws IOException {
        File playerInventoryFile;
        try {
            UUID playerUuid = systemsAdminServer.uuidComponentManager.getRegisteredComponent(playerId).getUuid();
            Path playerDir = getPlayerDir(playerUuid);
            if (!playerDir.toFile().exists()) Files.createDirectories(playerDir);
            playerInventoryFile = getPlayerInventoryFile(playerDir).toFile();
        } catch (Exception e) {
            logger.warn("can not save player inventory.", e);
            return;
        }

        SerializedApplicationBytes chestInventoryBytes = serverContext.getSerializerController().getChestSerializerController().encode(playerId);

        playerInventoryFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(playerInventoryFile)) {
            fos.write(chestInventoryBytes.applicationBytes());
        }
    }

    public void loadPlayerInventory(UUID playerUuid) throws IOException {
        int playerId = getPlayerByUuid(playerUuid);
        Path playerDir = getPlayerDir(playerUuid);
        if (!playerDir.toFile().exists()) return;

        try (FileInputStream fis = new FileInputStream(getPlayerInventoryFile(getPlayerDir(playerUuid)).toFile())) {
            byte[] rawInventoryBytes = fis.readAllBytes();
            ChestEditEntity chestEditEntityArg = serverContext.getSerializerController().getChestSerializerController().decode(new SerializedApplicationBytes(rawInventoryBytes));
        }
    }

    private static Path getPlayerInventoryFile(Path playerDir) {
        return Path.of(playerDir.toString(), "inventory.pis");
    }

    private Path getPlayerDir(UUID playerUuid) {
        return Path.of(DataCtrl.getPlayersDir(systemsAdminServer.saveInfManager.getSaveName()).toPath().toString(), playerUuid.toString());
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

    public record TousLesJoueursArg(int nombreDeJoueur, Vector2[] pos, UUID[] uuids) { }
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
            if (systemsAdminServer.uuidComponentManager.getRegisteredComponent(playerId).getUuid().equals(uuid)) {
                return playerId;
            }
        }
        return -1;
    }

    public PlayerConnexionComponent getPlayerConnexionComponentByChannel(Channel clientChannel) {
        int playerId = getPlayerByChannel(clientChannel);
        return mPlayerConnexion.get(playerId);
    }

    public void removePlayer(Channel channel) {
        int[] playersConnexionData = players.getData();
        for (int i = 0; i < playersConnexionData.length; i++) {
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(players.get(i));
            if (playerConnexionComponent.channel == channel) {
                players.removeIndex(i);
                break;
            }
        }
    }

}
