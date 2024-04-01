package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.TextureImportantComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.UUID;

public class MobManagerClient extends Manager {
    @Wire(name = "context")
    private RealmTech context;

    @Wire(name = "systemsAdmin")
    private SystemsAdminClient systemsAdminClient;

    public void mobAttackCoolDown(UUID mobUuid, int cooldown) {
        int mobId = systemsAdminClient.getUuidEntityManager().getEntityId(mobUuid);
        TextureAtlas.AtlasRegion texture = context.getTextureAtlas().findRegion("zombie-0");
        world.edit(mobId).create(TextureImportantComponent.class).set(new TextureRegion[]{texture}, cooldown, cooldown);
    }
}
