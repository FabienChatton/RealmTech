package ch.realmtech.server.level.cell;

import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.mod.PlayerFootStepSound;
import ch.realmtech.server.mod.RealmTechCoreMod;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CellBehavior {
    private ItemType breakWith;
    private float speedEffect = 1;
    private PlayerFootStepSound playerFootStepSound;
    private final byte layer;
    private BreakCell breakCellEvent;
    private Supplier<BreakCell> getBreakCellEvent;
    private int breakStepNeed = 20;
    private CreatePhysiqueBody createBody;
    private BiConsumer<com.badlogic.gdx.physics.box2d.World, Body> deleteBody;
    private EditEntity defaultEditEntityArgs;
    private BiConsumer<ClientContext, Integer> interagieClickDroit;

    public static CellBehaviorBuilder builder(byte layer) {
        return new CellBehaviorBuilder(layer);
    }

    public static CellBehaviorBuilder builder(Cells.Layer layer) {
        return builder(layer.layer);
    }

    private CellBehavior(byte layer) {
        this.layer = layer;
    }

    public ItemType getBreakWith() {
        return breakWith;
    }

    public float getSpeedEffect() {
        return speedEffect;
    }

    public PlayerFootStepSound getPlayerFootStepSound() {
        return playerFootStepSound;
    }

    public byte getLayer() {
        return layer;
    }

    public BreakCell getBreakCellEvent() {
        if (breakCellEvent == null && getBreakCellEvent != null) breakCellEvent = getBreakCellEvent.get();
        return breakCellEvent != null ? breakCellEvent : BreakCellEvent.dropNothing();
    }

    public int getBreakStepNeed() {
        return breakStepNeed;
    }

    public Optional<EditEntity> getDefaultEditEntityArgs() {
        return Optional.ofNullable(defaultEditEntityArgs);
    }


    public CreatePhysiqueBody getCreateBody() {
        return createBody;
    }

    public BiConsumer<World, Body> getDeleteBody() {
        return deleteBody;
    }

    public BiConsumer<ClientContext, Integer> getInteragieClickDroit() {
        return interagieClickDroit;
    }

    public static class CellBehaviorBuilder {
        private final CellBehavior cellBehavior;

        public CellBehaviorBuilder(byte layer) {
            cellBehavior = new CellBehavior(layer);
        }

        public CellBehaviorBuilder(Cells.Layer ground) {
            this(ground.layer);
        }

        public CellBehaviorBuilder breakWith(ItemType itemType) {
            cellBehavior.breakWith = itemType;
            return this;
        }

        public CellBehaviorBuilder breakWith(ItemType itemType, String dropItemRegistryName) {
            breakWith(itemType);
            dropOnBreak(dropItemRegistryName);
            return this;
        }

        public CellBehaviorBuilder dropOnBreak(String dropItemRegistryName) {
            cellBehavior.getBreakCellEvent = () -> BreakCellEvent.dropOnBreak(RealmTechCoreMod.ITEMS.get(dropItemRegistryName).getEntry());
            return this;
        }

        public CellBehaviorBuilder dropNothing() {
            cellBehavior.getBreakCellEvent = BreakCellEvent::dropNothing;
            return this;
        }

        public CellBehaviorBuilder speedEffect(float speedEffect) {
            cellBehavior.speedEffect = speedEffect;
            return this;
        }

        public CellBehaviorBuilder playerWalkSound(String soundEffectName, float volume) {
            cellBehavior.playerFootStepSound = new PlayerFootStepSound(soundEffectName, volume);
            return this;
        }

        public CellBehaviorBuilder editEntity(EditEntity defaultEditEntityArgs) {
            cellBehavior.defaultEditEntityArgs = defaultEditEntityArgs;
            return this;
        }

        /**
         * Par défaut, 20
         */
        public CellBehaviorBuilder breakStepNeed(int steepNeed) {
            cellBehavior.breakStepNeed = steepNeed;
            return this;
        }

        public CellBehaviorBuilder physiqueBody(CreatePhysiqueBody.CreatePhysiqueBodyArgs createPhysiqueBody) {
            cellBehavior.createBody = createPhysiqueBody.createPhysiqueBody();
            cellBehavior.deleteBody = createPhysiqueBody.deletePhysiqueBody();
            return this;
        }

        public CellBehaviorBuilder interagieClickDroit(BiConsumer<ClientContext, Integer> interagieClickDroit) {
            cellBehavior.interagieClickDroit = interagieClickDroit;
            return this;
        }

        public CellBehavior build() {
            return cellBehavior;
        }
    }
}
