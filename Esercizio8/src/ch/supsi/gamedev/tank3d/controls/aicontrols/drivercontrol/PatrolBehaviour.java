package ch.supsi.gamedev.tank3d.controls.aicontrols.drivercontrol;

import ch.supsi.gamedev.tank3d.Event;
import ch.supsi.gamedev.tank3d.controls.aicontrols.Behaviour;
import ch.supsi.gamedev.tank3d.controls.aicontrols.HelmControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.HelmControl.Speed;
import ch.supsi.gamedev.tank3d.controls.aicontrols.NavigationControl;
import ch.supsi.gamedev.tank3d.controls.aicontrols.WarningControl;
import com.jme3.math.Vector3f;

public class PatrolBehaviour extends Behaviour {

    private final HelmControl helmControl;
    private final WarningControl warningControl;
    private final NavigationControl navigationControl;
    private final Vector3f[] waypoints;
    private int goal = 0;

    public PatrolBehaviour(HelmControl helmControl, WarningControl warningControl, NavigationControl navigationControl, Vector3f[] waypoints) {
        this.helmControl = helmControl;
        this.warningControl = warningControl;
        this.navigationControl = navigationControl;
        this.waypoints = waypoints;
    }

    @Override
    public void start() {
        helmControl.setEnabled(true);
        warningControl.setEnabled(true);
        navigationControl.setEnabled(true);
        navigationControl.plotCourse(waypoints[goal]);
        helmControl.setSpeed(Speed.CRUISE);
    }

    @Override
    public void loop(float tpf) {
        if (!navigationControl.onCourse()) {
            goal = (++goal) % navigationControl.getWaypointsCount();
            navigationControl.plotCourse(waypoints[goal]);
        }
        
        helmControl.setHeading(navigationControl.getApproachAzimuth());
        float dist = helmControl.getSpatial().getWorldTranslation().subtract(waypoints[goal]).length();
        //System.out.println("Distance: " + dist);
        //System.out.println("Speed: " + helmControl.getSpeed());
        if(navigationControl.getDistance() < 70.f){
            helmControl.setSpeed(Speed.SLOW);
        }else{
            helmControl.setSpeed(Speed.CRUISE);
        }
        if(warningControl.getDanger() != null){
            getManager().setCurrentBehaviour(EvadeBehaviour.class);
        }


    }

    @Override
    public void end() {
        helmControl.setEnabled(false);
        warningControl.setEnabled(false);
        navigationControl.setEnabled(false);
    }

    @Override
    public void event(Event event) {
    }
}
