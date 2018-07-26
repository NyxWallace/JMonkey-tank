package ch.supsi.gamedev.tank3d.appstates;

import ch.supsi.gamedev.tank3d.Globals;
import ch.supsi.gamedev.tank3d.controls.InputControl;
import ch.supsi.gamedev.tank3d.utils.Utils;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import java.util.HashSet;
import java.util.Set;

public class InputsManager extends AbstractAppState {

	private static final int DEFAULT_FREQUENCY = 20;
	private int frequency = DEFAULT_FREQUENCY;
	private Set<InputControl> inputControls = new HashSet<>();
	private float time = 0.0f;

	@Override
	public void initialize(AppStateManager stateManager, Application application) {
		super.initialize(stateManager, application);
		time = 0.0f;
	}

	@Override
	public void update(float tpf) {
		time += tpf;
		if (time < 1.0f / (float) frequency) {
			return;
		}
		
		Node rootNode = Globals.SINGLETON.getRootNode();
		Set<InputControl> newInputControls = Utils.descendantsControls(rootNode, InputControl.class);
		Set<InputControl> addedInputControls = new HashSet<>(newInputControls);
		addedInputControls.removeAll(inputControls);
		Set<InputControl> removedInputControls = new HashSet<>(inputControls);
		removedInputControls.removeAll(newInputControls);
		inputControls = newInputControls;

		InputManager inputManager = Globals.SINGLETON.getInputManager();
		for (InputControl addedInputControl : addedInputControls) {
			inputManager.addListener(addedInputControl, addedInputControl.actions());
		}
		for (InputControl removedInputControl : removedInputControls) {
			inputManager.removeListener(removedInputControl);
		}
	}

	@Override
	public void cleanup() {
		InputManager inputManager = Globals.SINGLETON.getInputManager();
		for (InputControl inputControl : inputControls) {
			inputManager.removeListener(inputControl);
		}
		inputControls.clear();
	}
}
