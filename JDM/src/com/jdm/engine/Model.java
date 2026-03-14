package com.jdm.engine;

import java.util.Set;

import javafx.scene.Parent;

public class Model {

	final Set<Wiring> wiring;
	
	final StringBuilder styles;
	
	final Parent root;

	Model(Set<Wiring> waring,Parent root,StringBuilder styles) {
		this.wiring = waring;
		this.styles = styles;
		this.root = root;
	}

	StringBuilder styles() {
		return styles;
	}

	Parent root() {
		return root;
	}

	Set<Wiring> wiring() {
		return wiring;
	}
	
	

}