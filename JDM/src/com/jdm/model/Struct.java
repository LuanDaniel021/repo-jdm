package com.jdm.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.jdm.model.Document.Builder;
import com.jdm.model.engine.Link;
import com.jdm.model.engine.Link.Linker;

import javafx.scene.Node;
import javafx.scene.Parent;

class Struct {

	public static Struct build( Object model ) throws Exception { return new Struct( model ); }

	Map<String, Integer> current = new HashMap<String, Integer>();

	StringBuilder styles = new StringBuilder();
	
	Parent root;

	private Struct( Object model ) throws Exception {
		Class<?> clss = model.getClass();

		Field field = null;

		try {

			field = clss.getDeclaredField("_root_");

		} catch (Exception e) { /* ignora null */ }
		
		if ( field != null ) {

			if ( !Parent.class.isAssignableFrom( field.getType() ) ) {

				throw new IllegalArgumentException("ERROR: O campo '_root_' deve ser do tipo Parent (ou subclasse).");

			}

		} else {

			Field[] fields = clss.getDeclaredFields();

			for ( Field _field : fields) {

				if ( Parent.class.isAssignableFrom( _field.getType() ) ) {

					field = _field;

					break;

				}

			}

		}

		if ( field == null ) {

			throw new IllegalStateException("ERROR: Nenhum campo do tipo Parent foi definido como root em " + clss.getSimpleName());

		}
		
		root = (Parent) load( model, field )._node;

	}

	private Element load( Object father, Field field ) throws Exception {

		Element el = new Element( instance(father, field), field );

		el.current = current( el._type_name );

		if ( el._ignore ) return el;
		
		if ( !el._ok ) return el.err();
		
		el.pack();
		
		if ( el.isGenericID ) {

			current.put( el._type_name, el.current );

		}

		styles.append( el.stylesheet );

		if (el.genered) return el;
		
		Linker path = Link.get( el._node );
		
		if ( path != null ) {

			Field[] fields = el._node.getClass().getDeclaredFields();

			for ( Field _field : fields ) {

				if (shouldSkip(_field)) continue;

				else {

					Element child = load( el._node, _field );
							
					if (!child._ignore) {
						
						path.link( el._node, child);

					}

				}

			}

		}

		return el;
	}

	private int current( String key ) {
		int count = 0;
		
		if ( !current.containsKey( key ) ) {
			
			current.put( key, count );
			
		} else {
			
			count = current.get(key);
			
		}
		
		count++;
		
		return count;
	}
	
	private Node instance( Object father, Field field ) throws Exception {

		boolean flag = field.isAccessible();

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

			catch (Exception e) {
				String className = field.getType().getSimpleName();
				String fieldName = field.getName();

				instance = new Element.Error(
					String.format(
						"Error: Class '%s' at field '%s' is not static.%nExternal components must be static to be instantiated.",
						className, fieldName
					)
				);
			}
		}

		field.setAccessible(flag);

        return (Node) instance;

	}

	private static boolean shouldSkip(Field field) {
		Class<?> clss = field.getType();
        return !Node.class.isAssignableFrom(clss) ||
        	field.isSynthetic() || Modifier.isStatic(field.getModifiers()) ||
        	clss.isPrimitive()  || clss.isInterface() || clss.isEnum() ||
        	Modifier.isAbstract(clss.getModifiers());
    }

	public static Struct handle(Object model, Builder<Struct> bd) throws Exception { return bd.build(model); }

}