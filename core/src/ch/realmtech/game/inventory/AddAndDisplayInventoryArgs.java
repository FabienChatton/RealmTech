package ch.realmtech.game.inventory;

public record AddAndDisplayInventoryArgs(Runnable addTable, DisplayInventoryArgs[] args) {
}
