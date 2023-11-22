package com.etheller.warsmash.networking;

import java.net.SocketAddress;

public interface ClientToServerListener {
	void joinGame(SocketAddress sourceAddress, long sessionToken);

	void issueTargetOrder(SocketAddress sourceAddress, long sessionToken, int unitHandleId, int abilityHandleId,
			int orderId, int targetHandleId, boolean queue);

	void issuePointOrder(SocketAddress sourceAddress, long sessionToken, int unitHandleId, int abilityHandleId,
			int orderId, float x, float y, boolean queue);

	void issueDropItemAtPointOrder(SocketAddress sourceAddress, long sessionToken, int unitHandleId,
			int abilityHandleId, int orderId, int targetHandleId, float x, float y, final boolean queue);

	void issueDropItemAtTargetOrder(SocketAddress sourceAddress, long sessionToken, int unitHandleId,
			int abilityHandleId, int orderId, int targetHandleId, int targetHeroHandleId, final boolean queue);

	void issueImmediateOrder(SocketAddress sourceAddress, long sessionToken, int unitHandleId, int abilityHandleId,
			int orderId, boolean queue);

	void unitCancelTrainingItem(SocketAddress sourceAddress, long sessionToken, int unitHandleId, int cancelIndex);

	void issueGuiPlayerEvent(SocketAddress sourceAddress, long sessionToken, int eventId);

	void finishedTurn(SocketAddress sourceAddress, long sessionToken, int gameTurnTick);

	void framesSkipped(long sessionToken, int nFramesSkipped);

}
