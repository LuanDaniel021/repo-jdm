package com.jdm.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;

import com.jdm.meta.Column;
import com.jdm.Document;
import com.jdm.meta.Class;
import com.jdm.meta.ID;
import com.jdm.meta.Ignore;
import com.jdm.meta.Image;
import com.jdm.meta.Layout;
import com.jdm.meta.Row;
import com.jdm.meta.Style;
import com.jdm.model.Element;

public class Manager {
	
	public static boolean configure(Document document, Element el) throws Exception {

		Field field = el.field;
		
		if (field.isAnnotationPresent(Ignore.class)) {

			return false;

    	}

		ID id = field.getDeclaredAnnotation(ID.class);

		Class _class = field.getDeclaredAnnotation(Class.class);

    	Layout layout = field.getDeclaredAnnotation(Layout.class);

        Style[] styles = field.getDeclaredAnnotationsByType(Style.class);

		Column[] columns = field.getAnnotationsByType(Column.class);

		Row[] rows = field.getAnnotationsByType(Row.class);

		Image image = field.getAnnotation(Image.class);

		handle(el,      id, Element::_id);

		handle(el,  _class, Element::_class);

		if ( layout != null ) {

			handle(el, layout, Element::_layout);

		}

		handle(el,  styles, Element::_styles);

		handle(el, columns, Element::_columns);

		handle(el,    rows, Element::_rows);

		handle(el,   image, Element::_image);

		if (el.father != null) {
			
			handle(el,  layout, Element::_linker); 
		
		}
		

		return true;

	}
	
	private static <T extends Annotation> void handle(Element el, T annotation, BiConsumer<Element, T> consumer) {
        consumer.accept(el, annotation);
	}
	
	private static <T extends Annotation> void handle(Element el, T[] annotation, BiConsumer<Element, T[]> consumer) {
		consumer.accept(el, annotation);
	}
}