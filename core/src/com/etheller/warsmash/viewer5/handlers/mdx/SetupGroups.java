package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.EmitterObject;

public class SetupGroups {
	public static int getPrio(final Batch object) {
		return object.layer.priorityPlane;
	}

	public static int getPrio(final ParticleEmitter2Object object) {
		return object.priorityPlane;
	}

	public static int getPrio(final RibbonEmitterObject object) {
		return object.layer.priorityPlane;
	}

	public static int getPrio(final Object object) {
		if (object instanceof Batch) {
			return getPrio((Batch) object);
		}
		else if (object instanceof RibbonEmitterObject) {
			return getPrio((RibbonEmitterObject) object);
		}
		else if (object instanceof ParticleEmitter2Object) {
			return getPrio((ParticleEmitter2Object) object);
		}
		else {
			throw new IllegalArgumentException(object.getClass().getName());
		}
	}

	public static int getBackupPrio(final Batch object) {
		return object.layer.filterMode;
	}

	public static int getBackupPrio(final ParticleEmitter2Object object) {
		return object.filterModeForSort;
	}

	public static int getBackupPrio(final RibbonEmitterObject object) {
		return object.layer.filterMode;
	}

	public static int getBackupPrio(final Object object) {
		if (object instanceof Batch) {
			return getBackupPrio((Batch) object);
		}
		else if (object instanceof RibbonEmitterObject) {
			return getBackupPrio((RibbonEmitterObject) object);
		}
		else if (object instanceof ParticleEmitter2Object) {
			return getBackupPrio((ParticleEmitter2Object) object);
		}
		else {
			throw new IllegalArgumentException(object.getClass().getName());
		}
	}

	public static boolean matchingGroup(final Object group, final Object object) {
		if (group instanceof BatchGroup) {
			return (object instanceof Batch) && (((Batch) object).skinningType == ((BatchGroup) group).skinningType)
					&& (((Batch) object).hd == ((BatchGroup) group).hd);
//		} else if(group instanceof ReforgedBatch) { TODO
//		    return (object instanceof ReforgedBatch) && (object.material.shader === group.shader);
		}
		else {
			// All of the emitter objects are generic objects.
			return (object instanceof GenericObject);
		}
	}

	public static GenericGroup createMatchingGroup(final MdxModel model, final Object object) {
		if (object instanceof Batch) {
			return new BatchGroup(model, ((Batch) object).skinningType, ((Batch) object).hd);
//		} else if(object instanceof ReforgedBatch) { TODO
//			return new ReforgedBatchGroup(model, ((ReforgedBatch)object).material.shader);
		}
		else {
			return new EmitterGroup(model);
		}
	}

	public static void setupGroups(final MdxModel model) {
		final List<Batch> opaqueBatches = new ArrayList<>();
		final List<Batch> translucentBatches = new ArrayList<>();

		for (final Batch batch : model.batches) {// TODO reforged
			if (/* batch instanceof ReforgedBatch || */batch.layer.filterMode < 2) {
				opaqueBatches.add(batch);
			}
			else {
				translucentBatches.add(batch);
			}
		}

		final List<GenericGroup> opaqueGroups = model.opaqueGroups;
		final List<GenericGroup> translucentGroups = model.translucentGroups;
		GenericGroup currentGroup = null;

		for (final Batch object : opaqueBatches) {
			if ((currentGroup == null) || !matchingGroup(currentGroup, object)) {
				currentGroup = createMatchingGroup(model, object);

				opaqueGroups.add(currentGroup);
			}

			final int index = object.index;
			currentGroup.objects.add(index);
		}

		// Sort between all of the translucent batches and emitters that have priority
		// planes
		final List<Object> sorted = new ArrayList<>();
		sorted.addAll(translucentBatches);
		sorted.addAll(model.particleEmitters2);
		sorted.addAll(model.ribbonEmitters);
		Collections.sort(sorted, new Comparator<Object>() {
			@Override
			public int compare(final Object o1, final Object o2) {
				final int priorityDifference = getPrio(o1) - getPrio(o2);
				if (priorityDifference == 0) {
					return getBackupPrio(o1) - getBackupPrio(o2);
				}
				return priorityDifference;
			}
		});

		// Event objects have no priority planes, so they might as well always be last.
		final List<Object> objects = new ArrayList<>();
		objects.addAll(sorted);
		objects.addAll(model.eventObjects);

		currentGroup = null;

		for (final Object object : objects) { // TODO reforged
			if ((object instanceof Batch /* || object instanceof ReforgedBatch */)
					|| (object instanceof EmitterObject)) {
				if ((currentGroup == null) || !matchingGroup(currentGroup, object)) {
					currentGroup = createMatchingGroup(model, object);

					translucentGroups.add(currentGroup);
				}

				final int index = ((GenericIndexed) object).getIndex();
				currentGroup.objects.add(index);
			}
		}
	}
}
