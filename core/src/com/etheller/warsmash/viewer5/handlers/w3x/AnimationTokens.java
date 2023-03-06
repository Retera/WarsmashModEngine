package com.etheller.warsmash.viewer5.handlers.w3x;

public class AnimationTokens {
	public static enum PrimaryTag {
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
	}

	public static enum SecondaryTag {
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
		
		public static SecondaryTag fromCount(int count) {
			switch(count) {
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
	}
}
