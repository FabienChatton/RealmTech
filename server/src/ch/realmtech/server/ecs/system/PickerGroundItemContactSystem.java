package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.PlayerPickUpItem;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.UUID;

@All(PickerGroundItemComponent.class)
public class PickerGroundItemContactSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private ItemManagerServer itemManagerServer;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<ItemPickableComponent> mItemPickable;
    private ComponentMapper<ItemBeingPickComponent> mItemBeingPick;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    @Override
    protected void process(int entityId) {
        Box2dComponent pickerBox2dComponent = mBox2d.get(entityId);
        IntBag itemsEntities = world.getAspectSubscriptionManager().get(Aspect.all(ItemPickableComponent.class)).getEntities();
        Vector2 pickerWorldCenter = pickerBox2dComponent.body.getWorldCenter();
        float pickerX = pickerWorldCenter.x - pickerBox2dComponent.widthWorld / 2f;
        float pickerY = pickerWorldCenter.y - pickerBox2dComponent.heightWorld / 2f;

        Rectangle playerRectangle = new Rectangle(pickerX, pickerY, pickerBox2dComponent.widthWorld, pickerBox2dComponent.heightWorld);
        int[] itemsData = itemsEntities.getData();
        for (int i = 0; i < itemsEntities.size(); i++) {
            int itemId = itemsData[i];
            Box2dComponent itemBox2dComponent = mBox2d.get(itemId);
            if (itemBox2dComponent == null || itemBox2dComponent.body == null) return;
            Vector2 itemWorldCenter = itemBox2dComponent.body.getWorldCenter();
            float itemX = itemWorldCenter.x - itemBox2dComponent.widthWorld / 2f;
            float itemY = itemWorldCenter.y - itemBox2dComponent.heightWorld / 2f;
            Rectangle itemRectangle = new Rectangle(itemX, itemY, itemBox2dComponent.widthWorld, itemBox2dComponent.heightWorld);
            if (itemRectangle.overlaps(playerRectangle)) {
                itemManagerServer.playerPickUpItem(itemId, entityId);
                mItemPickable.remove(itemId);
                mItemBeingPick.remove(itemId);
                if (mPlayerConnexion.has(entityId)) {
                    UUID playerUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId);
                    serverContext.getServerConnexion().broadCastPacket(new PlayerPickUpItem(playerUuid));
                }
            }
        }
    }
}
