package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Environment;
import com.jme3.audio.Listener;
import com.jme3.audio.LowPassFilter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AudioManager extends AbstractAppState {

	public static class Sound {

		private final AudioNode audioNode;
		private final Vector3f position;
		private float time = 0.0f;

		public Sound(AudioNode audioNode, Vector3f position) {
			this.audioNode = audioNode;
			this.position = position;
		}
		
		public AudioNode audioNode() {
			return audioNode;
		}
		
		public Vector3f position() {
			return position;
		}
		
		public float time() {
			return time;
		}
		
		public void update(float tpf) {
			time += tpf;
		}
	}
	private static final float DEFAULT_SOUND_SPEED = 343.2f; // WU/sec
	private static final float DEFAULT_MIN_FILTER_DISTANCE = 100.0f; // WU
	private static final float DEFAULT_MAX_FILTER_DISTANCE = 500.0f; // WU
	private final Set<Sound> sounds = new HashSet<>();
	private AudioRenderer audioRenderer = null;
	private float soundSpeed = DEFAULT_SOUND_SPEED;
	private float minFilterDistance = DEFAULT_MIN_FILTER_DISTANCE;
	private float maxFilterDistance = DEFAULT_MAX_FILTER_DISTANCE;
	private Camera camera = null;
	private Listener listener = null;
	private Vector3f lastListenerPosition = null;
	
	private float distance(Sound sound) {
		Vector3f listenerPosition = listener.getLocation();
		Vector3f soundPosition = sound.position();
		Vector3f vector = soundPosition.subtract(listenerPosition);
		return vector.length();
	}
	
	private boolean canHear(Sound sound) {
		float time = sound.time();
		float distance = distance(sound);
		float hearTime = distance / soundSpeed;
		return hearTime < time;
	}

	public float getSoundSpeed() {
		return soundSpeed;
	}

	public void setSoundSpeed(float soundSpeed) {
		this.soundSpeed = soundSpeed;
	}

	public float getMinFilterDistance() {
		return minFilterDistance;
	}

	public void setMinFilterDistance(float minFilterDistance) {
		this.minFilterDistance = minFilterDistance;
	}

	public float getMaxFilterDistance() {
		return maxFilterDistance;
	}

	public void setMaxFilterDistance(float maxFilterDistance) {
		this.maxFilterDistance = maxFilterDistance;
	}
	
	public void addSound(AudioNode audioNode) {
		Sound sound = new Sound(audioNode, audioNode.getPosition());
		sounds.add(sound);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		if (application instanceof SimpleApplication) {
			SimpleApplication simpleApplication = (SimpleApplication) application;
			camera = simpleApplication.getCamera();
			listener = simpleApplication.getListener();
			audioRenderer = simpleApplication.getAudioRenderer();
			audioRenderer.setEnvironment(Environment.Dungeon);
		}
	}

	@Override
	public void update(float tpf) {
		if (camera == null || listener == null) {
			return;
		}
		
		Iterator<Sound> iterator = sounds.iterator();
		while (iterator.hasNext()) {
			Sound sound = iterator.next();
			sound.update(tpf);
			if (canHear(sound)) {
				float distance = distance(sound);
				float filterAmount = Utils.normalize(distance, minFilterDistance, maxFilterDistance);
				sound.audioNode().setReverbFilter(new LowPassFilter(1.0f, 1.0f - filterAmount));
				sound.audioNode().playInstance();
				iterator.remove();
			}
		}
		
		Vector3f velocity = Vector3f.ZERO;
		Vector3f listenerPosition = new Vector3f(camera.getLocation());
		if (lastListenerPosition != null) {
			velocity = listenerPosition.subtract(lastListenerPosition).divideLocal(tpf);
		}
		listener.setVelocity(velocity);
		listener.setLocation(camera.getLocation());
		listener.setRotation(camera.getRotation());
	}
}
