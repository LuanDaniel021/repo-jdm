package com.jdm.engine;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.scene.Parent;

public class Engine {

	private static int COUNT_TempFile = 0;

	private static final Supplier< String > TempFileName = () -> "jdm-TempFile-" + COUNT_TempFile++;
	
	public static List<String> extract( Model model ) throws Exception {
		List<String> posibles = new ArrayList<>();

		Class<Parent> p = Parent.class;

		for ( Field f : model.fields() ) {

			if ( p.isAssignableFrom( f.getType() ) ) { 

				posibles.add( f.getName() );

			}

		}

		return posibles;
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

	public static Model build( Class<?> _class ) throws Exception {
		Constructor<?> ctor = _class.getDeclaredConstructor();

		boolean flag = ctor.isAccessible();

		ctor.setAccessible(true);

		Object _self = ctor.newInstance();

	    ctor.setAccessible(flag);

		Path _path = Files.createTempFile( TempFileName.get(), ".css");

		return new Model( _class, _self, _path );
	}
	
	public static Struct struct(Field field, Class<?> _class) {
		Struct struct;

		try {
			Constructor<?> ctor = _class.getDeclaredConstructor();

			boolean flag = ctor.isAccessible();

			ctor.setAccessible(true);

			Object instance = ctor.newInstance();

	        ctor.setAccessible(flag);

	        struct = struct( field, instance );
		}

		catch (Exception e) {
			struct = null;
		}

		return struct;
	}
	
	public static Struct struct(Field field, Object instance) throws Exception {
		return new Build(field, instance).struct();
	}
	
	public static Parent root(Struct model) {
		return model.root();
	}
	
	public static String styles(Struct model) {
		return model.styles().toString();
	}

	public static Path reload(Path tmp, String styles) {
		try { Files.write(tmp, styles.getBytes(StandardCharsets.UTF_8)); }
		catch (IOException e) {
			System.err.println("JDM - Error: Styles dont load in temporary file");
		}
		return tmp;
	}

	public static void define(Field field, Object instance, Object value) {
		boolean flag = field.isAccessible();

		field.setAccessible(true);

		try {
			field.set(instance, value);
		}

		catch (IllegalArgumentException | IllegalAccessException e) {

			System.err.println("JDM - Error: Fail to anchor root in model field.");

		}

		field.setAccessible(flag);
	}

	public static Field[] fields( Model model ) {
		return model.fields();
	}

	public static Object self( Model model ) {
		return model.self();
	}

	public static Path path( Model model ) {
		return model.path();
	}

}
