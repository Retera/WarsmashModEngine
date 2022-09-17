package com.etheller.warsmash.networking;

public interface ServerToClientListener {
	void acceptJoin(int playerIndex);

	void issueTargetOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, int targetHandleId,
			boolean queue);

	void issuePointOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, float x, float y,
			boolean queue);

	void issueDropItemAtPointOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, float x, float y, final boolean queue);

	void issueDropItemAtTargetOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, int targetHeroHandleId, final boolean queue);

	void issueImmediateOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, boolean queue);

	void unitCancelTrainingItem(int playerIndex, int unitHandleId, int cancelIndex);

	void startGame();

	void finishedTurn(int gameTurnTick);

	void heartbeat();
}
