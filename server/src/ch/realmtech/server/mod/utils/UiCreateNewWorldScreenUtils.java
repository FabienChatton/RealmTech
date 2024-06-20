package ch.realmtech.server.mod.utils;

import ch.realmtech.server.level.worldGeneration.SeedGenerator;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;

import java.nio.charset.StandardCharsets;

public class UiCreateNewWorldScreenUtils extends Entry {
    public UiCreateNewWorldScreenUtils() {
        super("UiCreateNewWorldScreenUtils");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
    }

    /**
     * Return a seed from a string. If the string is a valide long, the seed will be the value
     * parsed as a long. If the string is an invalide long. the seed will be some of the string as bytes.
     * If the string is null or blank, the seed will be a random long number.
     *
     * @param seedString The seed as a string.
     * @return The corresponding seed value.
     */
    public long parseTextFieldToSeed(String seedString) {
        long seed;
        if (seedString != null && !seedString.isBlank()) {
            try {
                seed = Long.parseLong(seedString);
            } catch (NumberFormatException e) {
                byte[] seedFieldBytes = seedString.getBytes(StandardCharsets.UTF_8);
                long somme = 0;
                for (byte seedFieldByte : seedFieldBytes) {
                    somme += seedFieldByte;
                }
                seed = somme;
            }
        } else {
            seed = SeedGenerator.randomSeed();
        }

        return seed;
    }
}
