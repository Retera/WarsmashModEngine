package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.List;

public class SetupSimpleGroups {
	private static final float[] alphaHeap = new float[1];

	public static boolean isBatchSimple(final Batch batch) {
		final GeosetAnimation geosetAnimation = batch.geoset.geosetAnimation;

		if (geosetAnimation != null) {
			geosetAnimation.getAlpha(alphaHeap, 0, 0, 0);

			if (alphaHeap[0] <= 0.01) {
				return false;
			}
		}

		Layer layer;

		if (batch instanceof Batch) {
			layer = batch.layer;
		}
		else {
			throw new IllegalStateException("reforged?"); // TODO
//			layer = batch.material.layers[0];
		}

		layer.getAlpha(alphaHeap, 0, 0, 0);

		if (alphaHeap[0] < 0.01) {
			return false;
		}

		return true;
	}

	public static void setupSimpleGroups(final MdxModel model) {
		final List<Batch> batches = model.batches;
		final List<GenericGroup> simpleGroups = model.simpleGroups;

		for (final GenericGroup group : model.opaqueGroups) {
			GenericGroup simpleGroup;

			if (group instanceof BatchGroup) {
				simpleGroup = new BatchGroup(model, ((BatchGroup) group).skinningType, ((BatchGroup) group).hd);
			}
			else {
				throw new IllegalStateException("reforged?"); // TODO
				// simpleGroup = new ReforgedBatchGroup(model, group.shader);
			}

			for (final Integer object : group.objects) {
				if (isBatchSimple(batches.get(object))) {
					simpleGroup.objects.add(object);
				}
			}

			simpleGroups.add(simpleGroup);
		}
	}
}
