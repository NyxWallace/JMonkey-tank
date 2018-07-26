package ch.supsi.gamedev.tank3d.appstates;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.Network;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientAppState extends NetworkAppState {

	private Client client = null;
	private volatile String player = null;
	
	@Override
	protected void send(Message message) {
		client.send(message);
	}

	@Override
	public boolean isOnline() {
		return client != null && client.isConnected();
	}
	
	public boolean connect(String ip) {
		try {
			client = Network.connectToServer(GAME_NAME, VERSION, ip, TCP_PORT, UDP_PORT);
			client.addMessageListener(this);
			client.start();
		} catch (IOException exception) {
			Logger.getLogger(ClientAppState.class.getName()).log(Level.SEVERE, null, exception);
			return false;
		}
		return client.isConnected();
	}

	private boolean first = true;

	@Override
	public void cleanup() {
		super.cleanup();
		if (isOnline()) {
			client.close();
		}
	}
}
