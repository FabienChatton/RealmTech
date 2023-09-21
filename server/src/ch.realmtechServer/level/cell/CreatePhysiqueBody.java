package ch.realmtechServer.level.cell;

import ch.realmtechServer.PhysiqueWorldHelper;
import com.badlogic.gdx.physics.box2d.*;

import java.util.function.BiConsumer;

public interface CreatePhysiqueBody {
    record CreatePhysiqueBodyArgs(CreatePhysiqueBody createPhysiqueBody, BiConsumer<World, Body> deletePhysiqueBody) {

    }

    record CreatePhysiqueBodyReturn(Body body, float with, float height) {

    }

    CreatePhysiqueBodyReturn createPhysiqueBody(World physiqueWorldCreate, BodyDef bodyDef, FixtureDef fixtureDef, int worldPosX, int worldPosY);

    static CreatePhysiqueBody defaultPhysiqueBodyCreate() {
        return (physiqueWorldCreate, bodyDef, fixtureDef, worldPosX, worldPosY) -> {
            PolygonShape polygonShape = new PolygonShape();
            float size = 1f / 2f;
            polygonShape.setAsBox(size, size);

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(worldPosX + size, worldPosY + size);
            Body body = physiqueWorldCreate.createBody(bodyDef);
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = PhysiqueWorldHelper.BIT_WORLD;
            fixtureDef.filter.maskBits = -1;
            body.createFixture(fixtureDef);

            polygonShape.dispose();
            return new CreatePhysiqueBody.CreatePhysiqueBodyReturn(body, size, size);
        };
    }

    static BiConsumer<World, Body> defaultPhysiqueBodyDelete() {
        return World::destroyBody;
    }

    static CreatePhysiqueBodyArgs defaultPhysiqueBody() {
        return new CreatePhysiqueBodyArgs(defaultPhysiqueBodyCreate(), defaultPhysiqueBodyDelete());
    }
}
