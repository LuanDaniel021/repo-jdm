package com.jdm.engine;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class Engine {
	
	public static Document build( Document document ) {

		try {
		
			Class<?> clss = document.getClass();

			Field field = null;

			Object root  = null;

			boolean flag = false;

			try {

				field = clss.getDeclaredField("_root_");

			} catch (Exception e) {}

			if ( field != null ) {

				flag = field.isAccessible();

				field.setAccessible(true);

				root = field.get(document);

				field.setAccessible(flag);

				if (!(root instanceof Parent)) {

					throw new Error( "ERROR: campo '_root_' reservado para tipo Parent'" );

				}

			} else {

				Field[] fields = clss.getDeclaredFields();

				for ( Field _field : fields) {

					flag = _field.isAccessible();

					_field.setAccessible(true);

					root = _field.get(document);

					_field.setAccessible(flag);

					if (root instanceof Parent) {

						field = _field;

						break;

					}

				}

			}

			if (field == null) {

				throw new Error("ERROR: root nao definido");

			}

			if (root == null) {

				System.err.println("WARNING: root é nulo");

			}

			Element el = new Element();

			el.field = field;
			
			el.node = (Node) root;

			document.root = load( document, el );
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return document;

	}

	private static Element load( Document document, Element element ) throws Exception {

		Node node = element.node;

		if (node == null) {

			node = element.instace();

		}

		element.pack( document );
		
		if (node instanceof Pane) {

			boolean flag = false;

			Field[] fields = node.getClass().getDeclaredFields();
			
			for ( Field field : fields ) {

				if (shouldSkip(field)) continue;

				else {

					flag = field.isAccessible();

					field.setAccessible(true);
					
					Element el = new Element();
					
					el.father = element;
					
					el.field = field;
					
					el.node = (Node) field.get(node);
					
					load(document, el);

					field.setAccessible(flag);

				}

			}

		}
		
		return element;
	}
	
	public static boolean shouldSkip(Field field) {
		Class<?> clss = field.getType();
        return field.isSynthetic() || Modifier.isStatic(field.getModifiers()) ||
        		clss.isPrimitive() || clss.isInterface() || clss.isEnum() ||
        		Modifier.isAbstract(clss.getModifiers());
    }
	

}
