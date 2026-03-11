package com.jdm.engine;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.jdm.meta.Root;

import javafx.scene.Parent;

public class Engine {

	static int current_id = 0;
	
	public static List<String> getRoots( Field[] fields ) throws Exception {
		
		Class<Parent> p = Parent.class;

		List<String> posibles = new ArrayList<>();

		for ( Field f : fields ) {

			if ( f.isAnnotationPresent( Root.class )) {

				if ( p.isAssignableFrom( f.getType() ) ) { 

					posibles.add( f.getName() );

				}

			}

		}

		return posibles;

	}
	
	public static Path registry() throws IOException {
		current_id++;
		
		Path tmp = Files.createTempFile("jdm-document-css-" + current_id, ".css");
		
		tmp.toFile().deleteOnExit();
		
		return tmp;
	}

	public static Field getField(String name, Class<?> _class) {
		Field field;

		try {

			field = _class.getDeclaredField(name);

		}

		catch (NoSuchFieldException | SecurityException e) {

			field = null;

		}

		return field;
	}

	public static void clear(Field field, Object instance) {
		boolean flag = field.isAccessible();

		field.setAccessible(true);

		try {

			field.set(instance, null);

		}

		catch (IllegalArgumentException | IllegalAccessException e) {}

		field.setAccessible(flag);
	}

	public static Model build(Field field, Class<?> _class) {
		Model build;

		try {

			Object instance = null;
			
			boolean flag = false;

			Constructor<?> ctor = _class.getDeclaredConstructor();

			flag = ctor.isAccessible();

			ctor.setAccessible(true);

			instance = ctor.newInstance();

	        ctor.setAccessible(flag);

	        build = build( field, instance );

		}

		catch (Exception e) {

			build = null;

		}

		return build;
	}
	
	public static Model build(Field field, Object instance) throws Exception {
		Struct st = new Struct();

		st.build( field, instance);

		return new Model(st.root, st.styles);
	}
	
	public static Parent root(Model model) {
		return model.root();
	}
	
	public static String styles(Model model) {
		return model.styles().toString();
	}

	public static void reload(Path tmp, String styles) {
		try {

			Files.write(tmp, styles.getBytes(StandardCharsets.UTF_8));

		} catch (IOException e) { System.err.println("JDM - Error: Styles dont load in temporary file"); }
	}

	public static void define(Field field, Object instance, Parent parent) {
		boolean flag = field.isAccessible();

		field.setAccessible(true);

		try {

			field.set(instance, parent);

		}

		catch (IllegalArgumentException | IllegalAccessException e) {

			System.err.println("JDM - Error: Fail to anchor root in model field.");

		}

		field.setAccessible(flag);
		
	}

}
