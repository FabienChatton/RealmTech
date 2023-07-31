package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.Box2dComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PositionComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

public class ItemManager extends Manager {
    private final static String TAG = ItemManager.class.getSimpleName();
    @Wire(name ="context")
    RealmTech context;
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
        setItemTexturePositionAndPhysicBody(itemId, textureComponent.texture = itemRegisterEntry.getTextureRegion(context), worldPosX, worldPosY);
        textureComponent.scale = RealmTech.UNITE_SCALE;
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
        Body itemBody = context.getEcsEngine().createBox2dItem(itemId, worldPosX, worldPosY, textureComponent.texture);
        box2dComponent.set(
                textureComponent.texture.getRegionWidth() / RealmTech.PPM,
                textureComponent.texture.getRegionHeight() / RealmTech.PPM,
                itemBody
        );
    }

    public void playerPickUpItem(int itemId, int playerId) {
        world.edit(itemId).remove(Box2dComponent.class);
        world.edit(itemId).remove(PositionComponent.class);
        world.getSystem(InventoryManager.class).addItemToInventory(itemId, playerId);
    }

    public boolean dropCurentPlayerItem() {
        ItemComponent itemComponent = world.getSystem(ItemBarManager.class).getSelectItemComponent();
        if (itemComponent != null) {
            world.getSystem(InventoryManager.class).removeOneItem(world.getSystem(ItemBarManager.class).getSelectStack());
            Vector3 gameCoo = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            world.getSystem(ItemManager.class).newItemOnGround(gameCoo.x, gameCoo.y, itemComponent.itemRegisterEntry);
            world.getSystem(SoundManager.class).playItemDrop();
            return true;
        } else {
            return false;
        }
    }
}
