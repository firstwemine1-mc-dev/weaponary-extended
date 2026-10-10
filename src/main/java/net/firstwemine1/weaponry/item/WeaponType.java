package net.firstwemine1.weaponry.item;

/**
 * Per-weapon handling stats, identical across material tiers. Values come from
 * {@code pixel-art/weaponry/docs/weapon-stats.md} section 3.
 *
 * <ul>
 *   <li>{@code damageOffset}: added to the vanilla sword damage of the tier (sword = 0).</li>
 *   <li>{@code attackSpeed}: attacks per second at full charge (sword = 1.6).</li>
 *   <li>{@code reach}: entity interaction range in blocks (survival default = 3.0).</li>
 *   <li>{@code durabilityMultiplier}: applied to the tier's sword durability, rounded down.</li>
 *   <li>{@code twoHanded}: recorded for the future two-handed mechanic; not enforced yet.</li>
 * </ul>
 */
public enum WeaponType {
	DAGGER("dagger", "Dagger", -2, 2.4f, 2.5, 0.75f, false),
	SABRE("sabre", "Sabre", -1, 2.0f, 3.0, 1.0f, false),
	SCIMITAR("scimitar", "Scimitar", 0, 1.6f, 3.0, 1.0f, false),
	SPEAR("spear", "Spear", -1, 1.4f, 4.0, 1.0f, false),
	PIKE("pike", "Pike", 0, 1.0f, 5.0, 1.2f, true),
	LANCE("lance", "Lance", -2, 0.8f, 4.5, 0.6f, true),
	HALBERD("halberd", "Halberd", 2, 0.9f, 4.0, 1.2f, true),
	HATCHET("hatchet", "Hatchet", 1, 1.1f, 2.75, 1.0f, false),
	WAR_HAMMER("war_hammer", "War Hammer", 3, 0.7f, 3.0, 1.5f, true),
	CLUB("club", "Club", -1, 1.2f, 3.0, 1.5f, false);

	private final String id;
	private final String displayName;
	private final int damageOffset;
	private final float attackSpeed;
	private final double reach;
	private final float durabilityMultiplier;
	private final boolean twoHanded;
	private final WeaponAbility ability;

	WeaponType(String id, String displayName, int damageOffset, float attackSpeed, double reach,
			float durabilityMultiplier, boolean twoHanded) {
		this(id, displayName, damageOffset, attackSpeed, reach, durabilityMultiplier, twoHanded, WeaponAbility.NONE);
	}

	WeaponType(String id, String displayName, int damageOffset, float attackSpeed, double reach,
			float durabilityMultiplier, boolean twoHanded, WeaponAbility ability) {
		this.id = id;
		this.displayName = displayName;
		this.damageOffset = damageOffset;
		this.attackSpeed = attackSpeed;
		this.reach = reach;
		this.durabilityMultiplier = durabilityMultiplier;
		this.twoHanded = twoHanded;
		this.ability = ability;
	}

	public String id() {
		return id;
	}

	public String displayName() {
		return displayName;
	}

	public int damageOffset() {
		return damageOffset;
	}

	public float attackSpeed() {
		return attackSpeed;
	}

	public double reach() {
		return reach;
	}

	public float durabilityMultiplier() {
		return durabilityMultiplier;
	}

	public boolean twoHanded() {
		return twoHanded;
	}

	public WeaponAbility ability() {
		return ability;
	}
}
