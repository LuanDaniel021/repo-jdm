package com.jdm.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.Parent;

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