package cc.unknown.component.impl.player.rotationcomponent;

public enum MovementFix {
    OFF("Off"),
    SILENT("Silent"),
    STRICT("Strict"),
    BACKWARDS_SPRINT("Backwards");

    final String name;

    @Override
    public String toString() {
        return name;
    }

	private MovementFix(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}