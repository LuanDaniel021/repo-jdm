package com.jdm;

import com.jdm.engine.Engine;
import com.jdm.model.*;

import javafx.scene.Parent;

public class Document {

	public Styles stylesheet;

	public Header header;

	public Element root;

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

		return new StringBuilder();

	}

}