package ch.supsi.gamedev.tank3d.controls.aicontrols.gunnercontrol;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.aicontrols.Behaviour;
import ch.supsi.gamedev.tank3d.controls.aicontrols.StabilityControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl;

public class ScanBehaviour extends Behaviour {

	private final WarningControl warningControl;
	private final StabilityControl stabilityControl;
        private int time = 0;

	public ScanBehaviour(WarningControl warningControl, StabilityControl stabilityControl) {
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
            time++;
            if(time > 150){
                time = 0;
                float randomAzimuth = (float)(Math.random()*(double)360);
                stabilityControl.setAzimuth(randomAzimuth);
            }
            if(warningControl.getDanger() != null){
                getManager().setCurrentBehaviour(EngageBehaviour.class);
                System.out.println("Engaging enemy");
            }
	}

	@Override
	public void end() {
            warningControl.setEnabled(false);
            stabilityControl.setEnabled(false);
	}

	@Override
	public void event(Event event) {
	}
}
