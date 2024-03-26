package ch.realmtech.server.mod;

import ch.realmtech.server.registry.Entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Evaluate this entry after the specified entry.
 * Used to have registry dependency evaluated.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EvaluateAfter {
    /**
     * Specify a fqrn entry or tags to evaluate after these entries
     */
    String[] value() default {};

    /**
     * Specify a class entry to evaluate after this class
     */
    Class<? extends Entry>[] classes() default {};
}
