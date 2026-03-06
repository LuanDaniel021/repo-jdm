package com.jdm.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import com.jdm.model.Document;
import com.jdm.model.Model;

import javafx.scene.Node;
import javafx.scene.Parent;

public class Engine {

	public static Model build( Document document, Object instace ) throws Exception {
		
		Struct st = Builder.handle( instace, new Struct() );
		
		return new Model() {
			
			@Override
			public Parent root() { return st.root; }
			
			@Override
			public String styles() { return st.styles.toString(); }

			@Override
			public Set<String> registry_id() { return st.ids; }

			@Override
			public Set<String> registry_class() { return st.classes; }
			
		};
		
	}
	
	static Node instance( Object father, Field field ) throws Exception {

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
						"Error: Class '%s' at field '%s' is not static. External components must be static to be instantiated.",
						className, fieldName
					)
				);
				
				instance = new Element.Error();
			}
		}

		field.setAccessible(flag);

        return (Node) instance;

	}
	
	static boolean shouldSkip(Field field) {
		
		Class<?> clss = field.getType();
        
		return !Node.class.isAssignableFrom(clss) ||
        	field.isSynthetic() || Modifier.isStatic(field.getModifiers()) ||
        	clss.isPrimitive()  || clss.isInterface() || clss.isEnum() ||
        	Modifier.isAbstract(clss.getModifiers());
    }
}
