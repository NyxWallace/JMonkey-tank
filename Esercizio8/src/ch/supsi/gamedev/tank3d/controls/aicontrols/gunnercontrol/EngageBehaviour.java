package ch.supsi.gamedev.tank3d.controls.aicontrols.gunnercontrol;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.TankControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.Behaviour;
import ch.supsi.gamedev.tank3d.controls.aicontrols.FireSolutionControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.StabilityControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl;
import ch.supsi.gamedev.tank3d.events.TankEvent;
import ch.supsi.gamedev.tank3d.events.TankEvent.Type;

public class EngageBehaviour extends Behaviour {
	
	private final TankControl tankControl;
	private final WarningControl warningControl;
	private final FireSolutionControl fireSolutionControl;
	private final StabilityControl stabilityControl;
	
	public EngageBehaviour(TankControl tankControl, WarningControl warningControl, FireSolutionControl fireSolutionControl, StabilityControl stabilityControl) {
		this.tankControl = tankControl;
		this.warningControl = warningControl;
		this.fireSolutionControl = fireSolutionControl;
		this.stabilityControl = stabilityControl;
	}

	@Override
	public void start() {
            warningControl.setEnabled(true);
            fireSolutionControl.setEnabled(true);
            stabilityControl.setEnabled(true);
            fireSolutionControl.setTarget(warningControl.getAttacker());
	}

	@Override
	public void loop(float tpf) {
            stabilityControl.setAzimuth(fireSolutionControl.getAzimuth());
            stabilityControl.setElevation(fireSolutionControl.getElevation());
            if(fireSolutionControl.isValid() && fireSolutionControl.isVisible())
                tankControl.fire();
	}

	@Override
	public void end() {
            warningControl.setEnabled(false);
            fireSolutionControl.setEnabled(false);
            stabilityControl.setEnabled(false);
	}

	@Override
	public void event(Event event) {
            if(event instanceof TankEvent)
                if(((TankEvent)event).type() == Type.DEAD)
                    getManager().setCurrentBehaviour(IdleBehaviour.class);
	}
}
