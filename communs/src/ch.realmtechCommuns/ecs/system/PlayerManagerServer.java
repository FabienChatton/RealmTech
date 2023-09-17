package ch.realmtechCommuns.ecs.system;

import ch.realmtechCommuns.PhysiqueWorldHelper;
import ch.realmtechCommuns.ecs.component.*;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerManagerServer extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(PlayerManagerServer.class);
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private final List<Integer> playersConnection;
    private ComponentMapper<PlayerConnectionComponent> mPlayerConnection;

    public PlayerManagerServer() {
        playersConnection = new ArrayList<>();
    }

    public Vector2 createPlayerServer(Channel channel) {
        logger.info("creation du joueur {}", channel.remoteAddress());
        final float playerWorldWith = 0.9f;
        final float playerWorldHigh = 0.9f;
        int playerId = world.create();

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
        PlayerConnectionComponent playerConnectionComponent = world.edit(playerId).create(PlayerConnectionComponent.class);
        playerConnectionComponent.set(channel);

        // movement component
        MovementComponent movementComponent = world.edit(playerId).create(MovementComponent.class);
        movementComponent.set(10, 10);

        // position component
        PositionComponent positionComponent = world.edit(playerId).create(PositionComponent.class);
        positionComponent.set(box2dComponent, 0, 0);

        // inventory component
        InventoryComponent inventoryComponent = world.edit(playerId).create(InventoryComponent.class);
        inventoryComponent.set(InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);

        // pick up item component
        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
        pickerGroundItemComponent.set(10);
        playersConnection.add(playerId);
        logger.info("le joueur {} a été ajouté avec l'id dans le monde {}", channel.remoteAddress(), playerId);
        return new Vector2(0, 0);
    }

    public List<PlayerConnectionComponent> getPlayersConnection() {
        return playersConnection.stream().map(mPlayerConnection::get).collect(Collectors.toList());
    }

    public float[] getPlayerConnectionPos(PlayerConnectionComponent playerConnectionComponent) {
        return new float[]{1, 0};
    }
}
