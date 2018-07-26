package ch.supsi.gamedev.tank3d.controls.physicscontrols;

import ch.supsi.gamedev.tank3d.controls.listeners.PhysicsListener;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class DeadTankControl extends AbstractControl implements PhysicsListener, Cloneable {

	private Node deadTank = null;
	private Node turret = null;
	private Node cannon = null;
	private Node originalTank = null;
	private RigidBodyControl rigidBodyControl = null;
	private RigidBodyControl turretRigidBodyControl = null;
	private boolean initialized = false;
	private boolean exploded = false;

	private void initialize() {
		if (originalTank == null) {
			return;
		}

		rigidBodyControl = deadTank.getControl(RigidBodyControl.class);
		if (rigidBodyControl == null) {
			return;
		}


		turretRigidBodyControl = turret.getControl(RigidBodyControl.class);
		if (turretRigidBodyControl == null) {
			return;
		}

		Node originalTurret = Utils.getChild(originalTank, "turret", Node.class);
		if (originalTurret != null) {
			turret.setLocalTransform(originalTurret.getLocalTransform());
		}

		Node originalCannon = Utils.getChild(originalTurret, "cannon", Node.class);
		if (originalCannon != null) {
			cannon.setLocalTransform(originalCannon.getLocalTransform());
		}

		CompoundCollisionShape compoundCollisionShape = new CompoundCollisionShape();
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 1.1f, 6.0f)), new Vector3f(0.0f, 0.25f, 0.0f));
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 1.1f, 6.0f)), new Vector3f(0.0f, 0.25f, 0.0f));
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 0.5f, 1.5f)), new Vector3f(-3.5f, -0.25f, -2.5f));
		compoundCollisionShape.addChildShape(new BoxCollisionShape(new Vector3f(1.5f, 0.5f, 1.5f)), new Vector3f(3.5f, -0.25f, -2.5f));
		rigidBodyControl.setCollisionShape(compoundCollisionShape);

		CompoundCollisionShape turretCompoundCollisionShape = new CompoundCollisionShape();
		turretCompoundCollisionShape.addChildShape(new CylinderCollisionShape(new Vector3f(1.75f, 0.5f, 1.75f), 1), new Vector3f(0.0f, 0.5f, 0.0f));
		Matrix3f rotationMatrix = cannon.getLocalRotation().toRotationMatrix();
		Vector3f position = new Vector3f(0.0f, 0.4f, 1.75f);
		position.addLocal(rotationMatrix.getColumn(2).mult(3.0f));
		turretCompoundCollisionShape.addChildShape(new CylinderCollisionShape(new Vector3f(0.5f, 0.5f, 3.0f), 2), position, rotationMatrix);

		turretRigidBodyControl.setCollisionShape(turretCompoundCollisionShape);
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (deadTank == null || turret == null || cannon == null) {
			return;
		}
		if (!initialized) {
			initialize();
			return;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		deadTank = spatial instanceof Node ? (Node) spatial : null;
		turret = Utils.getChild(deadTank, "turret", Node.class);
		cannon = Utils.getChild(turret, "cannon", Node.class);
	}

	public void setOriginalTank(Node originalTank) {
		this.originalTank = originalTank;
	}

	@Override
	public void physicsUpdate(float tpf) {
		if (!initialized || exploded || originalTank == null) {
			return;
		}
		RigidBodyControl tankRigidBodyControl = originalTank.getControl(RigidBodyControl.class);
		Vector3f linearVelocity = tankRigidBodyControl.getLinearVelocity();
		Vector3f angularVelocity = tankRigidBodyControl.getAngularVelocity();
		rigidBodyControl.setLinearVelocity(linearVelocity);
		rigidBodyControl.setAngularVelocity(angularVelocity);
		turretRigidBodyControl.setLinearVelocity(linearVelocity);
		turretRigidBodyControl.setAngularVelocity(angularVelocity);
		Vector3f turretUpVector = turret.getWorldRotation().getRotationColumn(1);
		Vector3f randomVector = Utils.randomUnitVector().mult(0.3f);
		turretRigidBodyControl.applyImpulse(turretUpVector.mult(30000.0f), turretUpVector.mult(-0.5f).add(randomVector));
		exploded = true;
	}
}
