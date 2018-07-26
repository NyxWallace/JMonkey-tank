package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.Globals;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;

public class MusicControl extends AbstractControl implements Cloneable {

	private static enum State {

		STOPPED, PLAYING, TRANSITION
	}
	//Defaults
	private static float DEFAULT_TRANISTION_DURATION = 5.0f;
	//Properties
	private float transitionDuration = DEFAULT_TRANISTION_DURATION;
	//States
	private State state = State.STOPPED;
	private float transitionTime = 0.0f;
	//SceneGraph
	private final List<AudioNode> tracks = new ArrayList<>();
	private AudioNode previousTrack = null;
	private AudioNode currentTrack = null;

	private void updateTransition(float tpf) {
		float factor = transitionTime / transitionDuration;
		previousTrack.setVolume(1.0f - factor);
		currentTrack.setVolume(factor);
		transitionTime += tpf;
		if (transitionTime > transitionDuration) {
			if (Globals.SINGLETON.hasAudioRenderer()) {
				previousTrack.stop();
			}
			transitionTime = 0.0f;
			state = State.PLAYING;
		}
	}

	@Override
	protected void controlUpdate(float tpf) {
		switch (state) {
			case STOPPED:
				break;
			case PLAYING:
				break;
			case TRANSITION:
				updateTransition(tpf);
				break;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		Node node = spatial instanceof Node ? (Node) spatial : null;
		List<Spatial> children = node != null ? node.getChildren() : null;
		tracks.clear();
		if (children != null) {
			for (Spatial child : children) {
				if (child instanceof AudioNode) {
					AudioNode audioNode = (AudioNode) child;
					tracks.add(audioNode);
				}
			}
		}
	}

	public void play() {
		switch (state) {
			case STOPPED:
				if (tracks.size() > 0) {
					currentTrack = tracks.get(0);
					if (Globals.SINGLETON.hasAudioRenderer()) {
						currentTrack.play();
					}
					state = State.PLAYING;
				}
				break;
			case PLAYING:
				break;
			case TRANSITION:
				break;
		}
	}

	public void play(int trackIndex) {
		switch (state) {
			case STOPPED:
				if (tracks.size() > trackIndex) {
					currentTrack = tracks.get(trackIndex);
					if (Globals.SINGLETON.hasAudioRenderer()) {
						currentTrack.play();
					}
					state = State.PLAYING;
				}
				break;
			case PLAYING:
				if (tracks.size() > trackIndex) {
					previousTrack = currentTrack;
					currentTrack = tracks.get(trackIndex);
					if (Globals.SINGLETON.hasAudioRenderer()) {
						currentTrack.play();
					}
					state = State.TRANSITION;
				}
				break;
			case TRANSITION:
				if (tracks.size() > trackIndex) {
					if (Globals.SINGLETON.hasAudioRenderer()) {
						previousTrack.stop();
					}
					previousTrack = currentTrack;
					currentTrack = tracks.get(trackIndex);
					if (Globals.SINGLETON.hasAudioRenderer()) {
						currentTrack.play();
					}
					state = State.TRANSITION;
				}
				break;
		}
	}

	public void stop() {
		switch (state) {
			case STOPPED:
				break;
			case PLAYING:
				if (Globals.SINGLETON.hasAudioRenderer()) {
					currentTrack.stop();
				}
				currentTrack = null;
				state = State.STOPPED;
				break;
			case TRANSITION:
				if (Globals.SINGLETON.hasAudioRenderer()) {
					currentTrack.stop();
					previousTrack.stop();
				}
				currentTrack = null;
				previousTrack = null;
				state = State.STOPPED;
				break;
		}
	}
}
