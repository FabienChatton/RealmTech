package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.screen.GameScreen;
import ch.realmtech.server.ecs.component.PlayerDeadComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;

public class PlayerDeadClientManager extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<PlayerDeadComponent> mPlayerDead;
    private boolean hasDeadMessageShow;

    @Override
    protected void processSystem() {
        int mainPlayerId = context.getSystemsAdminClient().getPlayerManagerClient().getMainPlayer();
        if (hasDeadMessageShow) {
            if (!mPlayerDead.has(mainPlayerId)) {
                hasDeadMessageShow = false;
                hideHeadMessage();
            }
        } else {
            if (mPlayerDead.has(mainPlayerId)) {
                hasDeadMessageShow = true;
                showDeadMessage();
            }
        }
    }

    public void showDeadMessage() {
        context.withGameScreen(GameScreen::showDeadMessage);
    }

    public void hideHeadMessage() {
        context.withGameScreen(GameScreen::hideDeadMessage);
    }
}
