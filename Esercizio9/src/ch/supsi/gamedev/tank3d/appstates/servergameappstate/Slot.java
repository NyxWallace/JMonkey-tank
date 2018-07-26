package ch.supsi.gamedev.tank3d.appstates.servergameappstate;

import ch.supsi.gamedev.tank3d.controls.multiplayercontrols.TankSpawnTxControl;
import com.jme3.scene.Spatial;

public class Slot {
	
	private final String playerName;
	private final TankSpawnTxControl tankSpawnTxControl;
	private Player player = null;

	public Slot(String playerName, TankSpawnTxControl tankSpawnTxControl) {
		this.playerName = playerName;
		this.tankSpawnTxControl = tankSpawnTxControl;
	}
	
	public boolean isFree() {
		return player == null;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		player.setName(playerName);
		this.player = player;
	}
	
	public void spawn() {
		Spatial spatial = tankSpawnTxControl.spawn(playerName);
		player.setSpatial(spatial);
	}
}
