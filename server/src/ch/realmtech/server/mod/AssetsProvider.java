package ch.realmtech.server.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determine if a mod has a texture atlas.
 * The name of your texture atlas and the texture must have the same filename as your modId.
 * The texture atlas and the texture must be located in the assets folder.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssetsProvider {
}
