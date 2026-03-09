package com.jdm.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Repeatable(StylesList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Styles {
	
	// seletor = [id][from]:[state] [target] { props } -> #id.from:state target { props }
	
	String from()   default "";
	String state()  default ""; //:hover, :focused, :selected, :disabled
	String target() default "";
	
    /* --- DIMENSÕES E POSICIONAMENTO (Layout) --- */
    String width() default "";           // para -fx-width
    String height() default "";          // para -fx-height
    String pref_width() default "";
    String pref_height() default "";
    String min_width() default "";
    String min_height() default "";
    String max_width() default "";
    String max_height() default ""; 
    String padding() default "";         // Ex: -fx-padding: x x x x
    String margin() default "";          // Ex: -fx-margin: x x x x
    String alignment() default "";       // Ex: "CENTER", "TOP_LEFT"
    String spacing() default "";         // Para HBox/VBox
    String fill_height() default "";     // boolean: "true"/"false"
    String fill_width() default "";
    String vgrow() default "";
    String hgrow() default "";
    

    String anchor_top() default "";
    String anchor_left() default "";
    String anchor_right() default "";
    String anchor_bottom() default "";
    
    /* --- FUNDO (Background) --- */
    String background_color() default "";  // Ex: "#FFFFFF", "linear-gradient(to bottom, #333, #eee)"
    String background_radius() default ""; // Ex: "5px", "10 0 10 0"
    String background_insets() default ""; // Afasta o fundo da borda
    String background_image() default "";  // Ex: "url('caminho/foto.png')"
    String background_repeat() default ""; // repeat, no-repeat

    /* --- BORDAS --- */
    String border_color() default "";      
    String border_width() default "";      
    String border_radius() default "";     
    String border_style() default "";      // solid, dashed, dotted
    String border_insets() default "";

    /* --- TIPOGRAFIA E TEXTO --- */
    String text_fill() default "";         // Traduz para -fx-text-fill
    String font_family() default "";       
    String font_size() default "";         // Ex: "12pt", "1.5em"
    String font_weight() default "";       // bold, normal, 100-900
    String font_style() default "";        // italic, normal
    String text_alignment() default "";    // LEFT, RIGHT, CENTER, JUSTIFY
    String underline() default "";         // "true"
    String wrap_text() default "";         // "true"
    String text_overrun() default "";      // ellipsis, clip
    String line_spacing() default "";

    /* --- CONTROLES ESPECÍFICOS (Inputs, Listas) --- */
    String prompt_text_fill() default "";  // Cor do placeholder
    String highlight_fill() default "";    // Cor da seleção de texto
    String highlight_text_fill() default "";
    String display_caret() default "";     // Mostrar cursor: "true"/"false"
    String cell_size() default "";         // Para ComboBox/ListView
    String hgap() default "";              // Gap horizontal do GridPane
    String vgap() default "";              // Gap vertical do GridPane
    String grid_lines() default "";        // grid-lines-visible

    /* --- EFEITOS E TRANSFORMAÇÕES --- */
    String opacity() default "";           // 0.0 a 1.0
    String cursor() default "";            // hand, wait, crosshair, default
    String rotate() default "";            // graus: "45"
    String scale_x() default "";           // multiplicador: "1.5"
    String scale_y() default "";
    String effect() default "";            // dropshadow, inner-shadow, gaussian-blur
    String visibility() default "";        // "visible", "hidden", "collapse"

    /* --- FORMAS (SVG) --- */
    String shape() default "";             // String de caminho SVG (M 0 0 L 10 10...)

    /* --- ESTADOS (Pseudo-classes) --- */
    // Permite definir estilos que só ativam em eventos
    String hover() default "";             // Ex: "-fx-background-color: blue;"
    String focused() default "";           // Ex: "-fx-border-color: yellow;"
    String disabled() default "";

    /* --- ESCAPE --- */
    String custom() default "";            // Para qualquer propriedade não listada
}