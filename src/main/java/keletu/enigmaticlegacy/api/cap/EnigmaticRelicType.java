package keletu.enigmaticlegacy.api.cap;

public enum EnigmaticRelicType {
	SCROLL(0),
	BOOK(1),
	CURSE(2),
	SPELLSTONE(3);

	int[] validSlots;

	private EnigmaticRelicType(int ... validSlots) {
		this.validSlots = validSlots;
	}

	public boolean hasSlot(int slot) {
		for (int s:validSlots) {
			if (s == slot) return true;
		}
		return false; 
	}

	public int[] getValidSlots() {
		return validSlots;
	}
}