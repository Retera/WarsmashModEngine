package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.list;

import java.util.Collections;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackListSorted<T extends Comparable<? super T>> extends ABSortableListCallback<T> {

	private ABSortableListCallback<T> list;
	private ABListSortType sort;
	
	@Override
	public List<T> callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		List<T> theList = list.callback(caster, localStore, castId);
		switch(sort){
		case DSC:
			theList.sort(Collections.reverseOrder());
			break;
		case RAND:
			Collections.shuffle(theList, localStore.game.getSeededRandom());
			break;
		default:
		case ASC:
			Collections.sort(theList);
			break;
		}
		return theList;
	}
}
