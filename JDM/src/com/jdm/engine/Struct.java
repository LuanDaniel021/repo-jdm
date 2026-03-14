package com.jdm.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jdm.engine.Link.Linker;
import com.jdm.meta.Wari;

import javafx.scene.Node;
import javafx.scene.Parent;

class Struct {
	
	public Map<String, Integer> current;

	public StringBuilder styles;

	public Parent root;
	
	public Object model;

	public Set<Wiring> wiring;
	
	{
		current  = new HashMap<>();
		styles   = new StringBuilder();
		wiring   = new HashSet<>();
		root = null;
	}
	
	public Struct build( Field field, Object object ) throws Exception {
		
		model = object;
		
		root = (Parent) load( object, field )._node;
		
		return this;
	}

	Element load( Object father, Field field ) throws Exception {

		Element element = new Element( instance(father, field), field, genericID( field.getType() ) );

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
		
		wire(element._name, element);

		styles.append( element.stylesheet );

		Linker path = Link.get( element._node );

		if ( path != null ) {

			Field[] fields = element._node.getClass().getDeclaredFields();

			for ( Field _field : fields ) {

				
				if ( shouldSkip(_field) ) continue;

				else {

					Element child = load( element._node, _field );

					if (!child._ignore) {

						path.link( element._node, child);

					}

				}

			}

		}

		return element;
	}

	private void wire(String name, Element el) { Node node = el._node;
		try {
			Field field = model.getClass().getDeclaredField(name);
			if ( field.isAnnotationPresent(Wari.class) ) {
			
				if ( field.getType().getClass().isAssignableFrom( el._type.getClass() ) ) {
					wiring.add( new Wiring(node, name ));
				}
			}

        } catch (Exception e) {}
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
	
	Node instance( Object father, Field field ) throws Exception {

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

				System.err.println(
					String.format(
						"JDM - Error: Class '%s' at field '%s' is not static. External components must be static to be instantiated.",
						className, fieldName
					)
				);
				
				instance = new Element.Error();
			}
		}

		field.setAccessible(flag);

        return (Node) instance;

	}
	
	boolean shouldSkip(Field field) {
		
		Class<?> clss = field.getType();
        
		return !Node.class.isAssignableFrom(clss) ||
        	field.isSynthetic() || Modifier.isStatic(field.getModifiers()) ||
        	clss.isPrimitive()  || clss.isInterface() || clss.isEnum() ||
        	Modifier.isAbstract(clss.getModifiers());
    }

}