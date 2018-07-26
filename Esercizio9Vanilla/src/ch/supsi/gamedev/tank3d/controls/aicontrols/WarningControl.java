package ch.supsi.gamedev.tank3d.controls.aicontrols;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.ProjectileControl;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.listeners.EventListener;
import ch.supsi.gamedev.tank3d.events.ProjectileEvent;
import static ch.supsi.gamedev.tank3d.events.ProjectileEvent.Type.EXPLODED;
import static ch.supsi.gamedev.tank3d.events.ProjectileEvent.Type.FIRED;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WarningControl extends AbstractControl implements Cloneable, EventListener {

	public static enum Cardinal {

		FRONT(0.0f), RIGHT(90.0f), BACK(180.0f), LEFT(270.0f);

		private static Cardinal cardinal(float degrees) {
			Cardinal result = null;
			float minDeltaAngleDeg = Float.MAX_VALUE;
			for (Cardinal value : values()) {
				float deltaAngleDeg = FastMath.abs(Utils.deltaAngleDeg(degrees, value.degrees));
				if (deltaAngleDeg < minDeltaAngleDeg) {
					result = value;
					minDeltaAngleDeg = deltaAngleDeg;
				}
			}
			return result;
		}
		private static Cardinal lateralCardinal(float degrees) {
			float leftDeltaAngleDeg = FastMath.abs(Utils.deltaAngleDeg(degrees, LEFT.degrees));
			float rightDeltaAngleDeg = FastMath.abs(Utils.deltaAngleDeg(degrees, RIGHT.degrees));
			return leftDeltaAngleDeg < rightDeltaAngleDeg ? LEFT : RIGHT;
		}
		private static Cardinal frontalCardinal(float degrees) {
			float backDeltaAngleDeg = FastMath.abs(Utils.deltaAngleDeg(degrees, BACK.degrees));
			float frontDeltaAngleDeg = FastMath.abs(Utils.deltaAngleDeg(degrees, FRONT.degrees));
			return backDeltaAngleDeg < frontDeltaAngleDeg ? BACK : FRONT;
		}
		private float degrees;

		private Cardinal(float degrees) {
			this.degrees = degrees;
		}

		public float degrees() {
			return degrees;
		}
		
		public boolean isLateral() {
			return this == LEFT || this == RIGHT;
		}
		
		public boolean isFrontal() {
			return this == BACK || this == FRONT;
		}
	}

	public static class Danger {

		private final Spatial projectile;
		private final Cardinal position;
		private final Cardinal direction;

		public Danger(Spatial projectile, Cardinal position, Cardinal direction) {
			this.projectile = projectile;
			this.position = position;
			this.direction = direction;
		}

		public Spatial projectile() {
			return projectile;
		}

		public Cardinal position() {
			return position;
		}

		public Cardinal direction() {
			return direction;
		}
	}

	private static final float DEFAULT_RADIUS = 16.0f;
	private Set<Spatial> projectiles = null;
	private Set<Danger> dangers = null;
	private float radius = DEFAULT_RADIUS;
	private TankControl tankControl = null;
	private boolean initialized = false;

	private Vector3f relativeVelocity(Spatial projectile) {
		Vector3f velocity = tankControl.getVelocity();
		RigidBodyControl rigidBodyControl = projectile.getControl(RigidBodyControl.class);
		Vector3f projectileVelocity = rigidBodyControl != null ? rigidBodyControl.getLinearVelocity() : Vector3f.ZERO;
		return projectileVelocity.subtract(velocity);
	}

	private Vector3f predictedCollisionPoint(Spatial projectile) {
		Vector3f position = spatial.getWorldTranslation();
		Vector3f projectilePosition = projectile.getWorldTranslation();
		Vector3f relativeVelocity = relativeVelocity(projectile);
		BoundingSphere boundingSphere = new BoundingSphere(radius, position);
		Ray ray = new Ray(projectilePosition, relativeVelocity.normalize());
		CollisionResults collisionResults = new CollisionResults();
		ray.collideWith(boundingSphere, collisionResults);
		CollisionResult closestCollision = collisionResults.getClosestCollision();
		return closestCollision != null ? closestCollision.getContactPoint() : null;
	}
	
	private float degrees(Vector3f vector) {
		Quaternion rotation = spatial.getWorldRotation();
		Vector3f localVector = rotation.inverse().mult(vector);
		Vector2f localVector2 = new Vector2f(localVector.getZ(), -localVector.getX());
		float result = localVector2.getAngle() * FastMath.RAD_TO_DEG;
		return Utils.normalizeAngleDeg(result);
	}

	private Cardinal cardinal(Vector3f vector) {
		Quaternion rotation = spatial.getWorldRotation();
		Vector3f localVector = rotation.inverse().mult(vector);
		Vector2f localVector2 = new Vector2f(localVector.getZ(), -localVector.getX());
		float degrees = localVector2.getAngle() * FastMath.RAD_TO_DEG;
		degrees = Utils.normalizeAngleDeg(degrees);
		return Cardinal.cardinal(degrees);
	}

	private boolean isDangerous(Spatial projectile) {
		return predictedCollisionPoint(projectile) != null;
	}

	private Cardinal direction(Spatial projectile) {
		Vector3f relativeVelocity = relativeVelocity(projectile);
		float degrees = degrees(relativeVelocity);
		return Cardinal.cardinal(degrees);
	}

	private Cardinal position(Spatial projectile, Cardinal direction) {
		Vector3f position = spatial.getWorldTranslation();
		Vector3f collisionPoint = predictedCollisionPoint(projectile);
		if (collisionPoint != null) {
			Vector3f vector = collisionPoint.subtract(position);
			float degrees = degrees(vector);
			switch (direction) {
				case LEFT:
				case RIGHT:
					return Cardinal.frontalCardinal(degrees);
				case BACK:
				case FRONT:
					return Cardinal.lateralCardinal(degrees);
			}
		}
		return null;
	}

	private void initialize() {
		tankControl = spatial.getControl(TankControl.class);
		if (tankControl == null) {
			return;
		}

		projectiles = new HashSet<>();
		dangers = new HashSet<>();
		initialized = true;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!initialized) {
			initialize();
			return;
		}
		dangers.clear();
		for (Spatial projectile : projectiles) {
			boolean dangerous = isDangerous(projectile);
			if (dangerous) {
				Cardinal direction = direction(projectile);
				Cardinal position = position(projectile, direction);
				Danger danger = new Danger(projectile, position, direction);
				dangers.add(danger);
			}
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	
	public Set<Spatial> projectiles() {
		return projectiles;
	}
	
	public Set<Danger> dangers() {
		return dangers;
	}
	
	public Danger getDanger() {
		Iterator<Danger> iterator = dangers.iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}
	
	public Spatial getAttacker() {
		Danger danger = getDanger();
		ProjectileControl projectileControl = danger != null ? danger.projectile().getControl(ProjectileControl.class) : null;
		return projectileControl != null ? projectileControl.getOwner() : null;
	}

	@Override
	public void event(Event event) {
		ProjectileEvent projectileEvent = event instanceof ProjectileEvent ? (ProjectileEvent) event : null;
		if (projectileEvent == null) {
			return;
		}
		Spatial tank = projectileEvent.tank();
		if (spatial.equals(tank)) {
			return;
		}
		ProjectileEvent.Type type = projectileEvent.type();
		Spatial projectile = projectileEvent.projectile();
		switch (type) {
			case FIRED:
				projectiles.add(projectile);
				break;
			case EXPLODED:
				projectiles.remove(projectile);
				break;
		}
	}
}
