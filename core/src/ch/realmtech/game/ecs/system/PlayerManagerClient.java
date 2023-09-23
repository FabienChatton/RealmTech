package ch.realmtech.game.ecs.system;

import ch.realmtechServer.PhysiqueWorldHelper;
import ch.realmtechServer.craft.CraftStrategy;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.mod.RealmTechCoreMod;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManagerClient extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(PlayerManagerClient.class);
    @Wire
    private TextureAtlas textureAtlas;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<Box2dComponent> mBox2d;
    private final HashMap<UUID, Integer> players;

    {
        players = new HashMap<>();
    }

    public void createPlayerClient(float x, float y, UUID uuid) {
        logger.info("creation du joueur client {} ", uuid);
        final float playerWorldWith = 0.9f;
        final float playerWorldHigh = 0.9f;
        int playerId = world.create();
        if (!world.getSystem(TagManager.class).isRegistered("MAIN_PLAYER")) {
            world.getSystem(TagManager.class).register("MAIN_PLAYER", playerId);
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

        // player component
        PlayerComponent playerComponent = world.edit(playerId).create(PlayerComponent.class);

        // player connexion
        PlayerConnexionComponent playerConnexionComponent = world.edit(playerId).create(PlayerConnexionComponent.class);
        playerConnexionComponent.uuid = uuid;

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

        // animation
        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
        textureComponent.scale = 0.05f;
        TextureAtlas.AtlasRegion textureFront0 = textureAtlas.findRegion("reimu-front-0");
        TextureAtlas.AtlasRegion textureFront1 = textureAtlas.findRegion("reimu-front-1");
        TextureAtlas.AtlasRegion textureFront2 = textureAtlas.findRegion("reimu-front-2");
        playerComponent.animationFront = new TextureRegion[]{textureFront0, textureFront1, textureFront2};
        TextureAtlas.AtlasRegion textureLeft0 = textureAtlas.findRegion("reimu-left-0");
        TextureAtlas.AtlasRegion textureLeft1 = textureAtlas.findRegion("reimu-left-1");
        TextureAtlas.AtlasRegion textureLeft2 = textureAtlas.findRegion("reimu-left-2");
        playerComponent.animationLeft = new TextureRegion[]{textureLeft0, textureLeft1, textureLeft2};
        TextureAtlas.AtlasRegion textureBack0 = textureAtlas.findRegion("reimu-back-0");
        TextureAtlas.AtlasRegion textureBack1 = textureAtlas.findRegion("reimu-back-1");
        TextureAtlas.AtlasRegion textureBack2 = textureAtlas.findRegion("reimu-back-2");
        playerComponent.animationBack = new TextureRegion[]{textureBack0, textureBack1, textureBack2};
        TextureAtlas.AtlasRegion textureRight0 = textureAtlas.findRegion("reimu-right-0");
        TextureAtlas.AtlasRegion textureRight1 = textureAtlas.findRegion("reimu-right-1");
        TextureAtlas.AtlasRegion textureRight2 = textureAtlas.findRegion("reimu-right-2");
        playerComponent.animationRight = new TextureRegion[]{textureRight0, textureRight1, textureRight2};

        // default crafting table
        int defaultCraftingTable = world.create();
        int defaultResultInventory = world.create();
        world.edit(playerId).create(CraftingTableComponent.class).set(defaultCraftingTable, defaultResultInventory, CraftStrategy.craftingStrategyCraftingTable());
        world.edit(defaultCraftingTable).create(InventoryComponent.class).set(2, 2, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
        world.edit(defaultResultInventory).create(InventoryComponent.class).set(1, 1, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
        world.edit(defaultCraftingTable).create(CraftingComponent.class).set(RealmTechCoreMod.CRAFT, defaultResultInventory);
        players.put(uuid, playerId);
    }

    public HashMap<UUID, Integer> getPlayers() {
        return players;
    }

    public void setPlayerPos(float x, float y, UUID uuid) {
        int playerId = players.get(uuid);
        if (!PlayerComponent.isMainPlayer(playerId, world)) {
            Box2dComponent box2dComponent = mBox2d.get(playerId);
            box2dComponent.body.setTransform(x + box2dComponent.widthWorld / 2, y + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
        }
    }
}
