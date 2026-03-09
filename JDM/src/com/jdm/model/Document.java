package com.jdm.model;

import static com.jdm.engine.Engine.build;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Document {

	private final String stylesheet;

	//private final Object model;
	
	private final String title;

	private final int height;

	private final int width;

	private Scene scene;

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

		if ( instance == null ) throw new IllegalArgumentException( "JDM - Error: instace == null && class == null" ); 

		else {
			
			Model m = build( this, instance );

			//model = instance;
			
			stylesheet =  m.styles();

			title = m.title();

			height = m.height();

			width = m.width();

			//scene = new Scene(build.root(), width, height);
			
			scene = new Scene(m.root(), width, height);
			
		}

	}

	public Node getNodeById(String id) { return getNodeById( scene.getRoot(), id ); }

	public Node getNodeById(Node node, String id) {

		if ( node.getId().equals( id ) ) {

			return node;

		}

		if ( node instanceof Parent ) {

			for ( Node _node : ((Parent) node).getChildrenUnmodifiable() ) {

				Node child = getNodeById(_node, id);

				if ( child != null ) {

					return child;

				}

			}

		}

		return null;

	}

	public Node getNodeClass(String clss) { return getNodeClass( scene.getRoot(), clss ); }

	public Node getNodeClass(Node node, String clss) {

		if ( node.getStyleClass().contains( clss ) ) {

			return node;

		}

		if ( node instanceof Parent ) {

			for ( Node _node : ((Parent) node).getChildrenUnmodifiable() ) {

				Node child = getNodeClass(_node, clss);

				if ( child != null ) {

					return child;

				}

			}

		}

		return null;

	}

	public Set<Node> getNodeClassAll(String clss) {
		return getNodeClassAll( scene.getRoot(), clss );
	}

	public Set<Node> getNodeClassAll(Node node, String clss) {
		return getNodeClassAll( new HashSet<Node>(), node, clss );
	}

	private Set<Node> getNodeClassAll(Set<Node> set, Node node, String clss) {

		if ( node.getStyleClass().contains( clss ) ) {

			set.add(node);

		}

		if ( node instanceof Parent ) {

			for ( Node _node : ((Parent) node).getChildrenUnmodifiable() ) {

				getNodeClassAll(set, _node, clss);

			}

		}

		return set;

	}

	public Node lookup(String selector) {
		return lookup( scene.getRoot(), selector );
	}

	public Node lookup(Node node, String selector) {
		return node.lookup( selector );
	}

	public Set<Node> lookupAll(String selector) {
		return lookupAll( scene.getRoot(), selector );
	}

	public Set<Node> lookupAll(Node node, String selector) {
		return node.lookupAll( selector );
	}

	public void configure(Stage stage) throws IOException {
		stage.setTitle(title);
		stage.setScene(
			configure()
		);
	}

	private Scene configure() throws IOException {

		String css = getStylesheet();

		Path tmp = Files.createTempFile("jdm-css", ".css");

		tmp.toFile().deleteOnExit();

    	Files.write(tmp, css.getBytes(StandardCharsets.UTF_8));

	    scene.getStylesheets().add( tmp.toUri().toString() );

		return scene;

	}

	public String getStylesheet() {
		return stylesheet;
	}

	public Scene getScene() {
		return scene;
	}

	public Parent getRoot() {
		return scene.getRoot();
	}

	public String getTitle() {
		return title;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

}