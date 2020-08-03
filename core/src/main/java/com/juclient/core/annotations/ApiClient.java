package com.juclient.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiClient {
    /**
     * By default JUClient will generate client methods in the same class signature from where they were extracted, if
     * you want to put them into the class of your choice then pass the name of the class in <b>group</b>
     */
    String group() default "";
}
