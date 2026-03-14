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
import com.jdm.engine.Engine.DocumentDTO;
import com.jdm.engine.Model;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public final class Document {

	public static final Event BEFORE_SWAP = Event.BEFORE_SWAP;
	public static final Event AFTER_SWAP  = Event.AFTER_SWAP;
	public static final Event DESTROY     = Event.DESTROY;
	public static final Event CREATE      = Event.CREATE;

	private final Map<String, Action> listener_create;
	private final Map<String, Action> listener_after_swap;
	private final Map<String, Action> listener_before_swap;
	private final Map<String, Action> listener_destroy;

	private final Map<String, Boolean> persists;
	private final Set<String> posibles;
	private final Set<String> wirings;
	private final String _default;
	private final Object model;
	private final Path tmp;

	private Scene scene;
	private String now;

	public Document( Object o, Factory<Scene> f, Create a ) throws Exception {
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

		DocumentDTO dto = Engine.extract( c.getDeclaredFields() );
		
		List<String> roots = Engine.getRoots(dto);

		listener_create = new HashMap<>();
		listener_after_swap = new HashMap<>();
		listener_before_swap = new HashMap<>();
		listener_destroy = new HashMap<>();

		_default = roots.get(0);

		now = _default;

		wirings  = new HashSet<>( Engine.getWarings( dto ) );
		posibles = new HashSet<>(roots);
		persists = new HashMap<>();

		roots.forEach(r -> persists.put(r, false));

		Model m = Engine.build( Engine.getField( now, c ), model );

		Parent p = Engine.root( m );

		String s = Engine.styles( m );

		Engine.waring( m, model, c );

		scene = f.factory( p );

		tmp = Engine.registry();

		Engine.reload( tmp, s );

		scene.getStylesheets().clear();

		scene.getStylesheets().add( tmp.toUri().toString() );

		a.exe( this );

		Action ev = listener_create.get( now );

		if ( ev != null) {

			ev.execute();

		}
	}

	public Document( Object o, Create a ) throws Exception { this(o, Scene::new, a ); }
	public Document( Object o, Factory<Scene> f) throws Exception { this(o, f, (d) -> {} ); }
	public Document( Object o) throws Exception { this( o, Scene::new, (d) -> {} ); }

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

	public Scene swap(String root) {
		return swap( root, scene, () -> {} );
	}

	public Scene swap(String root, Factory<Scene> factory) {
		return swap(root, factory, () -> {});
	}

	public Scene swap(String root, Action action) {
		return swap(root, scene, action);
	}

	public Scene swap(String root, Factory<Scene> factory, Action action ) {
		Parent p = getRoot();	

		scene.setRoot( new Parent() {} );

		scene = factory.factory(p);

		return swap( root, scene, action);
	}

	private Scene swap(String root, Scene scene, Action action) {
		if ( !root.equals(now)) {
			if ( !posibles.contains( root ) )  System.err.println("Error: Field root dont exist");
			else {
				Object i = model;

				Class<?> c = model.getClass();

				Action ev = listener_before_swap.get(now);

				if ( ev != null) {

					ev.execute();

				}

				clear( now );
				
				Field f = Engine.getField( root, c );

				Model m = Engine.build( f, c );

				Parent p = Engine.root( m );

				String s = Engine.styles( m );
				
				Engine.waring( m, i, c );
				
				Engine.define( f, i, p );

				now = root;

				scene.setRoot( p );

				ev = listener_create.get( root );

				if ( ev != null) {

					ev.execute();

				}

				Engine.reload( tmp, s );

				scene.getStylesheets().clear();

				scene.getStylesheets().add( tmp.toUri().toString() );

				ev = listener_after_swap.get(now);

				if ( ev != null) {

					ev.execute();

				}

				action.execute();
			}
		}
		return scene;
	}

	public void clear(String root) {
		Class<?> c = model.getClass();

		Field f = Engine.getField( root, c );

		if ( !persists.get(root) ) {

			Engine.clear( f, model );
			
			for ( String w : wirings ) {
				Engine.clear( Engine.getField( w, c ), model );
			}

			Action ev = listener_destroy.get(root);

			if ( ev != null) {
				
				ev.execute();
				
			}

		}
	}

	public void onCreate(String root, Action ev) {
		listener_create.put(root, ev);
	}

	public void onAfterSwap(String root, Action ev) {
		listener_after_swap.put(root, ev);
	}

	public void onBeforeSwap(String root, Action ev) {
		listener_before_swap.put(root, ev);
	}

	public void onDestroy(String root, Action ev) {
		listener_destroy.put(root, ev);
	}

	public void on(String root, Action ev, Event... evs) {
		for (Event e : evs) {
			switch (e) {
				case CREATE     : {     onCreate(root, ev); break; }
				case DESTROY    : {    onDestroy(root, ev); break; }
				case AFTER_SWAP : {  onAfterSwap(root, ev); break; }
				case BEFORE_SWAP: { onBeforeSwap(root, ev); break; }
			}
		}
	}

	@FunctionalInterface
	public interface Factory<T> { T factory(Parent root); }

	@FunctionalInterface
	public interface Action { void execute(); }

	@FunctionalInterface
	public interface Create { void exe( Document d ); }

	@SuppressWarnings("unchecked")
	private <T> T getModel(T c) {
		return (T) model;
	}

	@SuppressWarnings("unchecked")
	public <T> T getModel() {
		return (T) getModel(model);
	}

	public Scene getScene() {
		return scene;
	}

	public Parent getRoot() {
		return scene.getRoot();
	}

	public enum Event { CREATE, AFTER_SWAP, BEFORE_SWAP, DESTROY }

}