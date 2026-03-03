package com.jdm.engine;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Parent;

public class Document {

	final Map<String, Integer> current_types;
	
	final Map<String, Element> elements;

	StringBuilder stylesheet;

	Header header;

	Element root;
	
	{
		this.current_types = new HashMap<String, Integer>();
		this.elements = new HashMap<String, Element>();
		this.stylesheet = new StringBuilder();
		this.root = null;
	}

	public static Document build(Document doc) {

		return Engine.build( doc );

	}

	public Document build() {

		return build( this );

	}

	public Parent getRoot() {

		return (Parent) root.node;

	}

	public StringBuilder getStylesheet() {

		return stylesheet;

	}

	public Element getElementById(String key) {

		return elements.get(key);

	}

}