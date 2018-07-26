package ch.supsi.gamedev.tank3d;

import com.jme3.effect.shapes.EmitterShape;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import java.io.IOException;

public class EmitterCircleShape implements EmitterShape {
	
	private static final Vector3f DEFAULT_NORMAL = Vector3f.UNIT_Y;
	private static final float DEFAULT_RADIUS = 3.0f;
	private static final float DEFAULT_RANGE = 1.0f;
	
	private final Transform transform = new Transform();
	private Vector3f normal = DEFAULT_NORMAL;
	private float radius = DEFAULT_RADIUS;
	private float range = DEFAULT_RANGE;
	
	private void updateTransform() {
		Quaternion rotation = new Quaternion();
		float angle = FastMath.cos(normal.getY());
		Vector3f axis = normal.cross(Vector3f.UNIT_Y);
		rotation.fromAngleAxis(angle, axis);
		transform.setRotation(rotation);
	}

	public EmitterCircleShape() {
		updateTransform();
	}
		
	public EmitterCircleShape(Vector3f normal, float radius, float range) {
		this.normal = normal;
		this.radius = radius;
		this.range = range;
		updateTransform();
	}

	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
		updateTransform();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	@Override
	public void getRandomPoint(Vector3f result) {
		float angle = FastMath.nextRandomFloat() * FastMath.TWO_PI;
		float localRadius = radius + (FastMath.nextRandomFloat() - 0.5f) * range;
		Vector3f vector = new Vector3f(FastMath.cos(angle), 0.0f, FastMath.sin(angle));
		vector.multLocal(localRadius);
		transform.transformVector(vector, result);
	}

	@Override
	public void getRandomPointAndNormal(Vector3f resultPoint, Vector3f resultNormal) {
		getRandomPoint(resultPoint);
		resultNormal.set(normal);
	}

	@Override
	public EmitterShape deepClone() {
		return new EmitterCircleShape(new Vector3f(normal), radius, range);
	}

	@Override
	public void read(JmeImporter importer) throws IOException {
		InputCapsule capsule = importer.getCapsule(this);
		normal = (Vector3f) capsule.readSavable("normal", DEFAULT_NORMAL);
		radius = capsule.readFloat("radius", DEFAULT_RADIUS);
		range = capsule.readFloat("range", DEFAULT_RANGE);
	}

	@Override
	public void write(JmeExporter exporter) throws IOException {
		OutputCapsule capsule = exporter.getCapsule(this);
		capsule.write(normal, "normal", DEFAULT_NORMAL);
		capsule.write(radius, "radius", DEFAULT_RADIUS);
		capsule.write(range, "range", DEFAULT_RANGE);
	}
}
