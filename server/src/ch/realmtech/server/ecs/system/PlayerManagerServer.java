package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ServerContext;
import ch.realmtech.server.craft.CraftStrategy;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtech.server.packet.clientPacket.TousLesJoueurPacket;
import ch.realmtech.server.serialize.inventory.InventorySerializer;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManagerServer extends BaseSystem {
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

    @Override
    protected void processSystem() {
        TousLesJoueursArg tousLesJoueursArg = getTousLesJoueurs();
        TousLesJoueurPacket tousLesJoueurPacket = new TousLesJoueurPacket(tousLesJoueursArg.nombreDeJoueur(), tousLesJoueursArg.pos(), tousLesJoueursArg.uuids());
        serverContext.getServerHandler().broadCastPacket(tousLesJoueurPacket);

//        // devrait pas etre ici, devrait etre appelé quand l'inventaire change
//        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
//        IntBag inventoryEntities = world.getAspectSubscriptionManager().get(Aspect.all(InventoryComponent.class)).getEntities();
//        int[] inventoryData = inventoryEntities.getData();
//        for (int i = 0; i < inventoryEntities.size(); i++) {
//            int inventoryId = inventoryData[i];
//            InventoryComponent inventoryComponent = mInventory.get(inventoryId);
//            serverContext.getServerHandler().broadCastPacket(new InventorySetPacket(mUuid.get(inventoryId).getUuid(), InventorySerializer.toBytes(world, inventoryComponent)));
//        }
    }

    public ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg createPlayerServer(Channel channel) {
        logger.info("creation du joueur {}", channel.remoteAddress());
        final float playerWorldWith = 0.9f;
        final float playerWorldHigh = 0.9f;
        int playerId = world.create();
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
        UUID playerUuid = UUID.randomUUID();
        PlayerConnexionComponent playerConnexionComponent = world.edit(playerId).create(PlayerConnexionComponent.class);
        playerConnexionComponent.set(channel);
        systemsAdminServer.uuidComponentManager.createRegisteredComponent(playerUuid, playerId);

        // inventory
        int chestId = systemsAdminServer.inventoryManager.createChest(playerId, UUID.randomUUID(), InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW);
        InventoryComponent chestInventoryComponent = mInventory.get(chestId);

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
        players.add(playerId);
        logger.info("le joueur {} a été ajouté avec l'id dans le monde {}", channel.remoteAddress(), playerId);
        return new ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg(x, y, playerUuid,
                InventorySerializer.toBytes(world, chestInventoryComponent),
                mUuid.get(chestId).getUuid(),
                mUuid.get(craftingInventories[0]).getUuid(),
                mUuid.get(craftingInventories[1]).getUuid()
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
            PlayerConnexionComponent playerComponent = mPlayer.get(playerId);
            poss[i] = new Vector2(positionComponent.x, positionComponent.y);
            UuidComponent uuidComponent = systemsAdminServer.uuidComponentManager.getRegisteredComponent(playerId);
            uuids[i] = uuidComponent.getUuid();
        }
        return new TousLesJoueursArg(players.size(), poss, uuids);
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
