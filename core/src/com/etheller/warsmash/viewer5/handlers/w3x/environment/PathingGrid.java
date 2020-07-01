package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;

public class PathingGrid {
	private static final Map<String, MovementType> movetpToMovementType = new HashMap<>();
	static {
		for (final MovementType movementType : MovementType.values()) {
			if (movementType != MovementType.DISABLED) {
				movetpToMovementType.put(movementType.typeKey, movementType);
			}
		}
	}

	private final short[] pathingGrid;
	private final short[] dynamicPathingOverlay; // for buildings and trees
	private final int[] pathingGridSizes;
	private final float[] centerOffset;

	public PathingGrid(final War3MapWpm terrainPathing, final float[] centerOffset) {
		this.centerOffset = centerOffset;
		this.pathingGrid = terrainPathing.getPathing();
		this.pathingGridSizes = terrainPathing.getSize();
		this.dynamicPathingOverlay = new short[this.pathingGrid.length];
	}

	// this blit function is basically copied from HiveWE, maybe remember to mention
	// that in credits as well:
	// https://github.com/stijnherfst/HiveWE/blob/master/Base/PathingMap.cpp
	public void blitPathingOverlayTexture(final float positionX, final float positionY, final int rotation,
			final BufferedImage pathingTextureTga) {
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
				if ((rgb & 0xFF) > 250) {
					data |= PathingFlags.UNBUILDABLE;
				}
				if (((rgb & 0xFF00) >> 8) > 250) {
					data |= PathingFlags.UNFLYABLE;
				}
				if (((rgb & 0xFF0000) >> 16) > 250) {
					data |= PathingFlags.UNWALKABLE;
				}
				this.dynamicPathingOverlay[(yy * this.pathingGridSizes[0]) + xx] |= data;
			}
		}
	}

	public int getWidth() {
		return this.pathingGridSizes[0];
	}

	public int getHeight() {
		return this.pathingGridSizes[1];
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

	public short getCellPathing(final int cellX, final int cellY) {
		return (short) (this.pathingGrid[(cellY * this.pathingGridSizes[0]) + cellX]
				| this.dynamicPathingOverlay[(cellY * this.pathingGridSizes[0]) + cellX]);
	}

	public boolean isPathable(final float x, final float y, final PathingType pathingType) {
		return !PathingFlags.isPathingFlag(getPathing(x, y), pathingType.preventionFlag);
	}

	public boolean isPathable(final float x, final float y, final MovementType pathingType) {
		return pathingType.isPathable(getPathing(x, y));
	}

	public boolean isPathable(final float x, final float y, final MovementType pathingType, final float collisionSize) {
		if (collisionSize == 0f) {
			return pathingType.isPathable(getPathing(x, y));
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (!pathingType.isPathable(getPathing(x + (i * collisionSize), y + (j * collisionSize)))) {
					return false;
				}
			}
		}
		return true;
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
}
