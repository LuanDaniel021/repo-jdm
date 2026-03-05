package com.jdm.model;

import java.lang.reflect.Constructor;

import javafx.scene.Node;
import javafx.scene.Parent;

public final class Document {

	final StringBuilder stylesheet;
	
	protected final Object _model;

	Parent root;
	
	{
		this.stylesheet = new StringBuilder();
		this.root = null;
	}

	public Document( Object instace ) throws Exception {

		if ( instace == null ) {

			throw new Error( " Document - instace == null " );

		}

		_model = instace;

		build();

	}
	
	public Document( Class<?> c ) throws Exception {

		Object instace = null;

		boolean flag = false;

		Constructor<?> ctor = c.getDeclaredConstructor();

		flag = ctor.isAccessible();

		ctor.setAccessible(true);

		instace = ctor.newInstance();

        ctor.setAccessible(flag);

		_model = instace;

		build();

	}

	public void build() throws Exception {

		Struct.build( this );

	}

	public Node getNodeById(String id) {
		return root.lookup( String.format("#%s", id) );
	}

	public Parent getRoot() {

		return root;

	}

	public String getStylesheet() {

		return stylesheet.toString();

	}
}