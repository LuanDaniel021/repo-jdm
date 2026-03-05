package com.jdm.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.jdm.model.Element.Err;
import com.jdm.model.engine.Link;
import com.jdm.model.engine.Link.Linker;

import javafx.scene.Node;
import javafx.scene.Parent;

class Struct {

	public static Struct build( Document document ) throws Exception { return new Struct(document); }
	
	Map<String, Integer> current = new HashMap<String, Integer>();
	
	private Struct( Document document ) throws Exception {
		Object model = document._model;

		Class<?> clss = model.getClass();

		Field field = null;

		try {

			field = clss.getDeclaredField("_root_");

		} catch (Exception e) { /* ignora null */ }
		
		if ( field != null ) {

			if ( !Parent.class.isAssignableFrom( field.getType() ) ) {

				throw new Error( "ERROR: campo '_root_' reservado para tipo Parent'" );

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

			throw new Error("ERROR: root nao definido");

		}
		
		document.root = (Parent) load( document.stylesheet, model, field )._node;
	}

	private Element load(StringBuilder styles, Object father, Field field ) throws Exception {

		boolean flag = field.isAccessible();

		field.setAccessible(true);

		Node node = instace(father, field);

		field.setAccessible(flag);

		Element el = new Element( node, field );

		if (el._ignore) return el;
		
		if ( node instanceof Element.Err ) return el.err();
				
		el.current = current( el._type_name );
		
		el.pack();
		
		if ( el.isGenericID ) {

			current.put( el._type_name, el.current );

		}

		styles.append(el.stylesheet);

		Linker path = Link.get( node );
		
		if ( path != null ) {

			Field[] fields = node.getClass().getDeclaredFields();

			for ( Field _field : fields ) {

				if (shouldSkip(_field)) continue;

				else {

					Element child = load( styles, node, _field );
							
					if (!child._ignore) {
						
						path.link( node, child);

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
	
	private Node instace( Object father, Field field ) throws Exception {

		Object instace = field.get( father );

		if (instace == null) {
			try {

				Class<?> clss = field.getType();

		        Class<?> externalClass = clss.getDeclaringClass();

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

			} catch (Exception e) {

				System.err.println( String.format("Error: falha ao carregar field '%s'", field.getName()) );

				 instace = new Err();
			}
		}

        return (Node) instace;

	}

	private static boolean shouldSkip(Field field) {
		Class<?> clss = field.getType();
        return !Node.class.isAssignableFrom(clss) ||
        	field.isSynthetic() || Modifier.isStatic(field.getModifiers()) ||
        	clss.isPrimitive()  || clss.isInterface() || clss.isEnum() ||
        	Modifier.isAbstract(clss.getModifiers());
    }

}