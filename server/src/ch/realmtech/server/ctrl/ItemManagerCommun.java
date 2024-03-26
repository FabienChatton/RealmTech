package ch.realmtech.server.ctrl;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.ItemEntry;
import com.artemis.Archetype;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.UUID;

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

    public static int createNewItem(World world, ItemEntry itemRegisterEntry, Archetype defaultItemGroundArchetype, UUID itemUuid) {
        int itemId;
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        itemId = world.create(defaultItemGroundArchetype);
        mItem.get(itemId).set(itemRegisterEntry);
        SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");
        systemsAdminCommun.getUuidEntityManager().registerEntityIdWithUuid(itemUuid, itemId);
        return itemId;
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
        fixtureDef.filter.maskBits = PhysiqueWorldHelper.BIT_WORLD | PhysiqueWorldHelper.BIT_GAME_OBJECT;
        itemBody.createFixture(fixtureDef);
        polygonShape.dispose();
        return itemBody;
    }

    public static void removeBox2dAndPosition(int itemId, ComponentMapper<Box2dComponent> mBox2d, com.badlogic.gdx.physics.box2d.World physicWorld, World world) {
        Box2dComponent box2dComponent = mBox2d.get(itemId);
        physicWorld.destroyBody(box2dComponent.body);
        world.edit(itemId).remove(Box2dComponent.class);
        world.edit(itemId).remove(PositionComponent.class);
    }
}
