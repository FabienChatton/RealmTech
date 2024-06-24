package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.MainPlayerComponent;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.core.screen.GameScreen;
import ch.realmtech.server.ecs.component.PlayerDeadComponent;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

@All(MainPlayerComponent.class)
@Exclude(PlayerDeadComponent.class)
public class PlayerGameInteractionSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;

    @Override
    protected void process(int entityId) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            context.withGameScreen((gameScreen) -> gameScreen.toggleDebugTable(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)));
        }
        // open inventory
        if (Gdx.input.isKeyJustPressed(InputMapper.openInventory.getKey())) {
            context.withGameScreen(GameScreen::openInventory);
        }

        // open quest
        if (Gdx.input.isKeyJustPressed(InputMapper.openQuest.getKey())) {
            context.withGameScreen(GameScreen::openQuestMenu);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            context.withGameScreen(GameScreen::openConsole);
        }

//        if (Gdx.input.isKeyJustPressed(context.getOption().keyDropItem.get())) {
//            context.getEcsEngine().dropCurentPlayerItem();
//        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 4);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 5);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 6);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 7);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9))
            context.getSystemsAdminClient().getItemBarSystem().setSlotSelected((byte) 8);
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP))
            context.getSystemsAdminClient().getItemBarSystem().slotSelectedUp();
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN))
            context.getSystemsAdminClient().getItemBarSystem().slotSelectedDown();
    }
}
