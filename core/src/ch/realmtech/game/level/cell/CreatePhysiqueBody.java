package ch.realmtech.game.level.cell;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public interface CreatePhysiqueBody {
    record CreatePhysiqueBodyReturn(Body body, float with, float height) {

    }

    CreatePhysiqueBodyReturn createPhysiqueBody(World physiqueWorldCreate, BodyDef bodyDef, FixtureDef fixtureDef, int worldPosX, int worldPosY);
}
