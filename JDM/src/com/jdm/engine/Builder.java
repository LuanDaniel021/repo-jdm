package com.jdm.engine;

interface Builder<T> {
	
	T build( Object model ) throws Exception;
	
	static <T> T handle(Object instace, Builder<T> object) throws Exception {
		return object.build( instace );
	}
}
