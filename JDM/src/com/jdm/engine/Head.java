package com.jdm.engine;

import com.jdm.model.DocumentModel;

class Head implements Builder<Head> {

	public String title;

	public int height;

	public int width;

	@Override
	public Head build(Object model) throws Exception {
		if ( model instanceof DocumentModel ) {
			DocumentModel doc = (DocumentModel) model;
			
			title = doc.getTitle();
			
			height = doc.getHeight();
			
			width = doc.getWidth();
			
		}
		return this;
	}

}
