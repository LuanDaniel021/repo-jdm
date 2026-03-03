package com.jdm.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

class Engine {
	
	public static Document build( Document document ) {
		try {

			Object model = document._model;

			Class<?> clss = model.getClass();
			
			Field field = null;

			Object root  = null;

			boolean flag = false;

			try {

				field = clss.getDeclaredField("_root_");

			} catch (Exception e) {}

			if ( field != null ) {

				flag = field.isAccessible();

				field.setAccessible(true);

				root = field.get( model );

				field.setAccessible(flag);

				if (!(root instanceof Parent)) {

					throw new Error( "ERROR: campo '_root_' reservado para tipo Parent'" );

				}

			} else {

				Field[] fields = clss.getDeclaredFields();

				for ( Field _field : fields) {

					flag = _field.isAccessible();

					_field.setAccessible(true);

					root = _field.get( model );

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

				root = instace(field, model);

			}

			document.root = (Parent) load(document, field, null, (Node) root);

		} catch (Exception e) { e.printStackTrace(); }
		
		return document;

	}
	
	private static Node load( Document document, Field field, Node father, Node node ) throws Exception {

		if (node == null) node = instace( field, father );

		Manager.configure(document, field, father, node);

		if (node instanceof Pane) {

			boolean flag = false;

			Field[] fields = node.getClass().getDeclaredFields();
			
			for ( Field _field : fields ) {

				if (shouldSkip(_field)) continue;

				else {

					flag = _field.isAccessible();

					_field.setAccessible(true);

					load( document, _field, node, (Node) _field.get(node) );

					_field.setAccessible(flag);

				}

			}

		}

		return node;
	}
	
	public static Node instace( Field field, Object father ) throws Exception {

		boolean flag = field.isAccessible();

		field.setAccessible(true);

		Class<?> clss = field.getType();

        Class<?> externalClass = clss.getDeclaringClass();

        Object instace = null;

        if (externalClass != null && !Modifier.isStatic(clss.getModifiers())) {

        	Constructor<?> ctor = clss.getDeclaredConstructor(externalClass);

        	ctor.setAccessible(true);

        	instace = ctor.newInstance(father);

        } else {

        	Constructor<?> ctor = clss.getDeclaredConstructor();

            ctor.setAccessible(true);

            instace = ctor.newInstance();

        }

        field.set( father, instace );

        field.setAccessible(flag);

        return (Node) instace;

	}
	
	public static boolean shouldSkip(Field field) {
		Class<?> clss = field.getType();
        return field.isSynthetic() || Modifier.isStatic(field.getModifiers()) ||
        		clss.isPrimitive() || clss.isInterface() || clss.isEnum() ||
        		Modifier.isAbstract(clss.getModifiers());
    }
	

}
