package com.jdm;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.jdm.system.Engine;
import com.jdm.system.Model;
import com.jdm.system.Struct;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public final class Document {

	public static final int SWAP        = 0;
	public static final int DESTROY     = 1;
	public static final int AFTER_SWAP  = 2;
	public static final int BEFORE_SWAP = 3;
	
	private final List< Map<String, Action> > listeners;
	private final Map<String, Boolean> persists;
	
	private final Class<?> _class;
	private final Object _self;
	private final Path _path;

	private Scene scene;
	private String now;

	public Document( Class<?> clss ) throws Exception {
		this( clss, p -> { return new Scene( p ); } );
	}
	public Document( Class<?> clss, Consumer<Document> c ) throws Exception {
		this( clss, p -> { return new Scene( p ); }, c );
	}
	public Document( Class<?> clss, Function<Parent, Scene> f ) throws Exception {
		this( clss, f, d -> {} );
	}
	public Document( Class<?> clss, Function<Parent, Scene> function, Consumer<Document> consumer ) throws Exception {
		Model model = Engine.build( _class = clss );

		_self = Engine.self(model);

		_path = Engine.path(model);

		_path.toFile().deleteOnExit();

		Struct s = Engine.struct( model );

		Engine.reload( _path, Engine.styles( s ) );

		scene = function.apply( Engine.root( s ) );

		scene.getStylesheets().add( _path.toUri().toString() );

		List<String> roots = model.fields;

		now = roots.get(0);

		persists = new HashMap<>();

		listeners = new ArrayList< Map<String, Action> >();

		listeners.add( new HashMap<>() );
		listeners.add( new HashMap<>() );
		listeners.add( new HashMap<>() );
		listeners.add( new HashMap<>() );
		
		roots.forEach(r -> {

			persists.put(r, false);

			if ( !r.equals( now ) ) {
				clear( r );
			}

		});

		consumer.accept( this );

		Action ev = listeners.get( SWAP ).get( now );

		if ( ev != null) {

			ev.execute();

		}
	}
	
	public Scene swap(String root) {
		return swap( root, scene, () -> {} );
	}

	public Scene swap(String root, Function<Parent, Scene> factory) {
		return swap(root, factory, () -> {});
	}

	public Scene swap(String root, Action action) {
		return swap(root, scene, action);
	}

	public Scene swap(String root, Function<Parent, Scene> factory, Action action ) {
		Parent p = getRoot();	

		scene.setRoot( new Parent() {} );

		scene = factory.apply(p);

		return swap( root, scene, action);
	}

	private Scene swap(String root, Scene scene, Action action) {
		if ( !now.equals( root )) {

			Action ev = listeners.get( BEFORE_SWAP ).get( root );

			if ( ev != null) {

				ev.execute();

			}

			if ( clear( now ) ) {

				Field f = Engine.getField( root, _class );

				Struct s = Engine.struct( f, _class );

				Parent p = persists.get( root ) ? Engine.root( f, _self ) : null;
						
				if ( p == null ) {

					p = Engine.root( s );

					Engine.setFieldValue( f, _self, p );

				} 

				scene.setRoot( p );	

				ev = listeners.get( SWAP ).get( root );

				if ( ev != null) {

					ev.execute();

				}
				
				Engine.reload( _path, Engine.styles( s ) );

				scene.getStylesheets().clear();

				scene.getStylesheets().add( _path.toUri().toString() );

				ev = listeners.get( AFTER_SWAP ).get( root );

				if ( ev != null) {

					ev.execute();

				}
				
				now = root;

				action.execute();
			}
		}
		return scene;
	}

	public boolean clear( String root ) {
		if ( persists.get( root ) ) {
			return true;
		}
		if ( Engine.clear( Engine.getField( root, _class ), _self ) ) {
			Action ev = listeners.get( DESTROY ).get( root );

			if ( ev != null) {

				ev.execute();

			}

			return true;
		}
		
		return false;
	}	

	public void on(int[] ops, String root, Action action) {
		for ( int op : ops) {
			on( op, root, action );	
		}
	}

	public void onSwap(String root, Action ev) {
		on( SWAP, root, ev);
	}

	public void onAfterSwap(String root, Action ev) {
		on( AFTER_SWAP, root, ev);
	}

	public void onBeforeSwap(String root, Action ev) {
		on(BEFORE_SWAP,root, ev);
	}

	public void onDestroy(String root, Action ev) {
		on(DESTROY,root, ev);
	}

	public void on(int op, String root, Action action) {
		listeners.get( op ).put( root , action );
	}

	public void setPersists( String root ) {
		this.persists.put(root, true);	
	}
	
	public void setPersists( String root, boolean value) {
		if ( persists.containsKey(root) ) {
			this.persists.put(root, value);	
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNodeById(String id) { return (T) getNodeById( scene.getRoot(), id ); }

	@SuppressWarnings("unchecked")
	public <T extends Node> T getNodeById(Node node, String id) {

		if ( id.equals( node.getId() ) ) {

			return (T) node;

		}

		if ( node instanceof Parent ) {

			for ( Node _node : ((Parent) node).getChildrenUnmodifiable() ) {

				Node child = getNodeById(_node, id);

				if ( child != null ) {

					return (T) child;

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

	@SuppressWarnings("unchecked")
	public <T> T getSelfModel() {
		return (T) _self;
	}
	
	public Scene getScene() {
		return scene;
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T getRoot() {
		return (T) scene.getRoot();
	}
	
	 public Path getTempFile() {
		return _path;
	}

	@FunctionalInterface public interface Action { void execute(); }

}
