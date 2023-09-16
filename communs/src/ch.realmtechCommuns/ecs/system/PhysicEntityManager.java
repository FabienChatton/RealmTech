package ch.realmtechCommuns.ecs.system;

import ch.realmtechCommuns.PhysiqueWorldHelper;
import ch.realmtechCommuns.ecs.component.*;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.physics.box2d.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PhysicEntityManager extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(PhysicEntityManager.class);
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private BodyDef bodyDef;
    private final List<Integer> players;
    private ComponentMapper<PlayerComponent> mPlayer;

    public PhysicEntityManager() {
        players = new ArrayList<>();
    }

    public Body addPhysicEntity(BodyDef bodyDef) {
        return physicWorld.createBody(bodyDef);
    }

    public void createPlayer(Channel channel) {
        logger.info("creation du joueur {}", channel.remoteAddress());
        final float playerWorldWith = 0.9f;
        final float playerWorldHigh = 0.9f;
        int playerId = world.create();
        world.getSystem(TagManager.class).register(PlayerComponent.TAG, playerId);

        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body bodyPlayer = addPhysicEntity(bodyDef);
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
        playerComponent.set(channel);

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

//        // texture component
//        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
//        final TextureRegion texture = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class).findRegion("reimu");
//        textureComponent.set(texture);

        // animation
//        TextureComponent textureComponent = world.edit(playerId).create(TextureComponent.class);
//        textureComponent.scale = 0.05f;
//        TextureAtlas.AtlasRegion textureFront0 = textureAtlas.findRegion("reimu-front-0");
//        TextureAtlas.AtlasRegion textureFront1 = textureAtlas.findRegion("reimu-front-1");
//        TextureAtlas.AtlasRegion textureFront2 = textureAtlas.findRegion("reimu-front-2");
//        playerComponent.animationFront = new TextureRegion[]{textureFront0, textureFront1, textureFront2};
//        TextureAtlas.AtlasRegion textureLeft0 = textureAtlas.findRegion("reimu-left-0");
//        TextureAtlas.AtlasRegion textureLeft1 = textureAtlas.findRegion("reimu-left-1");
//        TextureAtlas.AtlasRegion textureLeft2 = textureAtlas.findRegion("reimu-left-2");
//        playerComponent.animationLeft = new TextureRegion[]{textureLeft0, textureLeft1, textureLeft2};
//        TextureAtlas.AtlasRegion textureBack0 = textureAtlas.findRegion("reimu-back-0");
//        TextureAtlas.AtlasRegion textureBack1 = textureAtlas.findRegion("reimu-back-1");
//        TextureAtlas.AtlasRegion textureBack2 = textureAtlas.findRegion("reimu-back-2");
//        playerComponent.animationBack = new TextureRegion[]{textureBack0, textureBack1, textureBack2};
//        TextureAtlas.AtlasRegion textureRight0 = textureAtlas.findRegion("reimu-right-0");
//        TextureAtlas.AtlasRegion textureRight1 = textureAtlas.findRegion("reimu-right-1");
//        TextureAtlas.AtlasRegion textureRight2 = textureAtlas.findRegion("reimu-right-2");
//        playerComponent.animationRight = new TextureRegion[]{textureRight0, textureRight1, textureRight2};

        // default crafting table
//        int defaultCraftingTable = world.create();
//        int defaultResultInventory = world.create();
//        world.edit(playerId).create(CraftingTableComponent.class).set(defaultCraftingTable, defaultResultInventory, CraftStrategy.craftingStrategyCraftingTable());
//        world.edit(defaultCraftingTable).create(InventoryComponent.class).set(2, 2, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
//        world.edit(defaultResultInventory).create(InventoryComponent.class).set(1, 1, InventoryComponent.DEFAULT_BACKGROUND_TEXTURE_NAME);
//        world.edit(defaultCraftingTable).create(CraftingComponent.class).set(RealmTechCoreMod.CRAFT, defaultResultInventory);
        players.add(playerId);
        logger.info("le joueur {} a été ajouté avec l'id dans le monde {}", channel.remoteAddress(), playerId);
    }
}
