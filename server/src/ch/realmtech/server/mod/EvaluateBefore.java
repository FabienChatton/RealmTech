package ch.realmtech.server.mod;

import ch.realmtech.server.registry.Entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EvaluateBefore {
    String[] value() default {};

    Class<? extends Entry>[] classes() default {};
}
