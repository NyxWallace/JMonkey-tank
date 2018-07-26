package ch.supsi.gamedev.tank3d.controls.networkcontrols;

import ch.supsi.gamedev.tank3d.messages.ControlMessage;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

public abstract class UpdateTxControl<ThisControlMessage extends ControlMessage> extends TransmitterControl<ThisControlMessage> {

	private static final int DEFAULT_FREQUENCY = 20;
	private int frequency = DEFAULT_FREQUENCY;
	private float time = 0.0f;

	protected abstract ThisControlMessage produce();

	@Override
	protected void controlUpdate(float tpf) {
		super.controlUpdate(tpf);
		time += tpf;
		if (time > 1.0f / (float) frequency) {
			ThisControlMessage message = produce();
			if (message != null) {
				send(message);
			}
			time -= 1.0f / (float) frequency;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
	}
}