package com.jdm.system;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.jdm.system.Link.Linker;

import javafx.scene.Node;
import javafx.scene.Parent;

public class Struct {

	public Map<String, Integer> current;
	public StringBuilder styles;
	public Object model;
	public Parent root;

	public Struct( Field field, Object object ) throws Exception {
		current = new HashMap<String, Integer>();
		model = object;
		styles = new StringBuilder();
		root = (Parent) load( field, object )._node;
	}

	Element load( Field field, Object father ) throws Exception {
		Element element = new Element( instance(field, father), field, genericID( field.getType() ) );

		if ( element._ignore ) {

			return element;

		}

		if ( element._ok ) {

			element.pack();

		} else {

			return element.err();

		}

		if ( element.isGenericID() ) {

			current.merge( element._type_name, 1, Integer::sum );

		}

		styles.append( element.stylesheet );

		Linker path = Link.get( element._node );

		if ( path != null ) {

			Field[] fields = element._node.getClass().getDeclaredFields();

			for ( Field _field : fields ) {

				if ( shouldSkip(_field) ) continue;

				else {

					Element child = load( _field, element._node );

					if (!child._ignore) {

						path.link( element._node, child);

					}

				}

			}

		}

		return element;
	}

	Node instance( Field field, Object father ) throws Exception {
		boolean flag = field.canAccess(father);

		field.setAccessible(true);

		Object instance = field.get( father );

		Class<?> clss = field.getType();

		if (instance == null) {
			try {
				try {
					Constructor<?> ctor = clss.getDeclaredConstructor();

		            ctor.setAccessible(true);

		            instance = ctor.newInstance();
				}
				catch (NoSuchMethodException e) {
		        	Constructor<?> ctor = clss.getDeclaredConstructor( clss.getDeclaringClass() );

		        	ctor.setAccessible(true);

	        		instance = ctor.newInstance(father);
	        	}
				field.set( father, instance );
			}
			catch (Exception e) {}
		}

		field.setAccessible(flag);

        return (Node) instance;
	}

	private String genericID(Class<?> type) {

		String key = type.getSimpleName();

		int count = 0;

		if ( !current.containsKey( key ) ) {

			current.put( key, count );

		} else {

			count = current.get(key);

		}

		count++;

		return String.format("%s-%d", key, count);
	}

	boolean shouldSkip(Field field) {

		Class<?> clss = field.getType();
        
		return !Node.class.isAssignableFrom(clss) ||
        	field.isSynthetic() || Modifier.isStatic(field.getModifiers()) ||
        	clss.isPrimitive()  || clss.isInterface() || clss.isEnum() ||
        	Modifier.isAbstract(clss.getModifiers());
    }

	String styles() {
		return styles.toString();
	}

	Parent root() {
		return root;
	}

}
