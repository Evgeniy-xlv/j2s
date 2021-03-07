package org.j2s;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface J2SLibrary {

    String name() default "example";

    String version() default "1.0.0";

    String description() default "Empty";
}
