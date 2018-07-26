package ch.supsi.gamedev.tank3d.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

public class TankSpawnControl extends SpawnControl {

	private static enum Type {

		PLAYER("Models/Player/TankModel.j3o"), REMOTE("Models/Remote/TankModel.j3o"), SERVER("Models/Server/TankModel.j3o");
		private final String model;

		private Type(String model) {
			this.model = model;
		}

		public String model() {
			return model;
		}
	}
	private String player = null;
	private boolean server = false;
	
	private Type getType(String name) {
		if (server) {
			return Type.SERVER;
		}
		if (name.equals(player)) {
			return Type.PLAYER;
		}
		return Type.REMOTE;
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean server) {
		this.server = server;
	}
	
	@Override
	public Spatial spawn(String name) {
		Type type = getType(name);
		setModel(type.model());
		return super.spawn(name);
	}
}
