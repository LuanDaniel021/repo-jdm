package com.jdm.engine;

import java.util.HashMap;
import java.util.Map;

import com.jdm.meta.Layout;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Link {
	
	private static final Map<Class<?>, Linker> CONTAINERS;

	static {
		CONTAINERS =  new HashMap<Class<?>, Linker>();

		CONTAINERS.put(Pane.class, Link::Pane);
		CONTAINERS.put(HBox.class, Link::HBox);
		CONTAINERS.put(VBox.class, Link::VBox);
		CONTAINERS.put(GridPane.class, Link::GridPane);
		CONTAINERS.put(BorderPane.class, Link::BorderPane);

		CONTAINERS.put(TabPane.class, Link::TabPane);
		CONTAINERS.put(SplitPane.class, Link::SplitPane);
		CONTAINERS.put(ScrollPane.class, Link::ScrollPane);

		CONTAINERS.put(TitledPane.class, Link::TitledPane);
	}
	
	public static Linker get( Object father ) { 

		Class<?> clss = father.getClass();

		while ( clss != null ) {

			if ( CONTAINERS.containsKey(clss) ) {

				break;

			}

			clss = clss.getSuperclass();

		}

		return CONTAINERS.get(clss);

	}

	private static void Pane( Object father, Element el ) {
		
		Pane pane = (Pane) father;
		
		Node child = el._node;

        if (!pane.getChildren().contains(child)) {

        	pane.getChildren().add(child);

        }
	}
	
	private static void HBox( Object father, Element el ) {
		Pane( father, el );
	}
	
	private static void VBox( Object father, Element el ) {
		Pane( father, el );
	}
	
	private static void GridPane( Object father, Element el ) {
		GridPane grid = (GridPane) father;
		
		Node child = el._node;
		
		Layout l = el._layout;

        int col = 0;

        int row = 0;

        if (l != null) {

        	col = l.column();

	        row = l.row();

        }

        grid.add(child, col, row);
	}
	
	private static void BorderPane(Object father, Element el) {
		BorderPane border = (BorderPane) father;

		Node child = el._node;

		Layout l = el._layout;

        String region = "center";

        if ( l != null) {
        
        	region = l.region();
        	
        }

        switch (region) {

        	case "top":    border.setTop(child);    break;

            case "bottom": border.setBottom(child); break;

            case "left":   border.setLeft(child);   break;

            case "right":  border.setRight(child);  break;

            default:       border.setCenter(child); break; // Default é sempre o centro

        }
	}

	private static void TabPane(Object father, Element el) {
		TabPane tabPane = (TabPane) father;

		Node child = el._node;
		
        Tab tab = new Tab(child.getId(), child);

        tabPane.getTabs().add(tab);
	}
	
	private static void SplitPane(Object father, Element el) {
		SplitPane split = (SplitPane) father;

		Node child = el._node;
		
        if (!split.getItems().contains(child)) {

        	split.getItems().add(child);

        }
	}
	
	private static void ScrollPane(Object father, Element el) {
		ScrollPane scrollpane = (ScrollPane) father;

		Node child = el._node;
		
    	scrollpane.setContent(child);
	}
	
	private static void TitledPane(Object father, Element el) {
		TitledPane title = (TitledPane) father;
		
		Node child = el._node;

        title.setContent(child);
	}
	
	@FunctionalInterface
	public interface Linker { void link( Object father, Element el ); }
	
	@FunctionalInterface
	public interface Unlinker { void unlink( Node node ); }

}
