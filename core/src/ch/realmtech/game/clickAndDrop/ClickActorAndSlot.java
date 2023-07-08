package ch.realmtech.game.clickAndDrop;

public final class ClickActorAndSlot {
    public ImageItemTable actor;

    public ClickActorAndSlot(ImageItemTable actor) {
        this.actor = actor;
    }

    public int[] getStack() {
        return actor.getStack();
    }
}
