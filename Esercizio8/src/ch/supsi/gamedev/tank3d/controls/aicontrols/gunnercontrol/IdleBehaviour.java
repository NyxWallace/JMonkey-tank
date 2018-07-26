package ch.supsi.gamedev.tank3d.controls.aicontrols.gunnercontrol;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.Behaviour;
import ch.supsi.gamedev.tank3d.controls.aicontrols.StabilityControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl;
import ch.supsi.gamedev.tank3d.events.ProjectileEvent;
import ch.supsi.gamedev.tank3d.events.ProjectileEvent.Type;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class IdleBehaviour extends Behaviour {

	private final TankControl tankControl;
	private final WarningControl warningControl;
	private final StabilityControl stabilityControl;

	public IdleBehaviour(TankControl tankControl, WarningControl warningControl, StabilityControl stabilityControl) {
		this.tankControl = tankControl;
		this.warningControl = warningControl;
		this.stabilityControl = stabilityControl;
	}

	@Override
	public void start() {
            warningControl.setEnabled(true);
            stabilityControl.setEnabled(true);
	}

	@Override
	public void loop(float tpf) {
            Vector3f orientation = tankControl.getVelocity().normalize();
            float sign = FastMath.sign(Vector3f.UNIT_Z.cross(orientation).y);
            float azimuth = orientation.angleBetween(Vector3f.UNIT_Z) * FastMath.RAD_TO_DEG * sign;
            stabilityControl.setAzimuth(azimuth);
            if(warningControl.getDanger() != null)
                getManager().setCurrentBehaviour(EngageBehaviour.class);
	}

	@Override
	public void end() {
            warningControl.setEnabled(false);
            stabilityControl.setEnabled(false);
	}

	@Override
	public void event(Event event) {
            if(event instanceof ProjectileEvent){
                if(((ProjectileEvent)event).type() == Type.FIRED){
                    getManager().setCurrentBehaviour(ScanBehaviour.class);
                }
            }
	}
}
