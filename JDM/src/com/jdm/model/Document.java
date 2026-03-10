package com.jdm.model;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jdm.engine.Engine;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public final class Document {

	private final Path tmp;

	public final Map<String, Boolean> persists;

	public final  Set<String> posibles;

	public final String _default;
	
	private String stylesheet;

	private Object model;
	
	private Scene scene;

	public String now;

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

		if ( instance == null ) throw new IllegalArgumentException( "JDM - Error: instance == null && class == null" ); 

		else {

			tmp = Engine.registry();
			
			model = instance;
			
			c = model.getClass();

			List<String> roots = Engine.getRoots( c.getDeclaredFields() );

			_default = roots.get(0);
			
			now = _default;
			
			posibles = new HashSet<>(roots);

			persists = new HashMap<>();
			
			roots.forEach(r -> persists.put(r, false));
			
			Model m = Engine.build(now, model);
			
			stylesheet = m.styles();

			scene = new Scene(m.root());

	    	Files.write(tmp, stylesheet.getBytes(StandardCharsets.UTF_8));

		    scene.getStylesheets().add( tmp.toUri().toString() );
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

	private void update() {
		try {

			Model m = Engine.build(now, model);

			stylesheet = m.styles();

			update(scene, m.root());

		} catch (Exception e) {}
		
	}
	
	private void update(Scene scene, Parent root) throws IOException {
		scene.setRoot( root );
		
    	Files.write(tmp, stylesheet.getBytes(StandardCharsets.UTF_8));

    	scene.getStylesheets().clear();
    	
	    scene.getStylesheets().add( tmp.toUri().toString() );
	}

	public void swap(String root) {
		if ( posibles.contains(root) ) {

			now = root;

			update();

		}
	}
	
	

	public void swap(String root, Action<RootCTX> action) {
		
		swap(root);

		action.exe( new RootCTX( getRoot(), now ) );
		
	}

	public void swap(String root, Factory<Scene> factory) {
		swap(root);

		Parent r = getRoot();

		scene.setRoot(new Region());
		
		scene = factory.factory(r);
	}

	public void swap(String root, Factory<Scene> factory, Action<SceneCTX> action) {
		swap(root, factory);
		
		SceneCTX ctx = new SceneCTX(getRoot(), now);
		
		action.exe( ctx );
		
		if (ctx.stage != null) {
			
			ctx.stage.setScene(scene);
			
			ctx.stage.setTitle(ctx.title);
			ctx.stage.setWidth(ctx.width);
			ctx.stage.setHeight(ctx.height);

		}
		
		update();
	}
	
	@FunctionalInterface
	public interface Factory<T> { T factory(Parent root); }

	@FunctionalInterface
	public interface Action<T> { void exe(T ctx); }
	
	public class RootCTX {
	    public final Parent node;
	    public final String name;
	    
	    public RootCTX( Parent node, String name ) {
			this.node = node;
			this.name = name;
		}
	}

	public class SceneCTX extends RootCTX {
		public SceneCTX(Parent node, String name) { super(node, name); }
	    public Stage stage;
	    public String title;
	    public double width;
	    public double height;
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

	
}