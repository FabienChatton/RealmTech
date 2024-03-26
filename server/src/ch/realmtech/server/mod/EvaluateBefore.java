package ch.realmtech.server.mod;

import ch.realmtech.server.registry.Entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Evaluate this entry before the specified entry.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EvaluateBefore {
    /**
     * Specify a fqrn entry or tags to evaluate before these entries
     */
    String[] value() default {};

    /**
     * Specify a class entry to evaluate before this class
     */
    Class<? extends Entry>[] classes() default {};
}
