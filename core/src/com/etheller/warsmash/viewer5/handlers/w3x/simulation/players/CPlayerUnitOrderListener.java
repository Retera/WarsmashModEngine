package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

public interface CPlayerUnitOrderListener {
	void issueTargetOrder(int unitHandleId, int abilityHandleId, int orderId, int targetHandleId, boolean queue);

	void issuePointOrder(int unitHandleId, int abilityHandleId, int orderId, float x, float y, boolean queue);

	void issueDropItemAtPointOrder(int unitHandleId, int abilityHandleId, int orderId, int targetHandleId, float x,
			float y, final boolean queue);

	void issueDropItemAtTargetOrder(int unitHandleId, int abilityHandleId, int orderId, int targetItemHandleId,
			int targetHeroHandleId, final boolean queue);

	void issueImmediateOrder(int unitHandleId, int abilityHandleId, int orderId, boolean queue);

	void unitCancelTrainingItem(int unitHandleId, int cancelIndex);
}
