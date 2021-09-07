package com.etheller.warsmash.networking;

import java.net.SocketAddress;

public interface ClientToServerListener {
	void joinGame(SocketAddress sourceAddress);

	void issueTargetOrder(SocketAddress sourceAddress, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, boolean queue);

	void issuePointOrder(SocketAddress sourceAddress, int unitHandleId, int abilityHandleId, int orderId, float x,
			float y, boolean queue);

	void issueDropItemAtPointOrder(SocketAddress sourceAddress, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, float x, float y, final boolean queue);

	void issueDropItemAtTargetOrder(SocketAddress sourceAddress, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, int targetHeroHandleId, final boolean queue);

	void issueImmediateOrder(SocketAddress sourceAddress, int unitHandleId, int abilityHandleId, int orderId,
			boolean queue);

	void unitCancelTrainingItem(SocketAddress sourceAddress, int unitHandleId, int cancelIndex);

	void finishedTurn(SocketAddress sourceAddress, int gameTurnTick);

	void framesSkipped(int nFramesSkipped);

}
