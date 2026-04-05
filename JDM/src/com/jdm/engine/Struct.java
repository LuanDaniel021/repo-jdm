package com.jdm.engine;

import javafx.scene.Parent;

public class Struct {

	public final StringBuilder styles;
	public final Parent root;

	Struct(Parent root,StringBuilder styles) {
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