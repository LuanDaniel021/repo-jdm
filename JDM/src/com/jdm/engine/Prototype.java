package com.jdm.engine;

class Prototype<T> {

	Header header;
	
	final StringBuilder stylesheet;

	T root;
	
	Prototype() {
		this.stylesheet = new StringBuilder();
		this.root = null;
	}

	public T getRoot() {

		return root;

	}

	public StringBuilder getStylesheet() {

		return stylesheet;

	}

}
