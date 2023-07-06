package ch.realmtech.game.clickAndDrop;


public interface ClickAndDropEvent {
    /** @return vrai si le click en drop commence avec cette actor*/
    boolean clickStart(ClickActorAndSlot actor);
    /** @return vrai si le click en drop prend fin */
    boolean clickStop(ClickActorAndSlot actorSrc, ClickActorAndSlot clickActorAndSlot);
}
