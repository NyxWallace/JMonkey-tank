package ch.supsi.gamedev.tank3d.controls;

import ch.supsi.gamedev.tank3d.controls.listeners.EventListener;
import ch.supsi.gamedev.tank3d.Event;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class DynamicMusicControl extends AbstractControl implements EventListener, Cloneable {

	private static enum State {

		CALM(0.0f), DANGER(300.0f);
		private final float lowerLimit;

		private State(float lowerLimit) {
			this.lowerLimit = lowerLimit;
		}
		
		public float lowerLimit() {
			return lowerLimit;
		}
	}
	//Defaults
	private static final float DEFAULT_TENSION_REDUCTION_SPEED = 1.0f;
	//Properties
	private float tensionReductionSpeed = 1.0f;
	//States
	private float tension = 0.0f;
	private State state = null;
	//SceneGraph
	private MusicControl musicControl = null;
	boolean initialized = false;
	
	private State computeState() {
		State result = null;
		for (State state : State.values()) {
			if (state.lowerLimit() > tension) {
				return result;
			}
			result = state;
		}
		return result;
	}
	
	private void initialize() {
		musicControl = spatial.getControl(MusicControl.class);
		if (musicControl == null) {
			return;
		}
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		tension -= tensionReductionSpeed * tpf;
		tension = FastMath.clamp(tension, 0.0f, 1000.0f);
		State newState = computeState();
		if (newState != null && !newState.equals(state)) {
			state = newState;
			switch (state) {
				case CALM:
					musicControl.play(0);
					break;
				case DANGER:
					musicControl.play(1);
					break;
			}
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void event(Event event) {
		switch (event.id()) {
			case "projectile fired":
				tension += 10.0f;
				break;
			case "projectile exploded":
				tension += 10.0f;
				break;
			case "tank damaged":
				tension += 1000.0f;
				break;
			case "tank dead":
				tension -= 1000.0f;
				break;
		}
	}
}
