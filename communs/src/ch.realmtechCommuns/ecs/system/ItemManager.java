package ch.realmtechCommuns.ecs.system;

import ch.realmtechCommuns.ecs.component.Box2dComponent;
import ch.realmtechCommuns.ecs.component.ItemComponent;
import ch.realmtechCommuns.ecs.component.PositionComponent;
import ch.realmtechCommuns.ecs.component.TextureComponent;
import ch.realmtechCommuns.registery.ItemRegisterEntry;
import ch.realmtechCommuns.PhysiqueWorldHelper;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public class ItemManager extends Manager {
    private final static String TAG = ItemManager.class.getSimpleName();
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private TextureAtlas textureAtlas;
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    private Archetype defaultItemGroundArchetype;
    private Archetype defaultItemInventoryArchetype;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemGroundArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(PositionComponent.class)
                .add(TextureComponent.class)
                .add(Box2dComponent.class)
                .build(world);
        defaultItemInventoryArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(TextureComponent.class)
                .build(world);
    }

    /**
     * Permet de faire apparaitre un nouvel item sur la map.
     * @param worldPosX La position X dans le monde du nouvel item.
     * @param worldPosY La position Y dans le monde du nouvel item.
     * @param itemRegisterEntry Le register qui permettra de créer l'item.
     */
    public void newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry) {
        final int itemId = createNewItem(itemRegisterEntry, defaultItemGroundArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        setItemTexturePositionAndPhysicBody(itemId, textureComponent.texture = itemRegisterEntry.getTextureRegion(), worldPosX, worldPosY);
        //textureComponent.scale = RealmTech.UNITE_SCALE;
    }

    public int newItemInventory(ItemRegisterEntry itemRegisterEntry) {
        final int itemId = createNewItem(itemRegisterEntry, defaultItemInventoryArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        return itemId;
    }

    private int createNewItem(ItemRegisterEntry itemRegisterEntry, Archetype defaultItemGroundArchetype) {
        final int itemId;
        if (itemRegisterEntry.getArchetype() != null) {
            itemId = world.create(itemRegisterEntry.getArchetype());
        } else {
            itemId = world.create(defaultItemGroundArchetype);
        }
        return itemId;
    }

    public void setItemTexturePositionAndPhysicBody(int itemId, TextureRegion texture, float worldPosX, float worldPosY) {
        PositionComponent positionComponent = world.edit(itemId).create(PositionComponent.class);
        positionComponent.set(worldPosX, worldPosY);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        textureComponent.set(texture);
        Box2dComponent box2dComponent = world.edit(itemId).create(Box2dComponent.class);
        Body itemBody = createBox2dItem(itemId, worldPosX, worldPosY, textureComponent.texture);
        box2dComponent.set(
                1,//textureComponent.texture.getRegionWidth() / RealmTech.PPM,
                1,//textureComponent.texture.getRegionHeight() / RealmTech.PPM,
                itemBody
        );
    }

    public Body createBox2dItem(int itemId, float worldX, float worldY, TextureRegion texture) {
        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.position.set(worldX, worldY);
        bodyDef.gravityScale = 0;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body itemBody = physicWorld.createBody(bodyDef);
        itemBody.setUserData(itemId);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(texture.getRegionWidth() / 1 /*RealmTech.PPM*/, texture.getRegionHeight() / 1 /*RealmTech.PPM*/);
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT | PhysiqueWorldHelper.BIT_PLAYER;
        itemBody.createFixture(fixtureDef);
        polygonShape.dispose();
        return itemBody;
    }

    public void playerPickUpItem(int itemId, int playerId) {
        world.edit(itemId).remove(Box2dComponent.class);
        world.edit(itemId).remove(PositionComponent.class);
        world.getSystem(InventoryManager.class).addItemToInventory(itemId, playerId);
    }
}
