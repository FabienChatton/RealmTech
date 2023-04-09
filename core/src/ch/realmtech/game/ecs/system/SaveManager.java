package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.PositionComponent;
import ch.realmtech.game.ecs.component.SaveComponent;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.All;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;


@All(SaveComponent.class)
public class SaveManager extends EntitySystem {

    private ComponentMapper<SaveComponent> mSave;

    @Override
    protected void processSystem() {

    }

    public void saveWorldMap(int saveEntityId) throws IOException {
        SaveComponent saveComponent = mSave.get(saveEntityId);
        final int version = 3;
        if (saveComponent.file.exists()) {
            Files.newBufferedWriter(saveComponent.file.toPath(), StandardOpenOption.TRUNCATE_EXISTING).close();
        }

        try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveComponent.file)))) {
            outputStream.write("RealmTech".getBytes());
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(version).array());
            outputStream.write(ByteBuffer.allocate(Long.BYTES).putLong(System.currentTimeMillis()).array());
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(RealmTechTiledMap.WORLD_WITH).array());
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(RealmTechTiledMap.WORLD_HIGH).array());
            outputStream.write(ByteBuffer.allocate(Byte.BYTES).put(GameChunk.NUMBER_LAYER).array());
            outputStream.write(ByteBuffer.allocate(Long.BYTES).putLong(saveComponent.context.getWorldMapManager().getSeed()).array());
            Entity player = saveComponent.context.getPlayer();
            PositionComponent playerPosition = player.getComponent(PositionComponent.class);
            outputStream.write(ByteBuffer.allocate(Float.BYTES).putFloat(playerPosition.x).array());
            outputStream.write(ByteBuffer.allocate(Float.BYTES).putFloat(playerPosition.y).array());
            saveComponent.context.getWorldMapManager().saveWorldMap(outputStream);
            outputStream.flush();
        }
    }
}
