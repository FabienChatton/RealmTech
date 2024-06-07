package ch.realmtech.server.mod.items;

import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.item.ItemBehavior;
import ch.realmtech.server.level.ClickInteractionItemClient;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.packet.serverPacket.RotateFaceCellRequestPacket;
import ch.realmtech.server.registry.ItemEntry;

public class WrenchItemEntry extends ItemEntry {
    public WrenchItemEntry() {
        super("Wrench", "wrench-01", ItemBehavior.builder()
                .rightClickOnJustPressed(rotateBlock())
                .build());
    }

    public static ClickInteractionItemClient rotateBlock() {
        return (clientContext, event, cellId, itemId) -> {
            if (clientContext.getWorld().getMapper(FaceComponent.class).has(cellId)) {
                FaceComponent faceComponent = clientContext.getWorld().getMapper(FaceComponent.class).get(cellId);
                float texture01coordinateX = MapManager.getTexture01coordinate(event.gameCoordinateX());
                float texture01coordinateY = MapManager.getTexture01coordinate(event.gameCoordinateY());
                int worldPosX = MapManager.getWorldPos(event.gameCoordinateX());
                int worldPosY = MapManager.getWorldPos(event.gameCoordinateY());
                if (!faceComponent.isMultiFace()) {
                    byte face;
                    if (texture01coordinateX < 0.3f) {
                        face = FaceComponent.WEST;
                    } else if (texture01coordinateX > 0.7f) {
                        face = FaceComponent.EAST;
                    } else if (texture01coordinateY > 0.7f) {
                        face = FaceComponent.NORTH;
                    } else if (texture01coordinateY < 0.3f) {
                        face = FaceComponent.SOUTH;
                    } else {
                        return;
                    }
                    clientContext.sendRequest(new RotateFaceCellRequestPacket(worldPosX, worldPosY, Cells.Layer.BUILD_DECO.layer, face));
                } else {
                    byte face = getFace(texture01coordinateX, faceComponent, texture01coordinateY);
                    if (face != -1) {
                        clientContext.sendRequest(new RotateFaceCellRequestPacket(worldPosX, worldPosY, Cells.Layer.BUILD_DECO.layer, face));
                    }
                }
            }
        };
    }

    private static byte getFace(float texture01coordinateX, FaceComponent faceComponent, float texture01coordinateY) {
        byte face;
        if (texture01coordinateX < 0.3f) {
            if (faceComponent.isWest()) {
                face = (byte) (faceComponent.getFace() & ~FaceComponent.WEST);
            } else {
                face = (byte) (faceComponent.getFace() | FaceComponent.WEST);
            }
        } else if (texture01coordinateX > 0.7f) {
            if (faceComponent.isEast()) {
                face = (byte) (faceComponent.getFace() & ~FaceComponent.EAST);
            } else {
                face = (byte) (faceComponent.getFace() | FaceComponent.EAST);
            }
        } else if (texture01coordinateY > 0.7f) {
            if (faceComponent.isNorth()) {
                face = (byte) (faceComponent.getFace() & ~FaceComponent.NORTH);
            } else {
                face = (byte) (faceComponent.getFace() | FaceComponent.NORTH);
            }
        } else if (texture01coordinateY < 0.3f) {
            if (faceComponent.isSouth()) {
                face = (byte) (faceComponent.getFace() & ~FaceComponent.SOUTH);
            } else {
                face = (byte) (faceComponent.getFace() | FaceComponent.SOUTH);
            }
        } else {
            return -1;
        }
        return face;
    }

    @Override
    public int getId() {
        return 384462994;
    }
}
