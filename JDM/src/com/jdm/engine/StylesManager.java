package com.jdm.engine;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.jdm.meta.Style;
import com.jdm.model.Element;

public class StylesManager {
	
	private static final Method[] mStyle = Style.class.getDeclaredMethods();
	
	private static final Set<String> ignore = new HashSet<String>();
	
	static {
		
		ignore.add("from");
		
		ignore.add("state");
		
		ignore.add("target");
		
	}
	
	public static void load(Element el, Style[] styles) {

		try {
		
			StringBuilder css = el.styles;
			
			String id = String.format("#%s", el.node.getId());
			
			for (Style s : styles) {
		
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
		
	}

	private static String select(Style s) {
    	
    	String from = s.from().replace(" ", "");
    	
    	String state = s.state();
    	
    	String target = s.target();
    	
    	target = target.isEmpty() ? target : " " + target;
    	
    	state = state.isEmpty() ? state : (state.startsWith(":") ? state : ":"+state);
    	 
    	return new StringBuilder().append(from).append(state).append(target).toString();
    }
	
}