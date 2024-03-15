package ch.realmtech.server.newMod;

import ch.realmtech.server.newRegistry.NewEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EvaluateBefore {
    String[] value() default {};

    Class<? extends NewEntry>[] classes() default {};
}
