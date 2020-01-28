package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public interface CPlayerController {
	boolean issueTargetOrder(int unitHandleId, int orderId, int targetHandleId);

	boolean issuePointOrder(int unitHandleId, int orderId, float x, float y);

	boolean issueImmediateOrder(int unitHandleId, int orderId);
}
