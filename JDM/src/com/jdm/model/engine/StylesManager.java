package com.jdm.model.engine;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.jdm.meta.Styles;

import javafx.scene.Node;

public class StylesManager {
	
	private static final Method[] mStyle = Styles.class.getDeclaredMethods();
	
	private static final Set<String> ignore = new HashSet<String>();
	
	static {
		
		ignore.add("from");
		
		ignore.add("state");
		
		ignore.add("target");
		
	}
	
	public static StringBuilder load(Node node, Styles[] styles) {

		StringBuilder css = new StringBuilder();
		
		try {
			
			String id = String.format("#%s", node.getId());
			
			for (Styles s : styles) {
		
				css.append(String.format("%s%s {", id, select(s)));
				
				for (Method m : mStyle) {

		        	String name = m.getName();

		        	if ( ignore.contains(name) ) {

		        		continue;

		        	}

		            Object value = m.invoke(s);

		            if ( value == null || value.toString().isEmpty() ) {

		            	continue;

		            }
		            
		            name = m.getName().replace("_", "-");
		            
		            css.append(String.format("%n    -fx-%s:%s;", name, value));

		        }
				
		        css.append("\n}\n");

			}
			
		} catch (Exception e) { e.printStackTrace(); }
		
		return css;
		
	}

	private static String select(Styles s) {
    	
    	String from = s.from().replace(" ", "");
    	
    	String state = s.state();
    	
    	String target = s.target();
    	
    	target = target.isEmpty() ? target : " " + target;
    	
    	state = state.isEmpty() ? state : (state.startsWith(":") ? state : ":"+state);
    	 
    	return new StringBuilder().append(from).append(state).append(target).toString();
    }
	
}