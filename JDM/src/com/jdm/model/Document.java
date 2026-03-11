package com.jdm.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jdm.engine.Engine;
import com.jdm.engine.Model;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Document {
	
	public static final Events CREATE = Events.CREATE;
	public static final Events AFTER_SWAP = Events.AFTER_SWAP;
	public static final Events BEFORE_SWAP = Events.BEFORE_SWAP;
	public static final Events DESTROY = Events.DESTROY;
	

	private final Map<String, Event> listener_create;
	
	private final Map<String, Event> listener_after_swap;
	
	private final Map<String, Event> listener_before_swap;
	
	private final Map<String, Event> listener_destroy;
	
	private final Map<String, Boolean> persists;

	private final  Set<String> posibles;

	private final String _default;

	private final Object model;

	private final Path tmp;

	private Scene scene;

	private String now;

	public Document( Object o ) throws Exception {
		
		if ( !(o instanceof Class<?>)) model = o;
		
		else {
			Class<?> c = (Class<?>) o;
			
			boolean flag = false;

			Constructor<?> ctor = c.getDeclaredConstructor();

			flag = ctor.isAccessible();

			ctor.setAccessible(true);

			model = ctor.newInstance();

	        ctor.setAccessible(flag);
		}

		Class<?> c = model.getClass();

		List<String> roots = Engine.getRoots( c.getDeclaredFields() );

		listener_create = new HashMap<>();
		listener_after_swap = new HashMap<>();
		listener_before_swap = new HashMap<>();
		listener_destroy = new HashMap<>();

		tmp = Engine.registry();

		_default = roots.get(0);

		now = _default;

		posibles = new HashSet<>(roots);

		persists = new HashMap<>();

		roots.forEach(r -> persists.put(r, false));

		scene = new Scene( new Parent() {} );

		Field f = Engine.getField( now, c );

		Model m = Engine.build( f, model );

		Parent p = Engine.root( m );

		String s = Engine.styles( m );

		this.apply( now, p, s );

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

	public void swap(String root) {
		if ( posibles.contains( root ) ) {
			Object i = model;

			Class<?> c = model.getClass();

			Field f = Engine.getField( root, c );

			Model m = Engine.build( f, c );

			Parent p = Engine.root( m );

			String s = Engine.styles( m );
			
			Engine.define( f, i, p );

			this.apply( root, p, s );
		}
	}
	
	public void swap(String root, Factory<Scene> factory) {
		String r;

		if ( posibles.contains( root ) ) r = root;

		else {

			System.err.println("Error: Field root dont exist, 'swap' load root default");

			r = _default;

		}

		Parent p = getRoot();	

		scene.setRoot( new Parent() {} );

		scene = factory.factory(p);

		this.swap( r );
	}

	public void swap(String root, Action<RootCTX> action) {
		
		this.swap(root);

		action.exe( new RootCTX( getRoot(), now ) );
		
	}
	
	public void swap(String root, Factory<Scene> factory, Action<SceneCTX> action) {
		this.swap(root, factory);
		
		SceneCTX ctx = new SceneCTX(scene, now);
		
		action.exe( ctx );
		
		if (ctx.stage != null) {
			
			ctx.stage.setScene(scene);
			
			ctx.stage.setTitle(ctx.title);
			ctx.stage.setWidth(ctx.width);
			ctx.stage.setHeight(ctx.height);

		}
	}

	private void apply(String root, Parent p, String s) {

		Event ev = listener_after_swap.get(now);

		if ( ev != null) {
			
			ev.execute();
			
		}
		
		clear( now );

		now = root;

		scene.setRoot( p );
		
		ev = listener_create.get( root );

		if ( ev != null) {
			
			ev.execute();
			
		}
		
		Engine.reload( tmp, s );

		scene.getStylesheets().clear();

		scene.getStylesheets().add( tmp.toUri().toString() );
		
		ev = listener_before_swap.get(now);

		if ( ev != null) {
			
			ev.execute();
			
		}

	}
	
	public void clear(String root) {

		Class<?> c = model.getClass();
		
		Field f = Engine.getField( root, c );

		boolean persist = persists.get(root);

		if ( !persist ) {

			Engine.clear( f, model );

			Event ev = listener_destroy.get(root);

			if ( ev != null) {
				
				ev.execute();
				
			}

		}

	}
	
	public void onCreate(String root, Event ev) {

		listener_create.put(root, ev);

	}
	
	public void onAfterSwap(String root, Event ev) {

		listener_after_swap.put(root, ev);

	}
	
	public void onBeforeSwap(String root, Event ev) {

		listener_before_swap.put(root, ev);

	}
	
	public void onDestroy(String root, Event ev) {

		listener_destroy.put(root, ev);

	}
	
	public void on(String root, Event ev, Events... evs) {
		for (Events e : evs) {
			switch (e) {
				case CREATE     : {     onCreate(root, ev); break; }
				case DESTROY    : {    onDestroy(root, ev); break; }
				case AFTER_SWAP : {  onAfterSwap(root, ev); break; }
				case BEFORE_SWAP: { onBeforeSwap(root, ev); break; }
			}
		}
	}

	public static Document create(Document document, Action<Document> action) {

		action.exe( document );

		Event ev = document.listener_create.get( document.now );

		if ( ev != null) {
			
			ev.execute();
			
		}
		
		return document;
	}

	@FunctionalInterface
	public interface Factory<T> { T factory(Parent root); }

	@FunctionalInterface
	public interface Action<T> { void exe(T ctx); }
	
	@FunctionalInterface
	public interface Event { void execute(); }
	
	public class RootCTX {
	    public final Parent node;
	    public final String name;
	    
	    public RootCTX( Parent node, String name ) {
			this.node = node;
			this.name = name;
		}
	}

	public class SceneCTX extends RootCTX {
		
		public Scene scene;
		public Stage stage;
	    public String title;
	    public double width;
	    public double height;

		public SceneCTX(Scene scene, String now) {
			super(scene.getRoot(), now);
			this.scene = scene;
	    }
	}
	
	public Object getModel() {
		return model;
	}

	public Scene getScene() {
		return scene;
	}

	public Parent getRoot() {
		return scene.getRoot();
	}

	public enum Events { CREATE, AFTER_SWAP, BEFORE_SWAP, DESTROY }
	
}