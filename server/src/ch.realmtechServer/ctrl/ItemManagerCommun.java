package ch.realmtechServer.ctrl;

import ch.realmtechServer.PhysiqueWorldHelper;
import ch.realmtechServer.ecs.component.Box2dComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import ch.realmtechServer.ecs.system.InventoryManager;
import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.Archetype;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class ItemManagerCommun {
    public static void setItemPositionAndPhysicBody(World world, com.badlogic.gdx.physics.box2d.World physicWorld, BodyDef bodyDef, FixtureDef fixtureDef, int itemId, float worldPosX, float worldPosY, float widthWorld, float heightWorld) {
        PositionComponent positionComponent = world.edit(itemId).create(PositionComponent.class);
        positionComponent.set(worldPosX, worldPosY);
        Box2dComponent box2dComponent = world.edit(itemId).create(Box2dComponent.class);
        Body itemBody = createBox2dItem(physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, widthWorld, heightWorld);
        box2dComponent.set(
                widthWorld,
                heightWorld,
                itemBody
        );
    }

    public static int createNewItem(World world, ItemRegisterEntry itemRegisterEntry, Archetype defaultItemGroundArchetype) {
        final int itemId;
        if (itemRegisterEntry.getArchetype() != null) {
            itemId = world.create(itemRegisterEntry.getArchetype());
        } else {
            itemId = world.create(defaultItemGroundArchetype);
        }
        return itemId;
    }

    public static void playerPickUpItem(World world, int itemId, int playerId) {
        world.edit(itemId).remove(Box2dComponent.class);
        world.edit(itemId).remove(PositionComponent.class);
        world.getSystem(InventoryManager.class).addItemToInventory(itemId, playerId);
    }

    private static Body createBox2dItem(com.badlogic.gdx.physics.box2d.World physicWorld, BodyDef bodyDef, FixtureDef fixtureDef, int itemId, float worldX, float worldY, float widthWorld, float heightWorld) {
        PhysiqueWorldHelper.resetBodyDef(bodyDef);
        PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        bodyDef.position.set(worldX, worldY);
        bodyDef.gravityScale = 0;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body itemBody = physicWorld.createBody(bodyDef);
        itemBody.setUserData(itemId);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(widthWorld, heightWorld);
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_GAME_OBJECT;
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT | PhysiqueWorldHelper.BIT_PLAYER;
        itemBody.createFixture(fixtureDef);
        polygonShape.dispose();
        return itemBody;
    }
}
