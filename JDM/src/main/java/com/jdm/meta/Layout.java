package com.jdm.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.Priority;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Layout {

    /* --- GRIDPANE CONSTRAINTS (Posicionamento na Grade) --- */
    int column() default -1;
    int row() default -1;
    int col_span() default 1;
    int row_span() default 1;

    /* --- CRESCIMENTO E PRIORIDADE --- */
    Priority hgrow() default Priority.SOMETIMES; 
    Priority vgrow() default Priority.NEVER;

    Pos position() default Pos.CENTER_LEFT;
    
    HPos halignment() default HPos.LEFT;
    VPos valignment() default VPos.CENTER;

    double anchor_top() default Double.NaN;
    double anchor_left() default Double.NaN;
    double anchor_right() default Double.NaN;
    double anchor_bottom() default Double.NaN;
    
    /* --- RESTRIÇÕES DE TAMANHO FIXO (Override do Style) --- */
    // Às vezes você quer forçar o layout ignorando o CSS
    double pref_width() default -1;
    double pref_height() default -1;
    double min_width() default -1;
    double min_height() default -1;
    double max_width() default -1;
    double max_height() default -1; 
    boolean managed() default true; // Se falso, o layout ignora o componente
	String region() default "center";
}