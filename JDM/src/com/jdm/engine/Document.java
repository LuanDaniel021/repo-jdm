package com.jdm.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.Parent;

public class Document extends Prototype<Parent> {

	final Map<String, Integer> currentTypes;
	
	final Map<String, Node> elements;
	
	{ 
		this.currentTypes = new HashMap<>();
		this.elements = new HashMap<String, Node>();
	}

	final Object _model;

	public Document( Object instace ) throws Exception {

		if ( instace == null ) {

			throw new Error( " Document - instace == null " );

		}

		_model = instace;

	}
	
	public Document( Class<?> c ) throws Exception {

		Object instace = null;

		boolean flag = false;

		Constructor<?> ctor = c.getDeclaredConstructor();

		flag = ctor.isAccessible();

		ctor.setAccessible(true);

		instace = ctor.newInstance();

        ctor.setAccessible(flag);

		if ( instace == null ) {

			throw new Error( " Document - instace == null " );

		}

		_model = instace;

	}

	public Document build() { return Engine.build( this ); }

	public int getCurrent(Class<?> type) {
		return currentTypes.get(type.getSimpleName());
	}
	
	public int setCurrent(Class<?> type, int value) {
		return currentTypes.put(type.getSimpleName(), value);
	}

	public Node getElementById(String key) {

		return elements.get(key);

	}
}