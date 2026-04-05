package com.jdm.engine;

import java.lang.reflect.Field;
import java.nio.file.Path;

public class Model {

	private final Field[] fields;
	private final Object self;
	private final Path path;

	final Class<?> _class;

	String[] roots;

	public Model( Class<?> _class, Object _self, Path _path ) {
		this.fields = _class.getDeclaredFields();
		this.self = _self;
		this.path = _path;
		this._class = _class;
	}

	Object self() {
		return self;
	}

	Path path() {
		return path;
	}

	Field[] fields() {
		return fields;
	}

	String[] getRoots() {
		return roots;
	}

	void setRoots( String[] roots ) {
		this.roots = roots;
	}
	
}