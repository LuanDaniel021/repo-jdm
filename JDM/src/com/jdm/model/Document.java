package com.jdm.model;

import java.lang.reflect.Constructor;
import java.util.Set;

import com.jdm.engine.Engine;

import javafx.scene.Node;
import javafx.scene.Parent;

public final class Document {

	private final Set<String> registry_class;
	
	private final Set<String> registry_id;

	private final String stylesheet;
	
	private final Parent root;
	
	public Document( Object instance ) throws Exception { this( instance, null ); }
	
	public Document( Class<?> _class ) throws Exception { this(   null, _class ); }
	
	private Document( Object instance, Class<?> c ) throws Exception {

		if ( c != null ) {
			boolean flag = false;

			Constructor<?> ctor = c.getDeclaredConstructor();

			flag = ctor.isAccessible();

			ctor.setAccessible(true);

			instance = ctor.newInstance();

	        ctor.setAccessible(flag);
		}
		
		if ( instance == null ) throw new Error( " Document - instace == null && class == null " ); 

		else {

			Model model = Engine.build( this, instance );

			registry_class = model.registry_class();
			
			registry_id =  model.registry_id();

			stylesheet =  model.styles();

			root = model.root();

		}

	}

	public Node getNodeById(String id) {
		Node node = null;

		if ( registry_id.contains(id) ) {

			node = root.lookup( String.format("#%s", id) );	

		}

		return node;
	}

	public Parent getRoot() { return root; }

	public String getStylesheet() {

		return stylesheet;

	}

	public Node getNodeClass(String clss) {
		Node node = null;
		
		if ( registry_class.contains(clss) ) {

			node = root.lookup( String.format(".%s", clss) );	

		}
		
		return node;
	}

	public Set<Node> getNodeClassAll(String clss) {
		Set<Node> nodes = null;;
		
		if ( registry_class.contains(clss) ) {

			nodes = root.lookupAll( String.format(".%s", clss) );	

		}
		
		return nodes;
	}
	
	

}