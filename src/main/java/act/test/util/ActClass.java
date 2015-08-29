package act.test.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines prefix patterns which {@code ActTestClassLoader} will load
 * in a separate ClassLoader.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActClass {
    /**
     * @return prefix patterns which {@code ActTestClassLoader} will load in a separate ClassLoader
     */
    public String[] value() default {};
}