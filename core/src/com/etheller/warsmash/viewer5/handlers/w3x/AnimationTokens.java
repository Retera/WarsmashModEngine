package com.etheller.warsmash.viewer5.handlers.w3x;

import com.etheller.interpreter.ast.util.CHandle;

public class AnimationTokens {
	public static enum PrimaryTag implements CHandle {
		ATTACK,
		BIRTH,
//		CINEMATIC,
		DEATH,
		DECAY,
		DISSIPATE,
		MORPH,
		PORTRAIT,
		SLEEP,
//		SPELL,
		STAND,
		WALK;

		@Override
		public int getHandleId() {
			return ordinal();
		}

		public static final PrimaryTag[] VALUES = values();
	}

	public static enum SecondaryTag implements CHandle {
		ALTERNATE,
		ALTERNATEEX,
		BONE,
		CHAIN,
		CHANNEL,
		COMPLETE,
		CRITICAL,
		DEFEND,
		DRAIN,
		EATTREE,
		FAST,
		FILL,
		FLAIL,
		FLESH,
		FIFTH,
		FIRE,
		FIRST,
		FIVE,
		FOUR,
		FOURTH,
		GOLD,
		HIT,
		LARGE,
		LEFT,
		LIGHT,
		LOOPING,
		LUMBER,
		MEDIUM,
		MODERATE,
		OFF,
		ONE,
		PUKE,
		READY,
		RIGHT,
		SECOND,
		SEVERE,
		SLAM,
		SMALL,
		SPIKED,
		SPIN,
		SPELL,
		CINEMATIC,
		SWIM,
		TALK,
		THIRD,
		THREE,
		THROW,
		TWO,
		TURN,
		VICTORY,
		WORK,
		WOUNDED,
		UPGRADE;

		public static SecondaryTag fromCount(final int count) {
			switch (count) {
			case 1:
				return SecondaryTag.FIRST;
			case 2:
				return SecondaryTag.SECOND;
			case 3:
				return SecondaryTag.THIRD;
			case 4:
				return SecondaryTag.FOURTH;
			case 5:
				return SecondaryTag.FIFTH;
			}
			return null;
		}

		@Override
		public int getHandleId() {
			return ordinal();
		}

		public static final SecondaryTag[] VALUES = values();
	}
}
