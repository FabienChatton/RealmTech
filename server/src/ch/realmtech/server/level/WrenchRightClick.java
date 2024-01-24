package ch.realmtech.server.level;

import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.packet.serverPacket.RotateFaceCellRequestPacket;

public final class WrenchRightClick {
    public static RightClickInteractionItemClient wrenchRightClick() {
        return (clientContext, event, itemId, cellTargetId) -> {
            if (clientContext.getWorld().getMapper(FaceComponent.class).has(cellTargetId)) {
                FaceComponent faceComponent = clientContext.getWorld().getMapper(FaceComponent.class).get(cellTargetId);
                if (!faceComponent.isMultiFace()) {
                    float texture01coordinateX = MapManager.getTexture01coordinate(event.gameCoordinateX());
                    float texture01coordinateY = MapManager.getTexture01coordinate(event.gameCoordinateY());
                    int worldPosX = MapManager.getWorldPos(event.gameCoordinateX());
                    int worldPosY = MapManager.getWorldPos(event.gameCoordinateY());
                    byte face;
                    if (texture01coordinateX < 0.3f) {
                        face = FaceComponent.WEST;
                    } else if (texture01coordinateX > 0.7f) {
                        face = FaceComponent.EAST;
                    } else if (texture01coordinateY > 0.7f) {
                        face = FaceComponent.NORTH;
                    } else {
                        face = FaceComponent.SOUTH;
                    }
                    clientContext.sendRequest(new RotateFaceCellRequestPacket(worldPosX, worldPosY, RealmTechCoreMod.ENERGY_BATTERY.cellRegisterEntry().getCellBehavior().getLayer(), face));
                }
            }
        };
    }
}
