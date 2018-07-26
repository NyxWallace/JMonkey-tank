package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.networkcontrols.RigidBodyRxControl;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.PhysicsProjectileControl;

public class ProjectileRxControl extends RigidBodyRxControl {

	private PhysicsProjectileControl physicsProjectileControl = null;
	private boolean initialized = false;

	private void initialize() {
		physicsProjectileControl = spatial.getControl(PhysicsProjectileControl.class);
		if (physicsProjectileControl == null) {
			return;
		}
		initialized = true;
	}
	
	@Override
	protected boolean isReady() {
		return physicsProjectileControl != null && physicsProjectileControl.isRunning();
	}

	@Override
	protected void controlUpdate(float tpf) {
		super.controlUpdate(tpf);
		if (!initialized) {
			initialize();
			return;
		}
	}
}
