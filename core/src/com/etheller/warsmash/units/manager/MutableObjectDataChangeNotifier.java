package com.etheller.warsmash.units.manager;

import com.etheller.warsmash.util.SubscriberSetNotifier;
import com.etheller.warsmash.util.War3ID;

public final class MutableObjectDataChangeNotifier extends SubscriberSetNotifier<MutableObjectDataChangeListener>
		implements MutableObjectDataChangeListener {

	@Override
	public void textChanged(final War3ID changedObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.textChanged(changedObject);
		}
	}

	@Override
	public void categoriesChanged(final War3ID changedObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.categoriesChanged(changedObject);
		}
	}

	@Override
	public void iconsChanged(final War3ID changedObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.iconsChanged(changedObject);
		}
	}

	@Override
	public void fieldsChanged(final War3ID changedObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.fieldsChanged(changedObject);
		}
	}

	@Override
	public void modelChanged(final War3ID changedObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.modelChanged(changedObject);
		}
	}

	@Override
	public void objectCreated(final War3ID newObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.objectCreated(newObject);
		}
	}

	@Override
	public void objectsCreated(final War3ID[] newObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.objectsCreated(newObject);
		}
	}

	@Override
	public void objectRemoved(final War3ID newObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.objectRemoved(newObject);
		}
	}

	@Override
	public void objectsRemoved(final War3ID[] newObject) {
		for (final MutableObjectDataChangeListener listener : this.set) {
			listener.objectsRemoved(newObject);
		}
	}

}
