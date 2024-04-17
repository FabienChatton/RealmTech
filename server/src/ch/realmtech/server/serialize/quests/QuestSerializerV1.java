package ch.realmtech.server.serialize.quests;

import ch.realmtech.server.ecs.component.QuestPlayerPropertyComponent;
import ch.realmtech.server.mod.quests.QuestManagerEntry;
import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.Wire;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class QuestSerializerV1 implements Serializer<Integer, List<QuestPlayerProperty>> {
    @Wire(name = "rootRegistry")
    private Registry<?> rootRegistry;
    private ComponentMapper<QuestPlayerPropertyComponent> mQuestPlayerProperty;

    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer playerToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, playerToSerialize));
        List<QuestPlayerProperty> questPlayerProperties = mQuestPlayerProperty.get(playerToSerialize).getQuestPlayerProperties();
        List<QuestPlayerProperty> completedQuestPlayerProperties = questPlayerProperties.stream().filter(QuestPlayerProperty::isCompleted).toList();

        int size = completedQuestPlayerProperties.size();
        buffer.writeInt(size);
        for (QuestPlayerProperty completedQuestPlayerProperty : completedQuestPlayerProperties) {
            int questId = completedQuestPlayerProperty.getQuestEntry().getId();
            long completedTimestamp = completedQuestPlayerProperty.getCompletedTimestamp();

            buffer.writeInt(questId);
            buffer.writeLong(completedTimestamp);
        }
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public List<QuestPlayerProperty> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        int size = buffer.readInt();
        List<QuestPlayerProperty> completedQuestPlayerProperties = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int questId = buffer.readInt();
            long completedTimestamp = buffer.readLong();

            QuestEntry questEntry = RegistryUtils.findEntryUnsafe(rootRegistry, questId);
            completedQuestPlayerProperties.add(new QuestPlayerProperty(questEntry, true, completedTimestamp));
        }

        List<? extends QuestEntry> allQuests = RegistryUtils.findEntryOrThrow(rootRegistry, QuestManagerEntry.class).getQuests();
        int totalQuestSize = allQuests.size();
        List<QuestPlayerProperty> unCompletedQuestPlayerProperties = new ArrayList<>(totalQuestSize - size);
        List<? extends QuestEntry> unCompletedQuestEntry = allQuests.stream().filter((questEntry) -> completedQuestPlayerProperties.stream().map(QuestPlayerProperty::getQuestEntry).toList().contains(questEntry)).toList();
        for (QuestEntry questEntry : unCompletedQuestEntry) {
            unCompletedQuestPlayerProperties.add(new QuestPlayerProperty(questEntry));
        }
        return Stream.concat(completedQuestPlayerProperties.stream(), unCompletedQuestPlayerProperties.stream()).toList();
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer playerToSerialize) {
        List<QuestPlayerProperty> questPlayerProperties = mQuestPlayerProperty.get(playerToSerialize).getQuestPlayerProperties();
        List<QuestPlayerProperty> completedQuestPlayerProperties = questPlayerProperties.stream().filter(QuestPlayerProperty::isCompleted).toList();

        int size = completedQuestPlayerProperties.size();

        int sizeLength = Integer.BYTES;
        int coreLength = /* quest id */ Integer.BYTES + /* timestamp */ +Long.BYTES;
        return sizeLength + (size * coreLength);
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
