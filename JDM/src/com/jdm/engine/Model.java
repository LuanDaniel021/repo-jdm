package com.jdm.engine;

import javafx.scene.Parent;

public class Model {

	final StringBuilder styles;
	
	final Parent root;

	Model(Parent root,StringBuilder styles) {
		this.styles = styles;
		this.root = root;
	}

	StringBuilder styles() {
		return styles;
	}

	Parent root() {
		return root;
	}
	
	

}