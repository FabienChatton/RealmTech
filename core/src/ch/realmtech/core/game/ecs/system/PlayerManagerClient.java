package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.component.MainPlayerComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ecs.component.*;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PlayerManagerClient extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(PlayerManagerClient.class);
    @Wire
    private SystemsAdminClient systemsAdminClient;
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<InventoryComponent> mInventory;
    public final static String MAIN_PLAYER_TAG = "MAIN_PLAYER";

    public int createPlayerClient(UUID uuid) {
        logger.info("Creating player for client side: {} ", uuid);
        int playerId = world.create();
        if (!systemsAdminClient.getTagManager().isRegistered(MAIN_PLAYER_TAG)) {
            systemsAdminClient.getTagManager().register(MAIN_PLAYER_TAG, playerId);
            int mapId = world.create();
            InfMapComponent infMapComponent = world.edit(mapId).create(InfMapComponent.class);
            infMapComponent.infChunks = new int[0];
            systemsAdminClient.getTagManager().register("infMap", mapId);
            world.edit(playerId).create(MainPlayerComponent.class);
        }

        // player component
        PlayerComponent playerComponent = world.edit(playerId).create(PlayerComponent.class);

        // player connexion
        PlayerConnexionComponent playerConnexionComponent = world.edit(playerId).create(PlayerConnexionComponent.class);
        systemsAdminClient.getUuidEntityManager().registerEntityIdWithUuid(uuid, playerId);

        // movement component
        PlayerMovementComponent playerMovementComponent = world.edit(playerId).create(PlayerMovementComponent.class);
        playerMovementComponent.set(10, 10);

        MouvementComponent mouvementComponent = world.edit(playerId).create(MouvementComponent.class);

        // pick up item component
        PickerGroundItemComponent pickerGroundItemComponent = world.edit(playerId).create(PickerGroundItemComponent.class);
        pickerGroundItemComponent.set(10);

        // animation
        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
        textureComponent.scale = 1.6f;
        TextureAnimationComponent textureAnimationComponent = world.edit(playerId).create(TextureAnimationComponent.class);
        TextureAtlas.AtlasRegion textureFront0 = textureAtlas.findRegion("reimu-front-0");
        TextureAtlas.AtlasRegion textureFront1 = textureAtlas.findRegion("reimu-front-1");
        TextureAtlas.AtlasRegion textureFront2 = textureAtlas.findRegion("reimu-front-2");
        textureAnimationComponent.animationFront = new TextureRegion[]{textureFront0, textureFront1, textureFront2};
        TextureAtlas.AtlasRegion textureLeft0 = textureAtlas.findRegion("reimu-left-0");
        TextureAtlas.AtlasRegion textureLeft1 = textureAtlas.findRegion("reimu-left-1");
        TextureAtlas.AtlasRegion textureLeft2 = textureAtlas.findRegion("reimu-left-2");
        textureAnimationComponent.animationLeft = new TextureRegion[]{textureLeft0, textureLeft1, textureLeft2};
        TextureAtlas.AtlasRegion textureBack0 = textureAtlas.findRegion("reimu-back-0");
        TextureAtlas.AtlasRegion textureBack1 = textureAtlas.findRegion("reimu-back-1");
        TextureAtlas.AtlasRegion textureBack2 = textureAtlas.findRegion("reimu-back-2");
        textureAnimationComponent.animationBack = new TextureRegion[]{textureBack0, textureBack1, textureBack2};
        TextureAtlas.AtlasRegion textureRight0 = textureAtlas.findRegion("reimu-right-0");
        TextureAtlas.AtlasRegion textureRight1 = textureAtlas.findRegion("reimu-right-1");
        TextureAtlas.AtlasRegion textureRight2 = textureAtlas.findRegion("reimu-right-2");
        textureAnimationComponent.animationRight = new TextureRegion[]{textureRight0, textureRight1, textureRight2};

        return playerId;
    }

    public void playerInRange(float worldX, float worldY, UUID playerUuid) {
        int playerId = systemsAdminClient.getUuidEntityManager().getEntityId(playerUuid);
        if (playerId == -1) return;

        final float playerWorldWith = 0.9f;
        final float playerWorldHigh = 0.9f;

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

        // position component
        PositionComponent positionComponent = world.edit(playerId).create(PositionComponent.class);
        positionComponent.set(box2dComponent, worldX, worldY);
    }

    public void setPlayerPos(float x, float y, UUID playerUuid) {
        int playerId = systemsAdminClient.getUuidEntityManager().getEntityId(playerUuid);
        if (playerId == -1) return;
        if (!mBox2d.has(playerId)) {
            // create the player position for the first time, when is in range
            playerInRange(x, y, playerUuid);
        } else {
            Box2dComponent box2dComponent = mBox2d.get(playerId);
            box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        }
    }

    public void playerOutOfRange(UUID playerUuid) {
        int playerId = systemsAdminClient.getUuidEntityManager().getEntityId(playerUuid);
        if (playerId == -1) return;
        mBox2d.remove(playerId);
        mPos.get(playerId);
    }

    public void removePlayer(UUID playerUuid) {
        int playerId = systemsAdminClient.getUuidEntityManager().getEntityId(playerUuid);
        if (playerId == -1) return;
        world.delete(playerId);
        systemsAdminClient.getUuidEntityManager().deleteRegisteredEntity(playerId);
    }

    public int getMainPlayer() {
        return systemsAdminClient.getTagManager().getEntityId(MAIN_PLAYER_TAG);
    }
}
