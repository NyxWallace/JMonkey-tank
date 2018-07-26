package ch.supsi.gamedev.tank3d.appstates.servergameappstate;

import com.jme3.network.HostedConnection;
import com.jme3.scene.Spatial;
import java.util.Objects;

public class Player {

	private final HostedConnection hostedConnection;
	private String name = null;
	private boolean ready = false;
	private Spatial spatial = null;

	public Player(HostedConnection hostedConnection) {
		this.hostedConnection = hostedConnection;
	}

	public final HostedConnection hostedConnection() {
		return hostedConnection;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public Spatial getSpatial() {
		return spatial;
	}

	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		final Player other = (Player) object;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return true;
	}
}
