package com.jdm.meta;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Inherited
@Retention(RUNTIME)
@Target({FIELD,ElementType.TYPE})
public @interface Image {
	String url() default "";
	double width() default -1;
	double height() default -1;
	boolean preserve_ratio() default false;
	boolean smooth() default false;
}
