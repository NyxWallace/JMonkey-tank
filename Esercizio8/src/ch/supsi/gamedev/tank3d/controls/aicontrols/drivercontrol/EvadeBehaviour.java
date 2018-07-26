package ch.supsi.gamedev.tank3d.controls.aicontrols.drivercontrol;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.aicontrols.Behaviour;
import ch.supsi.gamedev.tank3d.controls.aicontrols.HelmControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.HelmControl.Speed;
import ch.supsi.gamedev.tank3d.controls.aicontrols.NavigationControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl;
import static ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl.Cardinal.BACK;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl.Danger;

public class EvadeBehaviour extends Behaviour {

    private final HelmControl helmControl;
    private final WarningControl warningControl;
    private final NavigationControl navigationControl;

    public EvadeBehaviour(HelmControl helmControl, WarningControl warningControl, NavigationControl navigationControl) {
        this.helmControl = helmControl;
        this.warningControl = warningControl;
        this.navigationControl = navigationControl;
    }

    @Override
    public void start() {
        helmControl.setEnabled(true);
        warningControl.setEnabled(true);
        navigationControl.setEnabled(true);
    }

    @Override
    public void loop(float tpf) {
        Danger dang = warningControl.getDanger();
        if(dang == null){
            getManager().setCurrentBehaviour(PatrolBehaviour.class);
            return;
        }
        
        switch(dang.direction()){
            case LEFT:
            case RIGHT:
                helmControl.setSpeed(Speed.FULL);
                break;
            case BACK:
            case FRONT:
                helmControl.setHeading(helmControl.getCurrentHeading()+90.0f);
                helmControl.setSpeed(Speed.FULL);
                break;
        }
    }

    @Override
    public void end() {
    }

    @Override
    public void event(Event event) {
    }
}
