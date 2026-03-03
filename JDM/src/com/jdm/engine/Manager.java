package com.jdm.engine;

import java.lang.reflect.Field;

import com.jdm.meta.Class;
import com.jdm.meta.Column;
import com.jdm.meta.ID;
import com.jdm.meta.Ignore;
import com.jdm.meta.Image;
import com.jdm.meta.Layout;
import com.jdm.meta.Row;
import com.jdm.meta.Style;

import javafx.scene.Node;

public class Manager {

	public static boolean configure(Document document, Field field, Node father, Node node) {
		
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

		int cur = 0;
		
		String key = field.getType().getSimpleName();
		
		if ( !document.currentTypes.containsKey( key ) ) {
			
			document.currentTypes.put( key, cur );
			
		} else {
			
			cur = document.currentTypes.get(key);
			
		}
		
		cur++;
		
		if ( Element._id(node, field.getName(), key, cur, id) ) {

			document.currentTypes.put( key, cur );
			
			document.elements.put( node.getId(), node );

		}
		
		Element._class(node, key, _class);

		if ( layout != null ) {

			Element._layout(node, layout);

		}
		
		document.stylesheet.append( Element._styles( node, styles).toString() );
		
		Element._columns( node, columns );
		Element._rows( node, rows);
		Element._image( node, image );

		if ( father != null ) {

			Element._linker( father, node, layout);

		}

		return true;
		
	}
	
}