package ch.supsi.gamedev.tank3d.controls.aicontrols.gunnercontrol;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.BehavioursManager;
import ch.supsi.gamedev.tank3d.controls.aicontrols.FireSolutionControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.StabilityControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl;
import ch.supsi.gamedev.tank3d.controls.listeners.EventListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class GunnerControl extends AbstractControl implements EventListener, Cloneable {
	
	private final BehavioursManager behavioursManager = new BehavioursManager();
	
	// SceneGraph
	private TankControl tankControl = null;
	private StabilityControl stabilityControl = null;
	private FireSolutionControl fireSolutionControl = null;
	private WarningControl warningControl = null;
	private boolean initialized = false;
	
	private void initialize() {
		tankControl = spatial.getControl(TankControl.class);
		if (tankControl == null) {
			return;
		}		
		stabilityControl = spatial.getControl(StabilityControl.class);
		if (stabilityControl == null) {
			return;
		}
		fireSolutionControl = spatial.getControl(FireSolutionControl.class);
		if (fireSolutionControl == null) {
			return;
		}
		warningControl = spatial.getControl(WarningControl.class);
		if (warningControl == null) {
			return;
		}
		
		behavioursManager.clear();
		behavioursManager.addBehaviour(new IdleBehaviour(tankControl, warningControl, stabilityControl));
		behavioursManager.addBehaviour(new ScanBehaviour(warningControl, stabilityControl));
		behavioursManager.addBehaviour(new EngageBehaviour(tankControl, warningControl, fireSolutionControl, stabilityControl));
		behavioursManager.setCurrentBehaviour(IdleBehaviour.class);
		initialized = true;
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		
		behavioursManager.update(tpf);
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public BehavioursManager getBehavioursManager() {
		return behavioursManager;
	}

	@Override
	public void event(Event event) {
		behavioursManager.event(event);
	}
}
