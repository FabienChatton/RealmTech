package ch.realmtech.server.ecs.plugin.commun;

import ch.realmtech.server.ecs.component.*;
import com.artemis.ArtemisPlugin;
import com.artemis.ComponentMapper;
import com.artemis.WorldConfigurationBuilder;

public abstract class MapperAdminCommun implements ArtemisPlugin {
    public ComponentMapper<Box2dComponent> mBox2d;
    public ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    public ComponentMapper<CraftingComponent> mCrafting;
    public ComponentMapper<CraftingTableComponent> mCraftingTable;
    public ComponentMapper<FurnaceComponent> mFurnace;
    public ComponentMapper<InfCellComponent> mCell;
    public ComponentMapper<InfChunkComponent> mChunk;
    public ComponentMapper<InfMapComponent> mMap;
    public ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    public ComponentMapper<InventoryComponent> mInventory;
    public ComponentMapper<ItemBeingPickComponent> mItemBeingPick;
    public ComponentMapper<ItemComponent> mItem;
    public ComponentMapper<ItemPickableComponent> mItemPickable;
    public ComponentMapper<ItemResultCraftComponent> mItemResult;
    public ComponentMapper<MovementComponent> mMovement;
    public ComponentMapper<PickerGroundItemComponent> mPickerGroundItem;
    public ComponentMapper<PlayerComponent> mPlayer;
    public ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    public ComponentMapper<PositionComponent> mPos;
    public ComponentMapper<ItemStoredComponent> mItemStored;
    public ComponentMapper<TextureAnimatedComponent> mTextureAnimated;
    public ComponentMapper<TextureComponent> mTexture;

    @Override
    public void setup(WorldConfigurationBuilder b) {

    }
}
