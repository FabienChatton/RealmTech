package ch.realmtech.server.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determine if a mod has a texture atlas.
 * Name your texture atlas and texture with the mod id and put them in the assets folder.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssetsProvider {
}
