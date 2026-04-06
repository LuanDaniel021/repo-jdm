package com.jdm.system;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import javafx.scene.Parent;

public class Engine {

	private static int COUNT_TempFile = 0;

	private static final Supplier< String > TempFileName = () -> "jdm-TempFile-" + ( COUNT_TempFile++ );

	public static Model build( Class<?> _class ) throws Exception {
		Constructor<?> ctor = _class.getDeclaredConstructor();

		boolean flag = ctor.canAccess(null);

		ctor.setAccessible(true);

		Object _self = ctor.newInstance();

	    ctor.setAccessible(flag);

		Path _path = Files.createTempFile( TempFileName.get(), ".css");

		return new Model( _class, _self, _path );
	}

	public static Struct struct( Model model ) {
		Object instance = model.self();

		String name = model.fields().get(0);
		
		Field field = getField( name, model._class);

		return struct( field, instance );
	}
	
	public static Struct struct( Field field, Class<?> _class ) {
	    try {
	    	Constructor<?> ctor = _class.getDeclaredConstructor();

	    	boolean flag = ctor.canAccess(null);

			ctor.setAccessible(true);

			Object instance = ctor.newInstance();

		    ctor.setAccessible(flag);

			return new Struct( field, instance );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Struct struct( Field field, Object instance ) {
		try {
			return new Struct( field, instance );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setFieldValue(Field field, Object instance, Object value) {
		boolean flag = field.canAccess(instance);
		field.setAccessible(true);
		try {
			field.set(instance, value);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			System.err.println("JDM - Error: Fail to anchor root in model field.");
		}
		field.setAccessible(flag);
	}

	public static Field getField(String name, Class<?> _class) {
		try {
			return _class.getDeclaredField(name);
		}
		catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Path reload(Path tmp, String styles) {
		try {
			Files.write(tmp, styles.getBytes(StandardCharsets.UTF_8)); 
		}
		catch (IOException e) {
			System.err.println("JDM - Error: Styles dont load in temporary file");
		}
		return tmp;
	}

	public static boolean clear(Field field, Object instance) {
		boolean response = true, flag = field.canAccess(instance);

		field.setAccessible(true);
		try {
			field.set(instance, null);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			response = false;
		}
		field.setAccessible(flag);

		return response;
	}

	public static Object self( Model model ) {
		return model.self();
	}

	public static Path path( Model model ) {
		return model.path();
	}

	public static String styles(Struct s) {
		return s.styles();
	}
	
	public static Parent root(Struct s) {
		return s.root();
	}
	
	public static Parent root(Field field, Object instance) {
		boolean flag = field.canAccess(instance); 
		field.setAccessible(true);
		Object p;
		try {
			p = field.get(instance);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			p = null;
		}
		field.setAccessible(flag);
		return (Parent) p;
	}

}
