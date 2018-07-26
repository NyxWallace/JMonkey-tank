package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.networkcontrols.TransmitterControl;
import ch.supsi.gamedev.tank3d.controls.physicscontrols.DeadTankControl;
import ch.supsi.gamedev.tank3d.messages.RigidBodyMessage;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class DeadTankTurretTxControl extends TransmitterControl<RigidBodyMessage> {

	private DeadTankControl deadTankControl = null;
	private RigidBodyControl rigidBodyControl = null;
	private boolean initialized = false;
	private boolean done = false;

	private void initialize() {
		Node turret = spatial instanceof Node ? (Node) spatial : null;
		Node deadTank = turret != null ? turret.getParent() : null;
		deadTankControl = deadTank != null ? deadTank.getControl(DeadTankControl.class) : null;
		if (deadTankControl == null) {
			return;
		}
		rigidBodyControl = turret != null ? turret.getControl(RigidBodyControl.class) : null;
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
		if (!done && deadTankControl.isExploded()) {
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
