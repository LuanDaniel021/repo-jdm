package com.jdm.animate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class Animation {

	private final Timeline timeline;

	public Animation() { this( new Timeline() ); }

	public Animation( Timeline timeline ) {
		this.timeline = timeline;
	}

	public void timeline(KeyFrame... frames) {
		timeline.getKeyFrames().addAll(frames);
	}

	public void play() { timeline.play(); }

	public void stop() { timeline.stop();  }

}
