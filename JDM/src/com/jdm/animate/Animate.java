package com.jdm.animate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animate {

	double value;

	Target target;
	
	public static KeyFrame keyframe(Node node, Duration time, Animate... animations) {
		Map<Target, Double> map = new HashMap<>();

		for ( Animate a : animations ) {
			if (a.target != Target.scale) map.put( a.target, a.value);
			else {
				map.put(Target.scaleX, a.value);
				map.put(Target.scaleY, a.value);
			}
		}

		List<KeyValue> list = new ArrayList<>();

		map.forEach( (k, v) -> {
			list.add( load( node, k, v ) );
		});
		
		return new KeyFrame(time, (KeyValue[]) list.toArray());
	}
	
	private static KeyValue load(Node node, Target k, Double v) {
		switch (k) {
			case scaleX    : return new KeyValue(node.translateXProperty(), v);
			case scaleY    : return new KeyValue(node.translateXProperty(), v);

			case rotate    : return new KeyValue(node.rotateProperty(), v);
			case opacity   : return new KeyValue(node.opacityProperty(), v);

			case translateX: return new KeyValue(node.translateXProperty(), v);
			case translateY: return new KeyValue(node.translateYProperty(), v);
			case translateZ: return new KeyValue(node.translateZProperty(), v);

			case visible   : return new KeyValue(node.visibleProperty(), v == 0 ? true : false);
			
			default: return null;
		}
		
	}

	public static Animate translateX(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate translateY(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate translateZ(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate scale(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate scaleX(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate scaleY(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate rotate(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate opacity(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Animate visible(int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
