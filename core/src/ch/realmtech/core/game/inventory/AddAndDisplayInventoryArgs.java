package ch.realmtech.core.game.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.function.Consumer;

public record AddAndDisplayInventoryArgs(Consumer<Window> addTable, DisplayInventoryArgs[] args) {
}
