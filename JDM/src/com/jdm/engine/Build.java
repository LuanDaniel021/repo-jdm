package com.jdm.engine;

import com.jdm.model.Model;

import javafx.scene.Parent;

class Build implements Model {

	private final String styles;
	
	private final Parent root;

	public Build(Parent root, String styles) {
		this.styles = styles;
		this.root = root;
	}

	@Override
	public String styles() {
		return styles;
	}

	@Override
	public Parent root() {
		return root;
	}

}
