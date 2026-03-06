package com.jdm.model;

import java.util.Set;

import javafx.scene.Parent;

public interface Model {

	Parent root();

	String styles();

	Set<String> registry_id();
	
	Set<String> registry_class();

}