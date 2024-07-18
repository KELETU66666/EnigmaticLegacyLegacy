package keletu.enigmaticlegacy.api;

public enum ExtendedBaubleType {
	SCROLL(0),
	BOOK(1),
	CURSE(2);

	int[] validSlots;

	private ExtendedBaubleType(int ... validSlots) {
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
