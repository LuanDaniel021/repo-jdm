package com.jdm.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.geometry.VPos;
import javafx.scene.layout.Priority;

@Repeatable(Rows.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Row {
    double prefHeight() default Double.NaN;
    double minHeight() default Double.NaN;
    double maxHeight() default Double.NaN;
    double percentHeight() default Double.NaN;
    Priority vgrow() default Priority.NEVER;
    VPos valign() default VPos.CENTER;
    boolean fillHeight() default true;
}