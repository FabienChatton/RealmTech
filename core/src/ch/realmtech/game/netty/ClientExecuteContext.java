package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtech.screen.ScreenType;
import ch.realmtechCommuns.packet.clientPacket.ClientExecute;
import com.badlogic.gdx.Gdx;

public class ClientExecuteContext implements ClientExecute {
    private final RealmTech context;

    public ClientExecuteContext(RealmTech context) {
        this.context = context;
    }

    @Override
    public void setScreenToGameScreen() {
        Gdx.app.postRunnable(() -> context.setScreen(ScreenType.GAME_SCREEN));
    }
}
