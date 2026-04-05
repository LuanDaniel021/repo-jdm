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

import com.jdm.animate.Animation;
import com.jdm.engine.Engine;
import com.jdm.engine.Model;
import com.jdm.engine.Struct;

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public final class Document {

	public static final int SWAP        = 0;
	public static final int DESTROY     = 1;
	public static final int AFTER_SWAP  = 2;
	public static final int BEFORE_SWAP = 3;

	private final List< Map<String, Action> > listeners;

	private final Map<String, Animation> animations;

	private final Map<String, Boolean> persists;
	private final Set<String> posibles;
	
	private final Class<?> _class;
	private final Object _self;
	private final Path _path;

	private Scene scene;
	private String now;

	public Document( Class<?> clss, Function<Parent, Scene> f, Consumer<Document> c ) throws Exception {
		Model model = Engine.build( _class = clss );

		_self = Engine.self(model);

		_path = Engine.path(model);

		_path.toFile().deleteOnExit();

		List<String> roots = Engine.extract( model );

		posibles = new HashSet<>(roots);
		
		persists = new HashMap<>();

		roots.forEach(r -> persists.put(r, false));
		
		now = roots.get(0);
		
		listeners = new ArrayList< Map<String, Action> >();
		
		listeners.add( new HashMap<>() );
		listeners.add( new HashMap<>() );
		listeners.add( new HashMap<>() );
		listeners.add( new HashMap<>() );

		animations = new HashMap<>();

		Struct s = Engine.struct( Engine.getField( now, _class ), _self );

		scene = f.apply( Engine.root( s ) );

		Engine.reload( _path, Engine.styles( s ) );

		scene.getStylesheets().clear();

		scene.getStylesheets().add( _path.toUri().toString() );

		c.accept( this );

		Action ev = listeners.get( SWAP ).get( now );

		if ( ev != null) {

			ev.execute();

		}
	}

	public Document( Class<?> clss ) throws Exception {
		this( clss, Scene::new );
	}

	public Document( Class<?> clss, Consumer<Document> c ) throws Exception {
		this( clss, Scene::new, c );
	}

	public Document( Class<?> clss, Function<Parent, Scene> f ) throws Exception {
		this( clss, f, (d) -> {} );
	}

	public Node getNodeById(String id) { return getNodeById( scene.getRoot(), id ); }

	public Node getNodeById(Node node, String id) {

		if ( id.equals( node.getId() ) ) {

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
		if ( !root.equals( now )) {
			if ( !posibles.contains( root ) )  System.err.println("Error: Field root dont exist");
			else {
				Object i = _self;

				Class<?> c = _self.getClass();

				Action ev = listeners.get( BEFORE_SWAP ).get(now);

				if ( ev != null) {

					ev.execute();

				}

				clear( now );
				
				Field f = Engine.getField( root, c );

				Struct m = Engine.struct( f, c );

				Parent p = Engine.root( m );

				String s = Engine.styles( m );
				
				Engine.define( f, i, p );

				now = root;

				scene.setRoot( p );

				ev = listeners.get( SWAP ).get( now );

				if ( ev != null) {

					ev.execute();

				}

				Engine.reload( _path, s );

				scene.getStylesheets().clear();

				scene.getStylesheets().add( _path.toUri().toString() );

				ev = listeners.get( AFTER_SWAP ).get( now );

				if ( ev != null) {

					ev.execute();

				}

				action.execute();
			}
		}
		return scene;
	}

	public void clear(String root) {
		Class<?> c = _self.getClass();

		Field f = Engine.getField( root, c );

		if ( !persists.get(root) ) {

			Engine.clear( f, _self );

			Action ev = listeners.get( DESTROY ).get( now );

			if ( ev != null) {

				ev.execute();

			}

		}
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

	@SuppressWarnings("unchecked")
	public <T> T getSelfModel() {
		return (T) _self;
	}

	public Scene getScene() {
		return scene;
	}
	
	public void setPersists( String root ) {
		setPersists(root, true);
	}

	public void setPersists( String root, boolean value) {
		if ( persists.containsKey(root) ) {
			this.persists.put(root, value);	
		}
	}

	public Parent getRoot() {
		return scene.getRoot();
	}

	public Animation animation( String name ) {
		return animations.get(name);
	}

	public Animation animation( String name, Animate animate ) {
		return animation(name, animations.get(name), animate);
	}
	
	public Animation animation( String name, Function<Timeline, Animation> factory ) {
		Animation a = factory.apply( null );
		animations.put(name, a);
		return a;
	}
	
	public Animation animation( String name, Function<Timeline, Animation> factory, Animate animate ) {
		return animation( name, animation(name, factory), animate );
	}
	
	private Animation animation( String name, Animation animation, Animate animate ) {
		
		animate.action(animation);

		return animation;
	}
	
	@FunctionalInterface
	public interface Action { void execute(); }

	@FunctionalInterface
	public interface Animate { void action( Animation a ); }

}
