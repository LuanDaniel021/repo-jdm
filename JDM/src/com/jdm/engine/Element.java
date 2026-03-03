package com.jdm.engine;

import java.lang.reflect.Method;

import com.jdm.meta.Column;
import com.jdm.meta.ID;
import com.jdm.meta.Layout;
import com.jdm.meta.Row;
import com.jdm.meta.Style;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Element {
	
	static boolean _id(Node node, String _name, String _class, int current, ID i) {

		boolean flag = false;
		
    	if (i != null) {

    		String value = i.value();
    		
    		if ( value.isEmpty() ) {

    			value = String.format("%s-%d", _class, current);
    			
    			flag = true;

    		}
    		
    		node.setId(value);
    		
    	} else {
    		
    		node.setId(_name);

    	}
    	
    	return flag;

	}

	static void _class(Node node, String type, com.jdm.meta.Class c) {
		
    	node.getStyleClass().add( type );

    	if (c != null) {

    		node.getStyleClass().addAll( c.value() );

    	}

	}

	static void _layout(Node node, Layout l) {

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

		if (node instanceof Region) { Region r = (Region) node;

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

	static StringBuilder _styles(Node node, Style[] s) { return StylesManager.load(node, s); }

	static void _columns(Node node, Column[] c) {

		if (node instanceof GridPane) { GridPane grid = (GridPane) node;

			ObservableList<ColumnConstraints> oc = grid.getColumnConstraints(); 

			for (Column _c : c) {

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

	static void _rows(Node node, Row[] r) {

		if (node instanceof GridPane) { GridPane grid = (GridPane) node;

			ObservableList<RowConstraints> or = grid.getRowConstraints();

			for (Row _r : r) {

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

	static void _image(Node node, com.jdm.meta.Image i) {

		if (node instanceof ImageView) {

            if (i != null) {

    	        ImageView img = (ImageView) node;

    	        if (!i.url().isEmpty()) img.setImage(new Image(i.url()));

    	        if (i.width() != -1) img.setFitWidth(i.width());

    	        if (i.height() != -1) img.setFitHeight(i.height());

    	        img.setPreserveRatio(i.preserve_ratio());

            }

    	}

	}
	
	static boolean _linker(Node father, Node node, Layout l) {
		
		//1. O Rei dos Layouts: GridPane
	    if (father instanceof GridPane) {

	    	GridPane grid = (GridPane) father;

	        int col = 0;

	        int row = 0;

	        if (l != null) {

	        	col = l.column();

		        row = l.row();

	        }

	        grid.add(node, col, row);

	        return true;

	    }

	    // 2. Containers de Lista de Filhos (HBox, VBox, AnchorPane, Group, StackPane)
	    if (father instanceof Pane) {

	    	Pane pane = (Pane) father;

	        if (!pane.getChildren().contains(node)) {

	        	pane.getChildren().add(node);

	        }

	        return true;

	    }

	    // 3. ScrollPane: Só aceita UM conteúdo (o Viewport)
	    if (father instanceof ScrollPane) {

	    	ScrollPane scrollpane = (ScrollPane) father;

	    	scrollpane.setContent(node);

	        return true;

	    }

	    // 4. SplitPane: Adiciona aos itens divisíveis
	    if (father instanceof SplitPane) {

	    	SplitPane split = (SplitPane) father;

	        if (!split.getItems().contains(node)) {

	        	split.getItems().add(node);

	        }

	        return true;

	    }

	    // 5. TabPane: Aqui o bicho pega, porque o Node precisa estar dentro de uma Tab
	    if (father instanceof TabPane) {

	    	TabPane tabPane = (TabPane) father;

	        Tab tab = new Tab(node.getId(), node);

	        tabPane.getTabs().add(tab);

	        return true;

	    }

	    if (father instanceof BorderPane) {

	    	BorderPane border = (BorderPane) father;

	        String region = "center";
	        
	        if ( l != null) {
	        
	        	region = l.region();
	        	
	        }

	        switch (region) {

	        	case "top":    border.setTop(node);    break;

	            case "bottom": border.setBottom(node); break;

	            case "left":   border.setLeft(node);   break;

	            case "right":  border.setRight(node);  break;

	            default:       border.setCenter(node); break; // Default é sempre o centro

	        }

	        return true;

	    }

	    // 6. TitledPane: O conteúdo colapsável
	    if (father instanceof TitledPane) {

	    	TitledPane title = (TitledPane) father;

	        title.setContent(node);

	        return true;

	    }

	    return false;

	}

	public void pack(Document document) throws Exception {
		
//		String type = field.getType().getSimpleName();
//		
//		current_type = 0;
//
//		if (!document.current_types.containsKey(type)) {
//
//			document.current_types.put(type, current_type);
//
//		} else {
//
//			current_type = document.current_types.get(type);
//
//		}
//
//		current_type++;
//
//		document.current_types.put(type, current_type);
//		
//		if ( Manager.configure( document, this ) ) {
//			
//			if ( !ignore ) {
//
//				document.elements.put(node.getId(), this);
//
//			}
//
//    		document.stylesheet.append( styles.toString() );
//
//    	}

	}

}