package ch.supsi.gamedev.tank3d.appstates;

import static ch.supsi.gamedev.tank3d.appstates.NetworkAppState.GAME_NAME;
import ch.supsi.gamedev.tank3d.messages.Broadcastable;
import ch.supsi.gamedev.tank3d.messages.TemporalMessage;
import com.jme3.network.Filter;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.Network;
import com.jme3.network.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerAppState extends NetworkAppState {

	private static class IsFilter implements Filter<HostedConnection> {

		private final HostedConnection hostedConnection;

		public IsFilter(HostedConnection hostedConnection) {
			this.hostedConnection = hostedConnection;
		}

		@Override
		public boolean apply(HostedConnection hostedConnection) {
			return this.hostedConnection.getId() == hostedConnection.getId();
		}
	}

	private static class IsNotFilter implements Filter<HostedConnection> {

		private final HostedConnection hostedConnection;

		public IsNotFilter(HostedConnection hostedConnection) {
			this.hostedConnection = hostedConnection;
		}

		@Override
		public boolean apply(HostedConnection hostedConnection) {
			return this.hostedConnection.getId() != hostedConnection.getId();
		}
	}
	private Server server = null;

	@Override
	protected void send(Message message) {
		server.broadcast(message);
	}

	public void sendMessageTo(Message message, HostedConnection hostedConnection) {
		if (!isOnline()) {
			return;
		}
		if (message instanceof TemporalMessage) {
			TemporalMessage temporalMessage = (TemporalMessage) message;
			temporalMessage.setTimestamp(getTimestamp());
		}
		server.broadcast(new IsFilter(hostedConnection), message);
	}

	public Server getServer() {
		return server;
	}

	@Override
	public boolean isOnline() {
		return server != null && server.isRunning();
	}

	@Override
	public void messageReceived(Object source, Message message) {
		super.messageReceived(source, message);
		if (message instanceof Broadcastable && source instanceof HostedConnection) {
			HostedConnection hostedConnection = (HostedConnection) source;
			server.broadcast(new IsNotFilter(hostedConnection), message);
		}
	}

	public boolean host() {
		try {
			server = Network.createServer(GAME_NAME, VERSION, TCP_PORT, UDP_PORT);
			server.addMessageListener(this);
			server.start();
		} catch (IOException exception) {
			Logger.getLogger(ServerAppState.class.getName()).log(Level.SEVERE, null, exception);
			return false;
		}
		return server.isRunning();
	}
	private boolean first = true;

	@Override
	public void cleanup() {
		super.cleanup();
		if (isOnline()) {
			for (HostedConnection hostedConnection : server.getConnections()) {
				hostedConnection.close("Closing server");
			}
			server.close();
		}
	}
}
