package ch.realmtech.server.craft;

import java.util.Optional;
import java.util.function.IntFunction;

public interface CraftChange extends IntFunction<Optional<Optional<CraftResult>>> {

}
