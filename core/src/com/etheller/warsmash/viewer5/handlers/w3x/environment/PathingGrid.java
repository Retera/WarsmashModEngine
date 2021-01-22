package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;

public class PathingGrid {
	private static final Map<String, MovementType> movetpToMovementType = new HashMap<>();
	static {
		for (final MovementType movementType : MovementType.values()) {
			if (!movementType.typeKey.isEmpty()) {
				movetpToMovementType.put(movementType.typeKey, movementType);
			}
		}
	}

	private final short[] pathingGrid;
	private final short[] dynamicPathingOverlay; // for buildings and trees
	private final int[] pathingGridSizes;
	private final float[] centerOffset;
	private final List<RemovablePathingMapInstance> dynamicPathingInstances;

	public PathingGrid(final War3MapWpm terrainPathing, final float[] centerOffset) {
		this.centerOffset = centerOffset;
		this.pathingGrid = terrainPathing.getPathing();
		this.pathingGridSizes = terrainPathing.getSize();
		this.dynamicPathingOverlay = new short[this.pathingGrid.length];
		this.dynamicPathingInstances = new ArrayList<>();
	}

	// this blit function is basically copied from HiveWE, maybe remember to mention
	// that in credits as well:
	// https://github.com/stijnherfst/HiveWE/blob/master/Base/PathingMap.cpp
	private void blitPathingOverlayTexture(final float positionX, final float positionY, final int rotationInput,
			final BufferedImage pathingTextureTga) {
		final int rotation = (rotationInput + 450) % 360;
		final int divW = ((rotation % 180) != 0) ? pathingTextureTga.getHeight() : pathingTextureTga.getWidth();
		final int divH = ((rotation % 180) != 0) ? pathingTextureTga.getWidth() : pathingTextureTga.getHeight();
		for (int j = 0; j < pathingTextureTga.getHeight(); j++) {
			for (int i = 0; i < pathingTextureTga.getWidth(); i++) {
				int x = i;
				int y = j;

				switch (rotation) {
				case 90:
					x = pathingTextureTga.getHeight() - 1 - j;
					y = i;
					break;
				case 180:
					x = pathingTextureTga.getWidth() - 1 - i;
					y = pathingTextureTga.getHeight() - 1 - j;
					break;
				case 270:
					x = j;
					y = pathingTextureTga.getWidth() - 1 - i;
					break;
				}
				// Width and height for centering change if rotation is not divisible by 180
				final int xx = (getCellX(positionX) + x) - (divW / 2);
				final int yy = (getCellY(positionY) + y) - (divH / 2);

				if ((xx < 0) || (xx > (this.pathingGridSizes[0] - 1)) || (yy < 0)
						|| (yy > (this.pathingGridSizes[1] - 1))) {
					continue;
				}

				final int rgb = pathingTextureTga.getRGB(i, pathingTextureTga.getHeight() - 1 - j);
				byte data = 0;
				if ((rgb & 0xFF) > 127) {
					data |= PathingFlags.UNBUILDABLE;
				}
				if (((rgb & 0xFF00) >>> 8) > 127) {
					data |= PathingFlags.UNFLYABLE;
				}
				if (((rgb & 0xFF0000) >>> 16) > 127) {
					data |= PathingFlags.UNWALKABLE;
				}
				this.dynamicPathingOverlay[(yy * this.pathingGridSizes[0]) + xx] |= data;
			}
		}
	}

