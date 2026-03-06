package com.jdm.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jdm.engine.Link.Linker;

import javafx.scene.Parent;

class Struct implements Builder<Struct> {

	protected Set<String> ids  = new HashSet<String>();
	
	protected Set<String> classes  = new HashSet<String>();
	
	protected Map<String, Integer> current = new HashMap<String, Integer>();

	protected StringBuilder styles = new StringBuilder();

	protected Parent root;

	

	public Struct build( Object model ) throws Exception {

		Class<?> clss = model.getClass();

		Field field = null;

		try {

			field = clss.getDeclaredField("_root_");

		} catch (Exception e) { /* ignora null */ }

		if ( field != null ) {

			if ( !Parent.class.isAssignableFrom( field.getType() ) ) {

				throw new IllegalArgumentException("ERROR: O campo '_root_' deve ser do tipo Parent (ou subclasse).");

			}

		} else {

			Field[] fields = clss.getDeclaredFields();

			for ( Field _field : fields) {

				if ( Parent.class.isAssignableFrom( _field.getType() ) ) {

					field = _field;

					break;

				}

			}

		}

		if ( field == null ) {

			throw new IllegalStateException("ERROR: Nenhum campo do tipo Parent foi definido como root em " + clss.getSimpleName());

		}

		root = (Parent) load( model, field )._node;

		return this;

	}

	private Element load( Object father, Field field ) throws Exception {

		Element el = new Element( Engine.instance(father, field), field );

		el.current = current( el._type_name );

		if ( el._ignore ) return el;
		
		if ( !el._ok ) return el.err();
		
		el.pack();
		
		ids.add(el._node.getId());
		
		classes.addAll( el._node.getStyleClass() );
		
		if ( el.isGenericID ) {

			current.put( el._type_name, el.current );

		}

		styles.append( el.stylesheet );

		if (el.genered) return el;
		
		Linker path = Link.get( el._node );
		
		if ( path != null ) {

			Field[] fields = el._node.getClass().getDeclaredFields();

			for ( Field _field : fields ) {

				if ( Engine.shouldSkip(_field) ) continue;

				else {

					Element child = load( el._node, _field );
							
					if (!child._ignore) {
						
						path.link( el._node, child);

					}

				}

			}

		}

		return el;
	}

	private int current( String key ) {
		int count = 0;

		if ( !current.containsKey( key ) ) {

			current.put( key, count );

		} else {

			count = current.get(key);

		}
		
		count++;
		
		return count;
	}

}