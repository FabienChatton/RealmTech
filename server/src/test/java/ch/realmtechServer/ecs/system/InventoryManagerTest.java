package ch.realmtechServer.ecs.system;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class InventoryManagerTest {

    @Test
    void removeOneItem() {
        int[] stackOriginal = new int[]{1,2,3,4};
        InventoryManager.removeItemInStack(stackOriginal, 2);
        int[] stackExpected = new int[]{1,3,4,0};
        assertArrayEquals(stackOriginal, stackExpected);

    }
}