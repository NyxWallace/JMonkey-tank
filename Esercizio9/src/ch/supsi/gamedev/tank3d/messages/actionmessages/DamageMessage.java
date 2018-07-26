package ch.supsi.gamedev.tank3d.messages.actionmessages;

import ch.supsi.gamedev.tank3d.messages.ActionMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class DamageMessage extends ActionMessage {
	
	private static final String ACTION = "Damage";
	private float damage;

	public DamageMessage() {
		super(ACTION);
	}

	public DamageMessage(float damage) {
		this();
		this.damage = damage;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}
}
