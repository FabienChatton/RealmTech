package ch.realmtechServer.ecs.system;

import ch.realmtechCommuns.PhysiqueWorldHelper;
import ch.realmtechCommuns.ecs.component.*;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.clientPacket.TousLesJoueurPacket;
import ch.realmtechServer.ServerContext;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.UUID;

public class PlayerManagerServer extends BaseSystem {
    private final static Logger logger = LoggerFactory.getLogger(PlayerManagerServer.class);
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private final IntBag players;
    private ComponentMapper<PlayerConnectionComponent> mPlayerConnection;
    private ComponentMapper<PositionComponent> mPosition;

    public PlayerManagerServer() {
        players = new IntBag();
    }

    @Override
    protected void processSystem() {
        TousLesJoueursArg tousLesJoueursArg = getTousLesJoueurs();
        TousLesJoueurPacket tousLesJoueurPacket = new TousLesJoueurPacket(tousLesJoueursArg.nombreDeJoueur(), tousLesJoueursArg.pos(), tousLesJoueursArg.uuids());
        serverContext.getServerHandler().broadCastPacket(tousLesJoueurPacket);
    }

    public ConnectionJoueurReussitPacket.ConnectionJoueurReussitArg createPlayerServer(Channel channel) {
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

        // player connection component
        UUID uuid = UUID.randomUUID();
        PlayerConnectionComponent playerConnectionComponent = world.edit(playerId).create(PlayerConnectionComponent.class);
        playerConnectionComponent.set(channel, uuid);

        // movement component
        MovementComponent movementComponent = world.edit(playerId).create(MovementComponent.class);
        movementComponent.set(10, 10);

        // position component
        PositionComponent positionComponent = world.edit(playerId).create(PositionComponent.class);
        positionComponent.set(box2dComponent, x, y);

        // inventory component
        InventoryComponent inventoryComponent = world.edit(playerId).create(InventoryComponent.class);
        inventoryComponent.set(InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);

        // pick up item component
        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
        pickerGroundItemComponent.set(10);
        players.add(playerId);
        logger.info("le joueur {} a été ajouté avec l'id dans le monde {}", channel.remoteAddress(), playerId);
        return new ConnectionJoueurReussitPacket.ConnectionJoueurReussitArg(x, y, uuid);
    }

    public IntBag getPlayers() {
        return players;
    }

    public TousLesJoueursArg getTousLesJoueurs() {
        int[] playersData = players.getData();
        Vector2[] poss = new Vector2[players.size()];
        UUID[] uuids = new UUID[players.size()];
        ComponentMapper<PlayerConnectionComponent> mPlayer = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnectionComponent.class);
        ComponentMapper<PositionComponent> mPos = serverContext.getEcsEngineServer().getWorld().getMapper(PositionComponent.class);
        for (int i = 0; i < players.size(); i++) {
            PositionComponent positionComponent = mPos.get(playersData[i]);
            PlayerConnectionComponent playerComponent = mPlayer.get(playersData[i]);
            poss[i] = new Vector2(positionComponent.x, positionComponent.y);
            uuids[i] = playerComponent.uuid;
        }
        return new TousLesJoueursArg(players.size(), poss, uuids);
    }
    public record TousLesJoueursArg(int nombreDeJoueur, Vector2[] pos, UUID[] uuids) { }
    public int getPlayerByChannel(Channel clientChannel) {
        int[] playersData = players.getData();
        for (int i = 0; i < players.size(); i++) {
            if (mPlayerConnection.get(playersData[i]).channel.equals(clientChannel)) {
                return playersData[i];
            }
        }
        throw new NoSuchElementException("Le player au channel " + clientChannel.toString() + " n'est pas dans la liste des joueurs");
    }

    public void removePlayer(Channel channel) {
        int[] playersConnectionData = players.getData();
        for (int i = 0; i < playersConnectionData.length; i++) {
            PlayerConnectionComponent playerConnectionComponent = mPlayerConnection.get(players.get(i));
            if (playerConnectionComponent.channel == channel) {
                players.removeIndex(i);
                break;
            }
        }
    }

    public void playerMove(Channel clientChannel, float impulseX, float impulseY, Vector2 pos) {
        int player = getPlayerByChannel(clientChannel);
        PositionComponent positionComponent = mPosition.get(player);
        positionComponent.set(pos.x, pos.y);
    }
}
