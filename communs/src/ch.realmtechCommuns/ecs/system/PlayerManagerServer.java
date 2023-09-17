package ch.realmtechCommuns.ecs.system;

import ch.realmtechCommuns.PhysiqueWorldHelper;
import ch.realmtechCommuns.ecs.component.*;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.physics.box2d.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PlayerManagerServer extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(PlayerManagerServer.class);
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private final IntBag players;
    private ComponentMapper<PlayerConnectionComponent> mPlayerConnection;

    public PlayerManagerServer() {
        players = new IntBag();
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
}
