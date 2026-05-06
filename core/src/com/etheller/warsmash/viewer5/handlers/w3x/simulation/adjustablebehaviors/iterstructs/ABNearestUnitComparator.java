package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.iterstructs;

import java.util.Comparator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABNearestUnitComparator implements Comparator<CUnit> {
	public static ABNearestUnitComparator INSTANCE = new ABNearestUnitComparator();

	@Override
	public int compare(CUnit o1, CUnit o2) {
		return (int) (o1.distance(o2) * 10);
	}

}
