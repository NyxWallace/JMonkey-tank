package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.networkcontrols.TransmitterControl;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.PhysicsProjectileControl;
import ch.supsi.gamedev.tank3d.messages.RigidBodyMessage;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class ProjectileTxControl extends TransmitterControl<RigidBodyMessage> {

	private PhysicsProjectileControl physicsProjectileControl = null;
	private RigidBodyControl rigidBodyControl = null;
	private boolean initialized = false;
	private boolean done = false;

	private void initialize() {
		physicsProjectileControl = spatial.getControl(PhysicsProjectileControl.class);
		if (physicsProjectileControl == null) {
			return;
		}
		rigidBodyControl = spatial.getControl(RigidBodyControl.class);
		if (rigidBodyControl == null) {
			return;
		}
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		super.controlUpdate(tpf);
		if (!initialized) {
			initialize();
			return;
		}
		if (!done && physicsProjectileControl.isRunning()) {
			Vector3f physicsLocation = rigidBodyControl.getPhysicsLocation();
			Quaternion physicsRotation = rigidBodyControl.getPhysicsRotation();
			Vector3f linearVelocity = rigidBodyControl.getLinearVelocity();
			Vector3f angularVelocity = rigidBodyControl.getAngularVelocity();
			RigidBodyMessage rigidBodyMessage = new RigidBodyMessage(physicsLocation, physicsRotation, linearVelocity, angularVelocity);
			rigidBodyMessage.setReliable(true);
			send(rigidBodyMessage);
			done = true;
		}
	}
}
