package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template;

public enum DataFieldLetter {
	A(0),
	B(1),
	C(2),
	D(3),
	E(4),
	F(5),
	G(6),
	H(7),
	I(8),
	J(9);

	private int index;

	DataFieldLetter(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
