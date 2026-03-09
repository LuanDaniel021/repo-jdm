package com.jdm.model;

import java.util.HashMap;
import java.util.Map;

import com.jdm.engine.Build;

public class Models {

	static final Map<Class<?>, Build<?>> CACHE;

	static {

		CACHE = new HashMap<>();

	}
	
	static Build<?> put( Class<?> c, Build<?> b ) {
		
		return CACHE.put(c, b);
	
	}

	static Build<?> remove( Class<?> c) {
		return CACHE.remove(c);
	}
	
	static Build<?> get( Class<?> c ) {
		return CACHE.get(c);
	}

}
