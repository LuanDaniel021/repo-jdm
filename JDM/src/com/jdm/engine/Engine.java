package com.jdm.engine;

import static com.jdm.engine.Builder.handle;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.jdm.model.Document;
import com.jdm.model.Model;

import javafx.scene.Node;
import javafx.scene.Parent;

public class Engine {

	static int current_id = 0;
	
	public static Model build( Document document, Object instance ) throws Exception {
		
		Struct st = handle( instance, new Struct() );
		
		return new Model() {

			@Override
			public String styles() {
				return st.styles.toString();
			}

			@Override
			public Parent root() {
				return st.root;
			}

		};

	}
	
	public static Model build( String root, Object instance ) throws Exception {
		Struct st = new Struct().build(root, instance);
		
		
		
		return new Build(st.root, st.styles.toString() );

	}
	
	public static List<String> getRoots( Field[] fields ) throws Exception {
		
		Class<Parent> p = Parent.class;

		List<String> posibles = new ArrayList<>();

		for ( Field f : fields ) {

			if ( p.isAssignableFrom( f.getType() ) ) { 

				posibles.add( f.getName() );

			}

		}

		return posibles;

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

	public static Path registry() throws IOException {
		current_id++;
		
		Path tmp = Files.createTempFile("jdm-document-css-" + current_id, ".css");
		
		tmp.toFile().deleteOnExit();
		
		return tmp;
	}

	public static Parent getRoot(Object ref, String now) {
		try {

			Field f = ref.getClass().getDeclaredField(now);
			
			f.setAccessible(true);
			
			Object obj = f.get( ref );
			
			return (Parent) obj;
		}catch (Exception e) {}

		return null;
	}
}
