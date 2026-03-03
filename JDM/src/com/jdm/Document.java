package com.jdm;

import java.util.HashMap;
import java.util.Map;

import com.jdm.engine.Engine;
import com.jdm.model.Element;
import com.jdm.model.Header;
import com.jdm.model.Styles;

import javafx.scene.Parent;

@SuppressWarnings("unused")
public class Document {

	public final Map<String, Integer> current_types;
	
	public final Map<String, Element> elements;

	public StringBuilder stylesheet;

	public Header header;

	public Element root;
	
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