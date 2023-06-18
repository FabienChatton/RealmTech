package ch.realmtech.game.listener;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class GameWorldInputListener implements Subcriber<InputMapper.PointerMapper> {
    private final RealmTech context;
    public GameWorldInputListener(RealmTech context) {
        this.context = context;
    }

    @Override
    public void receive(InputMapper.PointerMapper pointerMapper) {
        // supprime ou place cellule sur la carte
        if (pointerMapper.isPressed) {
            Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (pointerMapper.pointer == 0) {
                if (pointerMapper.button == InputMapper.leftClick.button) {
                    throw new RuntimeException("à implémenté");
                }
                if (pointerMapper.button == InputMapper.rightClick.button) {
                    throw new RuntimeException("à implémenté");
                }
            }
        }
    }
}
