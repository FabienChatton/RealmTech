package ch.realmtech.game.clickAndDrop;


import com.badlogic.gdx.scenes.scene2d.InputEvent;

public interface ClickAndDropEvent {
    /** @return vrai si le click en drop commence avec cette actor*/
    ImageItemTable clickStart(final ClickActorAndSlot actor, final int[] stackActive, final InputEvent event);
    /** @return null si le click prend fin sinon le nouvel acteur à affich */
    ImageItemTable clickStop(final ClickActorAndSlot clickActorAndSlotSrc, final int[] stackActive, final ClickActorAndSlot clickActorAndSlotDst, int button);
}
