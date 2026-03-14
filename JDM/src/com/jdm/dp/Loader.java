package com.jdm.dp;

import java.util.List;

import com.jdm.meta.Class;
import com.jdm.meta.ID;
import com.jdm.meta.Image;
import com.jdm.meta.Layout;
import com.jdm.meta.Root;
import com.jdm.meta.Styles;
import com.jdm.meta.Wari;
import com.jdm.model.Document;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Loader extends Application {

	static class Doc {
		
		@Styles( background_color = "gray" )
		@Root HBox login = new HBox() {

			@ID("left-pane")
			@Class({"CAIXA"})
			@Styles(
				background_color = "#2c3e50",
				spacing = "20",
				padding = "40",
				fill_height = "true",
				alignment = "center",
				hgrow = "always",
				max_width = "10000"
			)
			@Styles(
				target = "Region",
				background_color = "red"
			)
			VBox left = new VBox()  {
				
				@Image(
			    	url = "https://cdn-icons-png.flaticon.com/512/5087/5087579.png",
			    	height = 120,
			    	preserve_ratio = true
			    )
			    ImageView logo;
				
				class Welcome extends Label {

					{ setText("Bem vindo!!!"); }
					
				}
				@Styles (
			    	text_fill = "#ffffff", 
			    	font_size = "22",
			    	font_weight = "BOLD"
			    )
				Welcome welcome;
		
			    Region spacer = new Region();
			    
			    @Styles ( text_fill = "#d3d3d3", font_size = "11" )
			    Label footer = new Label("@2026 - JAS V1.20.0");
			    { // INIT
					setHgrow(this, Priority.ALWAYS);
					setVgrow(spacer, Priority.ALWAYS);
				}
			};
			
			@Styles(
				spacing = "25",
				background_color = "#ecf0f1", 
				padding = "40",
				font_style = "italic",
				alignment = "center"
			)
			VBox right = new VBox() {

				{ setHgrow(this, Priority.ALWAYS); }
				
				@Layout(column=1)
				public List<Button> botoesLaterais; // vai ser ignorado
				
				@Class({"TESTE"})
				@Styles(
					text_fill = "#2c3e50",
					font_size = "30",
					font_weight = "BOLD",
					text_alignment = "LEFT"
				)
				Label title = new Label("LOGIN");
				
				@Styles(spacing = "12", alignment = "center-left")
			    VBox fields = new VBox() {

			    	{ setHgrow(this, Priority.ALWAYS); }
					
			    	@Styles (
				    	text_fill = "#2c3e50",
				    	font_size = "13px"
				    )
					Label lblUser = new Label("Úsuario");
			
					@Styles(
				    	border_radius = "6",
				    	background_radius = "6",
				    	font_size = "14",
				    	text_fill = "#2c3e50",
				    	height = "40"
				    )
				    TextField txtUser;
			
				    @Styles(
				    	font_style = "italic",
				    	text_fill = "#2c3e50",
				    	font_size = "13"
				    )
				    Label lblPass = new Label("Senha");
			
				    @Styles(
				    	border_radius = "6",
				    	background_radius = "6",
				    	font_size = "14",
				    	text_fill = "#2c3e50",
				    	height = "40"
				    )
				    PasswordField txtPass;
				};
				
				@ID("meu-botao-id")
				@Class({"CAIXA"})
				@Styles(
					state = "hover",
		    		text_fill = "blue"
		        )
				@Styles(
		    		height = "45",
		    		pref_width = "200",
		    		background_color = "#3498db",
		    		border_width = "1",
		    	    border_radius = "0",
		    	    background_radius = "0",
		    	    font_size = "14==",
		    	    font_weight = "BOLD",
		    		text_fill = "red",
		    		alignment = "center"
		        )
			    Button btnLogin = new Button("Entrar");
				
				@Styles(
		    		height = "45",
		    		pref_width = "200",
		    		text_fill = "red",
		    		alignment = "center"
			        )
				BBB btn1Login;
				
			};
			
		};
		
		@Wari
		TextField txtUser;
		
		@ID("TESTE")
		@Root HBox body = new HBox() {
			@ID("meu-botao-id")
			@Class({"CAIXA"})
			@Styles(
				state = "hover",
	    		text_fill = "blue"
	        )
			@Styles(
	    		height = "45",
	    		pref_width = "200",
	    		background_color = "#3498db",
	    		border_width = "1",
	    	    border_radius = "0",
	    	    background_radius = "0",
	    	    font_size = "14==",
	    	    font_weight = "BOLD",
	    		text_fill = "red",
	    		alignment = "center"
	        )
		    Button btnLogin = new Button("Entrar");
		};
		
		int _root_;

	}
	
	class BBB extends Button {} // erro, vai mostrar uma area em vermelho

	@Override
	public void start(Stage ps) throws Exception {

		Document document = new Document( Doc.class );

		ps.setScene( document.getScene() );

		Doc doc = (Doc) document.getModel();
		
		document.onCreate("body", () -> {
			
			Button btn = (Button) document.getNodeById("meu-botao-id");
			
			btn.setOnAction(e -> {

				document.swap("login", Scene::new, _ctx -> {
					_ctx.stage = ps;
					_ctx.title = "Tela de Login";
					_ctx.height = 500;
					_ctx.width = 750.0;
			    });

			});

		});
		
		document.onCreate("login", () -> {
			
			Button btn = (Button) document.getNodeById("meu-botao-id");
			
			btn.setOnAction(e -> {

				document.swap("body", Scene::new, _ctx -> {
			        ps.setScene( _ctx.scene );
			        ps.setWidth(400);
			        ps.setTitle("Acesso Restrito");
			    });

			});

		});
		
		document.on("login", () -> {
			
			Button btn = (Button) document.getNodeById("meu-botao-id");
			
			btn.setOnAction(e -> {

				document.swap("body", Scene::new, _ctx -> {
			        ps.setScene( _ctx.scene );
			        ps.setWidth(400);
			        ps.setTitle("Acesso Restrito");
			    });

			});

		}, Document.CREATE);

		// swap, funciona
//			document.swap("body", ctx -> {
//				
//				//ctx.root = "asd";
//
//			});
//			
//			document.swap("body", Scene::new);
//			
//			document.swap("body", Scene::new, ctx -> {
//				ctx.width = 1;
//			});
//			
//			ps.setScene( document.getScene() );
		
		System.err.println(doc.txtUser); // wire, funciona... vai mudar

		System.out.println(document.getNodeById("left-pane"));

		System.out.println(document.getNodeClass("CAIXA"));
		
		System.out.println(document.getNodeClassAll("CAIXA"));
		
		System.out.println(document.lookup("CAIXA"));
		

		ps.show();
	}
}