	public boolean checkPathingTexture(final float positionX, final float positionY, final int rotationInput,
			final BufferedImage pathingTextureTga, final EnumSet<CBuildingPathingType> preventPathingTypes,
			final EnumSet<CBuildingPathingType> requirePathingTypes, final CWorldCollision cWorldCollision,
			final CUnit unitToExcludeFromCollisionChecks) {
		final int rotation = (rotationInput + 450) % 360;
		final int divW = ((rotation % 180) != 0) ? pathingTextureTga.getHeight() : pathingTextureTga.getWidth();
		final int divH = ((rotation % 180) != 0) ? pathingTextureTga.getWidth() : pathingTextureTga.getHeight();
		short anyPathingTypesInRegion = 0;
		short pathingTypesFillingRegion = (short) 0xFFFF;
		for (int j = 0; j < pathingTextureTga.getHeight(); j++) {
			for (int i = 0; i < pathingTextureTga.getWidth(); i++) {
				int x = i;
				int y = j;

				switch (rotation) {
				case 90:
					x = pathingTextureTga.getHeight() - 1 - j;
					y = i;
					break;
				case 180:
					x = pathingTextureTga.getWidth() - 1 - i;
					y = pathingTextureTga.getHeight() - 1 - j;
					break;
				case 270:
					x = j;
					y = pathingTextureTga.getWidth() - 1 - i;
					break;
				}
				// Width and height for centering change if rotation is not divisible by 180
				final int xx = (getCellX(positionX) + x) - (divW / 2);
				final int yy = (getCellY(positionY) + y) - (divH / 2);

				if ((xx < 0) || (xx > (this.pathingGridSizes[0] - 1)) || (yy < 0)
						|| (yy > (this.pathingGridSizes[1] - 1))) {
					continue;
				}

				final short cellPathing = getCellPathing(xx, yy);
				anyPathingTypesInRegion |= cellPathing;
				pathingTypesFillingRegion &= cellPathing;
			}
		}
		final float width = pathingTextureTga.getWidth() * 32f;
		final float height = pathingTextureTga.getHeight() * 32f;
		final float offsetX = ((pathingTextureTga.getWidth() % 2) == 1) ? 16f : 0f;
		final float offsetY = ((pathingTextureTga.getHeight() % 2) == 1) ? 16f : 0f;
		final Rectangle pathingMapRectangle = new Rectangle((positionX - (width / 2)) + offsetX,
				(positionY - (height / 2)) + offsetY, width, height);
		short anyPathingTypeWithUnit = 0;
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.AMPHIBIOUS)) {
			System.out.println("intersects amph unit");
			anyPathingTypesInRegion |= PathingFlags.UNBUILDABLE | PathingFlags.UNWALKABLE | PathingFlags.UNSWIMABLE;
			anyPathingTypeWithUnit |= PathingFlags.UNBUILDABLE | PathingFlags.UNWALKABLE | PathingFlags.UNSWIMABLE;
		}
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.FLOAT)) {
			System.out.println("intersects float unit");
			anyPathingTypesInRegion |= PathingFlags.UNSWIMABLE;
			anyPathingTypeWithUnit |= PathingFlags.UNSWIMABLE;
		}
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.FLY)) {
			System.out.println("intersects fly unit");
			anyPathingTypesInRegion |= PathingFlags.UNFLYABLE;
			anyPathingTypeWithUnit |= PathingFlags.UNFLYABLE;
		}
		if (cWorldCollision.intersectsAnythingOtherThan(pathingMapRectangle, unitToExcludeFromCollisionChecks,
				MovementType.FOOT)) {
			System.out.println("intersects foot unit");
			anyPathingTypesInRegion |= PathingFlags.UNBUILDABLE | PathingFlags.UNWALKABLE;
			anyPathingTypeWithUnit |= PathingFlags.UNBUILDABLE | PathingFlags.UNWALKABLE;
		}
		pathingTypesFillingRegion &= anyPathingTypeWithUnit;
		for (final CBuildingPathingType pathingType : preventPathingTypes) {
			if (PathingFlags.isPathingFlag(anyPathingTypesInRegion, pathingType)) {
				return false;
			}
		}
		for (final CBuildingPathingType pathingType : requirePathingTypes) {
			if (!PathingFlags.isPathingFlag(pathingTypesFillingRegion, pathingType)) {
				return false;
			}
		}
		return true;
	}

	public RemovablePathingMapInstance blitRemovablePathingOverlayTexture(final float positionX, final float positionY,
			final int rotationInput, final BufferedImage pathingTextureTga) {
		final RemovablePathingMapInstance removablePathingMapInstance = new RemovablePathingMapInstance(positionX,
				positionY, rotationInput, pathingTextureTga);
		removablePathingMapInstance.blit();
		this.dynamicPathingInstances.add(removablePathingMapInstance);
		return removablePathingMapInstance;
	}

	public RemovablePathingMapInstance createRemovablePathingOverlayTexture(final float positionX,
			final float positionY, final int rotationInput, final BufferedImage pathingTextureTga) {
		return new RemovablePathingMapInstance(positionX,
				positionY, rotationInput, pathingTextureTga);
	}

	public int getWidth() {
		return this.pathingGridSizes[0];
	}

	public int getHeight() {
		return this.pathingGridSizes[1];
	}

	public boolean contains(final float x, final float y) {
		final int cellX = getCellX(x);
		final int cellY = getCellY(y);
		return (cellX >= 0) && (cellY >= 0) && (cellX < this.pathingGridSizes[0]) && (cellY < this.pathingGridSizes[1]);
	}

	public short getPathing(final float x, final float y) {
		return getCellPathing(getCellX(x), getCellY(y));
	}

	public int getCellX(final float x) {
		final float userCellSpaceX = (x - this.centerOffset[0]) / 32.0f;
		final int cellX = (int) userCellSpaceX;
		return cellX;
	}

	public int getCellY(final float y) {
		final float userCellSpaceY = (y - this.centerOffset[1]) / 32.0f;
		final int cellY = (int) userCellSpaceY;
		return cellY;
	}

	public float getWorldX(final int cellX) {
		return (cellX * 32f) + this.centerOffset[0] + 16f;
	}

	public float getWorldY(final int cellY) {
		return (cellY * 32f) + this.centerOffset[1] + 16f;
	}

	public float getWorldXFromCorner(final int cornerX) {
		return (cornerX * 32f) + this.centerOffset[0];
	}

	public float getWorldYFromCorner(final int cornerY) {
		return (cornerY * 32f) + this.centerOffset[1];
	}

	public int getCornerX(final float x) {
		final float userCellSpaceX = ((x + 16f) - this.centerOffset[0]) / 32.0f;
		final int cellX = (int) userCellSpaceX;
		return cellX;
	}

	public int getCornerY(final float y) {
		final float userCellSpaceY = ((y + 16f) - this.centerOffset[1]) / 32.0f;
		final int cellY = (int) userCellSpaceY;
		return cellY;
	}

	public short getCellPathing(final int cellX, final int cellY) {
		final int index = (cellY * this.pathingGridSizes[0]) + cellX;
		return (short) (this.pathingGrid[index] | this.dynamicPathingOverlay[index]);
	}

	public boolean isPathable(final float x, final float y, final PathingType pathingType) {
		return !PathingFlags.isPathingFlag(getPathing(x, y), pathingType.preventionFlag);
	}

	public boolean isPathable(final float x, final float y, final MovementType pathingType) {
		return pathingType.isPathable(getPathing(x, y));
	}

	public boolean isPathable(final float unitX, final float unitY, final MovementType pathingType,
			final float collisionSize) {
		if (collisionSize == 0f) {
			return pathingType.isPathable(getPathing(unitX, unitY));
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				final float unitPathingX = unitX + (i * collisionSize);
				final float unitPathingY = unitY + (j * collisionSize);
				if (!contains(unitPathingX, unitPathingY)
						|| !pathingType.isPathable(getPathing(unitPathingX, unitPathingY))) {
					return false;
				}
			}
		}
