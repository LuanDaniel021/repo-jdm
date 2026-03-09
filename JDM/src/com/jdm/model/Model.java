package com.jdm.model;

import javafx.scene.Parent;

public interface Model {

	Parent root();

	String styles();

	String title();

	int height();

	int width();

}