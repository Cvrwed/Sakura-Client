package cc.unknown.font;

public enum Weight {
    NONE(0, ""),
    LIGHT(1, "Light", "light", "LIGHT"),
    BOLD(4, "Bold", "bold", "BOLD");

    final private int num;
    final private String[] aliases;

    Weight(int num, String... aliases) {
        this.num = num;
        this.aliases = aliases;
    }

	public int getNum() {
		return num;
	}

	public String[] getAliases() {
		return aliases;
	}
}
