package ch.supsi.gamedev.tank3d.controls.multiplayercontrols;

import ch.supsi.gamedev.tank3d.controls.SpawnControl;
import ch.supsi.gamedev.tank3d.controls.networkcontrols.TransmitterControl;
import ch.supsi.gamedev.tank3d.messages.actionmessages.SpawnMessage;
import com.jme3.scene.Spatial;

public class TankSpawnTxControl extends TransmitterControl<SpawnMessage> {
	
	private static final String MODEL = "Models/Server/TankModel.j3o";
	
	private SpawnControl spawnControl = null;
	private boolean initialized = false;

	private void initialize() {
		spawnControl = spatial.getControl(SpawnControl.class);
		if (spawnControl == null) {
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
	}

	public Spatial spawn(String name) {
		if (!initialized) {
			return null;
		}
		spawnControl.setModel(MODEL);
		Spatial result = spawnControl.spawn(name);
		send(new SpawnMessage(name));
		return result;
	}
}
