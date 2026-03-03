package com.jdm.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.geometry.HPos;
import javafx.scene.layout.Priority;

@Repeatable(Columns.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE}) // Indica que é para ser usada dentro de outra
public @interface Column {
    double prefWidth() default Double.NaN;        // -1 para COMPUTED_SIZE
    double minWidth() default Double.NaN;
    double maxWidth() default Double.NaN;
    double percentWidth() default Double.NaN;
    Priority hgrow() default Priority.NEVER;
    HPos halign() default HPos.LEFT;
    boolean fillWidth() default true;
}