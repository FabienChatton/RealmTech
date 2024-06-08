package ch.realmtech.server.mod.factory;

import ch.realmtech.server.item.ItemBehavior;

public class ItemBehaviorCommun {

    /**
     * Weapon behavior with default a close range.
     * See {@link ItemBehaviorCommun#weapon(int, int, boolean)} to set a range, specially for a fire arm
     */
    public static ItemBehavior.ItemBehaviorBuilder weapon(int dommage) {
        return weapon(dommage, 4, false);
    }

    /**
     * Create a weapon. This will attack.
     */
    public static ItemBehavior.ItemBehaviorBuilder weapon(int dommage, int range, boolean isFirearm) {
        return ItemBehavior.builder()
                .setAttackDommage(dommage)
                .setAttackRange(range)
                .setFireArm(isFirearm)
                .leftClickOnJustPressed(ItemInteractionCommun.attack(!isFirearm));
    }

    public static ItemBehavior.ItemBehaviorBuilder eat(int heartToRestore) {
        return ItemBehavior.builder()
                .eatRestore(heartToRestore)
                .rightClickOnJustPressed(ItemInteractionCommun.eat());
    }
}
