package cc.unknown.event.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cc.unknown.event.Priority;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EventLink {
    byte value() default Priority.MEDIUM;
}