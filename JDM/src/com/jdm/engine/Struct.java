package com.jdm.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jdm.engine.Link.Linker;

import javafx.scene.Parent;

class Struct implements Builder<Struct> {
	
	public Map<String, Integer> current;

	public StringBuilder styles;

	public Parent root;
	
	{
		current  = new HashMap<>();
		styles   = new StringBuilder();
		root = null;
	}
	
	@Override
	public Struct build( Object model ) throws Exception {

		Class<?> clss = model.getClass();

		Root roots = new Root(clss.getDeclaredFields());

		if ( roots.posibles.isEmpty() ) {

			throw new IllegalStateException("ERROR: Nenhum campo do tipo Parent foi definido como root em " + clss.getSimpleName());

		}

		root = (Parent) load( model, clss.getDeclaredField( roots.now ) )._node;

		return this;

	}

	private Element load( Object father, Field field ) throws Exception {

		Element element = new Element( Engine.instance(father, field), field, genericID( field.getType() ) );

		if ( element._ignore ) {

			return element;

		}

		if ( element._ok ) {

			element.pack();

		} else {

			return element.err();

		}

		if ( element.isGenericID() ) {

			current.merge( element._type_name, 1, Integer::sum );

		}

		styles.append( element.stylesheet );

		boolean anonymus = element._node.getClass().isAnonymousClass();
		
		boolean instaced = !anonymus && element._type.isMemberClass();
		
		if ( anonymus || instaced ) {

			Linker path = Link.get( element._node );
			
			if ( path != null ) {

				Field[] fields = element._node.getClass().getDeclaredFields();

				for ( Field _field : fields ) {

					if ( Engine.shouldSkip(_field) ) continue;

					else {

						Element child = load( element._node, _field );
								
						if (!child._ignore) {
							
							path.link( element._node, child);

						}

					}

				}

			}

		}

		return element;
	}

	private String genericID(Class<?> type) {

		String key = type.getSimpleName();
		
		int count = 0;

		if ( !current.containsKey( key ) ) {

			current.put( key, count );

		} else {

			count = current.get(key);

		}
		
		count++;
		
		return String.format("%s-%d", key, count);
	}
	
	class Root {
		
		public Map<String, Boolean> persists;

		public Set<String> posibles;

		public String _default;

		public String now;
		
		{
			persists = new HashMap<>();
			posibles = new HashSet<>();
			_default = "_root_";
			now = "";
		}
		
		public Root(Field[] fields) {

			Class<Parent> p = Parent.class;

			String field = "";

			for ( Field f : fields ) {

				if ( p.isAssignableFrom( f.getType() ) ) { String name = f.getName();

					if (field.isEmpty()) field = name;

					posibles.add( name );

					persists.put(name, false);

				}

			}
			
			if ( !posibles.contains(_default) ) {

				_default = field;

			}
			
			now = _default;

		}
	
	}
	
	class Data {
		
		String _id;

		String[] _class;

		Layout _layout;

		Contrains _contrains;
		
	}
	
	class Layout {
		
	}
	
	class Contrains {
		
	}

}