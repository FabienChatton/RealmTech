package ch.realmtech.game.clickAndDrop;

import com.badlogic.gdx.scenes.scene2d.Actor;

public final class ClickActorAndSlot {
    public Actor actor;
    public int[] stack;

    public ClickActorAndSlot(Actor actor, int[] slot) {
        this.actor = actor;
        this.stack = slot;
    }
}
