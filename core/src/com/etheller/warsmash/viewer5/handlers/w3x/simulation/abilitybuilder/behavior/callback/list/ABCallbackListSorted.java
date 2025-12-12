package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackListSorted<T extends Comparable<? super T>> extends ABSortableListCallback<T> {

	private ABSortableListCallback<T> list;
	private ABListSortType sort;
	
	@Override
	public List<T> callback(final CSimulation game, final CUnit caster,
			final Map<String, Object> localStore, final int castId) {
		List<T> theList = list.callback(game, caster, localStore, castId);
		switch(sort){
		case DSC:
			theList.sort(Collections.reverseOrder());
			break;
		case RAND:
			Collections.shuffle(theList, game.getSeededRandom());
			break;
		default:
		case ASC:
			Collections.sort(theList);
			break;
		}
		return theList;
	}
}
