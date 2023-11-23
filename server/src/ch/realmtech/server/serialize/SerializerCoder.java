package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.World;

public interface SerializerCoder<InputType, OutputType> {
    SerializedApplicationBytes encode(World world, InputType toSerialize);
    OutputType decode(World world, SerializedApplicationBytes applicationBytes);
}
