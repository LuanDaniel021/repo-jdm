package com.jdm.system;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jdm.meta.Class;
import com.jdm.meta.Column;
import com.jdm.meta.ID;
import com.jdm.meta.Ignore;
import com.jdm.meta.Layout;
import com.jdm.meta.Row;
import com.jdm.meta.Styles;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Element {

	public String stylesheet;

	public final boolean _ignore;

	public final boolean _ok;
	
	public final ID _id;

	public final Class _class;

	public final Layout _layout;

	public final Styles[] _styles;

	public final Column[] _columns;

	public final Row[] _rows;

	public final com.jdm.meta.Image _image;
	
	public final java.lang.Class<?> _type;

	public final String _type_name;

	public final String _genericID;

	public final String _name;

	public final Node _node;

	public Element( Node node, Field field, String genericID ) {

		_ignore = field.isAnnotationPresent(Ignore.class) || node == null;

		_ok = !(node instanceof Error);

		_id = field.getDeclaredAnnotation(ID.class);

		_class = field.getDeclaredAnnotation(Class.class);

    	_layout = field.getDeclaredAnnotation(Layout.class);

        _styles = field.getDeclaredAnnotationsByType(Styles.class);

		_columns = field.getAnnotationsByType(Column.class);

		_rows = field.getAnnotationsByType(Row.class);

		_image = field.getAnnotation(com.jdm.meta.Image.class);

		_type = field.getType();

		_type_name = _type.getSimpleName();

		_genericID = genericID;

		_name = field.getName();

		_node = node;

	}

	public Element pack() {

		configure( this, Element::ID );

		configure( this, Element::CLASS );

		configure( this, Element::LAYOUT );

		configure( this, Element::STYLES );

		configure( this, Element::COLUMNS );

		configure( this, Element::ROWS );

		configure( this, Element::IMAGE );

		return this;

	}
	
	public Element err() {

		configure( this, Element::ID );

		configure( this, Element::LAYOUT );

		return this;

	}
	
	private static void configure( Element el, Configure config ) { config.exe(el); }
	
	private static void ID(Element el) {
		String value = "";
		
    	if (el._id != null) {

    		value = el._id.value();

    	}

    	if ( value.isEmpty() ) {

    		value = el._genericID;

		}

    	el._node.setId(value);
	}
	
	private static void CLASS(Element el) {
		Node node = el._node;
		
		String type = el._type_name;
		
		node.getStyleClass().add( type );

    	if (el._class != null) {

    		node.getStyleClass().addAll( el._class.value() );

    	}
	}
	
	private static void LAYOUT(Element el) {
		if (el._layout != null) {
			Layout l = el._layout;

			Node node = el._node;
			
			if (l.vgrow() != Priority.NEVER) VBox.setVgrow(node, l.vgrow());

	    	if (l.hgrow() != Priority.NEVER) HBox.setHgrow(node, l.hgrow());

	    	if (!Double.isNaN(l.anchor_top()))    AnchorPane.setTopAnchor(node, l.anchor_top());

	    	if (!Double.isNaN(l.anchor_left()))   AnchorPane.setLeftAnchor(node, l.anchor_left());

		    if (!Double.isNaN(l.anchor_right()))  AnchorPane.setRightAnchor(node, l.anchor_right());

		    if (!Double.isNaN(l.anchor_bottom())) AnchorPane.setBottomAnchor(node, l.anchor_bottom());

	    	try {

	    		Method setAlign = node.getClass().getMethod("setAlignment", Pos.class);

		        setAlign.invoke(node, l.position());

		    }

	    	catch (Exception ignored) { StackPane.setAlignment(node, l.position()); }

			if (node instanceof Region) { Region r = (Region) el._node;
		
				if (l.pref_width() >= 0) r.setPrefWidth(l.pref_width());
		
				if (l.pref_height()>= 0) r.setPrefHeight(l.pref_height());
		
				if (l.max_width()  >= 0) r.setMaxWidth(l.max_width());
		
				if (l.max_height() >= 0) r.setMaxHeight(l.max_height());
		
				if (l.min_width()  >= 0) r.setMinWidth(l.min_width());
		
				if (l.min_height() >= 0) r.setMinHeight(l.min_height());
		
			    GridPane.setHalignment(node, l.halignment());
		
			    GridPane.setValignment(node, l.valignment());
		    }
		}
	}
	
	private static void STYLES(Element el) { 
	
		el.stylesheet = StylesManager.load(el._node, el._styles).toString();
		
	}
	
	private static void COLUMNS(Element el) {
		if ( el._rows.length > 0 && el._node instanceof GridPane) {
			GridPane grid = (GridPane) el._node;

			ObservableList<ColumnConstraints> oc = grid.getColumnConstraints(); 
	
			for (Column _c : el._columns) {
	
				ColumnConstraints cc = new ColumnConstraints();
	
				if (!Double.isNaN(_c.percentWidth())) cc.setPercentWidth(_c.percentWidth());
	
				if (!Double.isNaN(_c.prefWidth())) cc.setPrefWidth(_c.prefWidth());
	
				if (!Double.isNaN(_c.maxWidth())) cc.setMaxWidth(_c.maxWidth());
	
				if (!Double.isNaN(_c.minWidth())) cc.setMinWidth(_c.minWidth());
	
				cc.setFillWidth(_c.fillWidth());
	
				cc.setHalignment(_c.halign());
	
				cc.setHgrow(_c.hgrow());
	
				oc.add(cc);

			}
		}
	}
	
	private static void ROWS(Element el) {
		if ( el._rows.length > 0 && el._node instanceof GridPane) {
			GridPane grid = (GridPane) el._node;

			ObservableList<RowConstraints> or = grid.getRowConstraints();

			for (Row _r : el._rows) {

				RowConstraints rc = new RowConstraints();

				if (!Double.isNaN(_r.percentHeight())) rc.setPercentHeight(_r.percentHeight());

				if (!Double.isNaN(_r.prefHeight())) rc.setPrefHeight(_r.prefHeight());

				if (!Double.isNaN(_r.maxHeight())) rc.setMaxHeight(_r.maxHeight());

				if (!Double.isNaN(_r.minHeight())) rc.setMinHeight(_r.minHeight());

				rc.setFillHeight(_r.fillHeight());

				rc.setValignment(_r.valign());

				rc.setVgrow(_r.vgrow());

				or.add(rc);

			}	
		}
	}
	
	private static void IMAGE(Element el) {
		if (el._image != null && el._node instanceof ImageView) {
			
			com.jdm.meta.Image i = el._image;
			
			Node node = el._node;
	
	        ImageView img = (ImageView) node;
	
	        if (!i.url().isEmpty()) img.setImage(new Image(i.url()));
	
	        if (i.width() != -1) img.setFitWidth(i.width());
	
	        if (i.height() != -1) img.setFitHeight(i.height());
	
	        img.setPreserveRatio(i.preserve_ratio());

    	}
	}

	@FunctionalInterface
	interface Configure { void exe(Element el); }

	public static class Error extends StackPane {

		public Error() { setStyle("-fx-background-color: #ffeeee; -fx-border-color: red; -fx-pref-width: 25; -fx-pref-height: 25; "); }

	}

	public boolean isGenericID() {
		return _node.getId().equals(_genericID);
	}

}