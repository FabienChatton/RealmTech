package ch.realmtech.components;

import ch.realmtech.server.ecs.component.FaceComponent;
import com.artemis.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FaceComponentTest {

    private static World world;
    @BeforeAll
    public static void createWorld() {
        world = new World();
    }

    @Test
    void northSingleFace() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setNorth();
        assertTrue(faceComponent.isNorth());
    }

    @Test
    void northSingleEast() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setEast();
        assertTrue(faceComponent.isEast());
    }

    @Test
    void northSingleSouth() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setSouth();
        assertTrue(faceComponent.isSouth());
    }

    @Test
    void northSingleWest() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setWest();
        assertTrue(faceComponent.isWest());
    }

    @Test
    void singleFace() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setNorth();
        assertFalse(faceComponent.isMultiFace());
    }

    @Test
    void invertNorthFace() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setNorth();
        byte faceInverted = faceComponent.getFaceInverted();

        assertFalse(FaceComponent.isNorth(faceInverted));
        assertTrue(FaceComponent.isEast(faceInverted));
        assertTrue(FaceComponent.isSouth(faceInverted));
        assertTrue(FaceComponent.isWest(faceInverted));
    }

    @Test
    void invertNorthEast() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setEast();
        byte faceInverted = faceComponent.getFaceInverted();

        assertTrue(FaceComponent.isNorth(faceInverted));
        assertFalse(FaceComponent.isEast(faceInverted));
        assertTrue(FaceComponent.isSouth(faceInverted));
        assertTrue(FaceComponent.isWest(faceInverted));
    }

    @Test
    void invertNorthSouth() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setSouth();
        byte faceInverted = faceComponent.getFaceInverted();

        assertTrue(FaceComponent.isNorth(faceInverted));
        assertTrue(FaceComponent.isEast(faceInverted));
        assertFalse(FaceComponent.isSouth(faceInverted));
        assertTrue(FaceComponent.isWest(faceInverted));
    }

    @Test
    void invertNorthWest() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setWest();
        byte faceInverted = faceComponent.getFaceInverted();

        assertTrue(FaceComponent.isNorth(faceInverted));
        assertTrue(FaceComponent.isEast(faceInverted));
        assertTrue(FaceComponent.isSouth(faceInverted));
        assertFalse(FaceComponent.isWest(faceInverted));
    }

    @Test
    void setMultiFaceSingleFace() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class);
        faceComponent.setMultiFace(false);

        assertFalse(faceComponent.isMultiFace());
    }

    @Test
    void setMultiFace() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderMultiFace().addEast().addNorth().build();
        assertTrue(faceComponent.isEast());
        assertTrue(faceComponent.isNorth());
        assertFalse(faceComponent.isSouth());
        assertFalse(faceComponent.isWest());
        faceComponent.setMultiFace(false);

        assertFalse(faceComponent.isEast());
        assertFalse(faceComponent.isNorth());
        assertFalse(faceComponent.isSouth());
        assertFalse(faceComponent.isWest());

        assertFalse(faceComponent.isMultiFace());
    }

    @Test
    void setMultiFaceSingleMultiFace() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class);
        faceComponent.setMultiFace(true);

        assertTrue(faceComponent.isMultiFace());
    }

    @Test
    void canNotAddMultipleFaceOnSingle() {
        int entityId = world.create();
        FaceComponent faceComponent = world.edit(entityId).create(FaceComponent.class).builderSingleFace().setEast();
        faceComponent.addSouth();

        assertFalse(faceComponent.isSouth());
        assertTrue(faceComponent.isEast());
    }
}