//		final float maxX = unitX + collisionSize;
//		final float maxY = unitY + collisionSize;
//		for (float minX = unitX - collisionSize; minX < maxX; minX += 32f) {
//			for (float minY = unitY - collisionSize; minY < maxY; minY += 32f) {
//				if (!pathingType.isPathable(getPathing(minX, minY))) {
//					return false;
//				}
//			}
//		}
		return true;
	}

	public boolean isUnitCell(final float queryX, final float queryY, final float unitX, final float unitY,
			final MovementType movementType, final float collisionSize) {
		final float maxX = unitX + collisionSize;
		final float maxY = unitY + collisionSize;
		final int cellX = getCellX(queryX);
		final int cellY = getCellY(queryY);
		for (float minX = unitX - collisionSize; minX < maxX; minX += 32f) {
			for (float minY = unitY - collisionSize; minY < maxY; minY += 32f) {
				final int yy = getCellY(minY);
				final int xx = getCellX(minX);
				if ((yy == cellY) && (xx == cellX)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isCellPathable(final int x, final int y, final MovementType pathingType, final float collisionSize) {
		return isPathable(getWorldX(x), getWorldY(y), pathingType, collisionSize);
	}

	public boolean isCellPathable(final int x, final int y, final MovementType pathingType) {
		return pathingType.isPathable(getCellPathing(x, y));
	}

	public static boolean isPathingFlag(final short pathingValue, final PathingType pathingType) {
		return !PathingFlags.isPathingFlag(pathingValue, pathingType.preventionFlag);
	}

	// movetp referring to the unit data field of the same name
	public static MovementType getMovementType(final String movetp) {
		return movetpToMovementType.get(movetp);
	}

	public static final class PathingFlags {
		public static int UNWALKABLE = 0x2;
		public static int UNFLYABLE = 0x4;
		public static int UNBUILDABLE = 0x8;
		public static int UNSWIMABLE = 0x40; // PROBABLY, didn't confirm this flag value is accurate

		public static boolean isPathingFlag(final short pathingValue, final int flag) {
			return (pathingValue & flag) != 0;
		}

		public static boolean isPathingFlag(final short pathingValue, final CBuildingPathingType pathingType) {
			switch (pathingType) {
			case BLIGHTED:
				throw new IllegalArgumentException("Blight pathing check system is Not Yet Implemented");
			case UNAMPH:
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE)
						&& PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE);
			case UNBUILDABLE:
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNBUILDABLE);
			case UNFLOAT:
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE);
			case UNFLYABLE:
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNFLYABLE);
			case UNWALKABLE:
				return PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			default:
				return false;
			}
		}

		private PathingFlags() {
		}
	}

	public static enum MovementType {
		FOOT("foot") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		FOOT_NO_COLLISION("") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		HORSE("horse") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		FLY("fly") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNFLYABLE);
			}
		},
		HOVER("hover") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE);
			}
		},
		FLOAT("float") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE);
			}
		},
		AMPHIBIOUS("amph") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNWALKABLE)
						|| !PathingFlags.isPathingFlag(pathingValue, PathingFlags.UNSWIMABLE);
			}
		},
		DISABLED("") {
			@Override
			public boolean isPathable(final short pathingValue) {
				return true;
			}
		};
		private final String typeKey;

		// TODO windwalk pathing type can walk through units but not through items

		private MovementType(final String typeKey) {
			this.typeKey = typeKey;
		}

		public abstract boolean isPathable(short pathingValue);
	}

	public static enum PathingType {
		WALKABLE(PathingFlags.UNWALKABLE),
		FLYABLE(PathingFlags.UNFLYABLE),
		BUILDABLE(PathingFlags.UNBUILDABLE),
		SWIMMABLE(PathingFlags.UNSWIMABLE);

		private final int preventionFlag;

		private PathingType(final int preventionFlag) {
			this.preventionFlag = preventionFlag;
		}
	}

	public final class RemovablePathingMapInstance {
		private final float positionX;
		private final float positionY;
		private final int rotationInput;
		private final BufferedImage pathingTextureTga;

		public RemovablePathingMapInstance(final float positionX, final float positionY, final int rotationInput,
				final BufferedImage pathingTextureTga) {
			this.positionX = positionX;
			this.positionY = positionY;
			this.rotationInput = rotationInput;
			this.pathingTextureTga = pathingTextureTga;
		}

		private void blit() {
			blitPathingOverlayTexture(this.positionX, this.positionY, this.rotationInput, this.pathingTextureTga);
		}

		public void remove() {
			PathingGrid.this.dynamicPathingInstances.remove(this);
			Arrays.fill(PathingGrid.this.dynamicPathingOverlay, (short) 0);
			for (final RemovablePathingMapInstance instance : PathingGrid.this.dynamicPathingInstances) {
				instance.blit();
			}
		}

		public void add() {
			PathingGrid.this.dynamicPathingInstances.add(this);
			blit();
		}
	}
}
