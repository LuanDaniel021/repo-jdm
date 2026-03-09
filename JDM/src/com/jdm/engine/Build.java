package com.jdm.engine;

import javafx.scene.Parent;

public interface Build<T extends Parent> {

	T root();
	
}
