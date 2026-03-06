package com.jdm.engine;

abstract interface Builder<T> {
	
	abstract T build( Object model ) throws Exception;
	
	static Struct handle(Object instace, Builder<Struct> object) throws Exception {
		return object.build( instace );
	}
}
