package com.jdm.system;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Parent;

public class Model {

	public final List<String> fields;
	private final Object _self;
	private final Path _path;
	
	final Class<?> _class;
	
	public Model( Class<?> _class, Object _self, Path _path ) {
		this._class = _class;
		this._self = _self;
		this._path = _path;
		
		fields = filter( _class.getDeclaredFields() );
	}

	private List<String> filter( Field[] fields ) {
		List<String> posibles = new ArrayList<>();

		Class<Parent> p = Parent.class;

		for ( Field f : fields ) {

			if ( p.isAssignableFrom( f.getType() ) ) { 
				posibles.add( f.getName() );
			}

		}

		return posibles;
	}

	List<String> fields() {
		return fields;
	}
	
	Object self() {
		return _self;
	}

	Path path() {
		return _path;
	}

}