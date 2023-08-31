package com.etheller.warsmash.networking;

import java.net.SocketAddress;

public class MockServerWriter implements SendableServerToClientListener {
	private State state = new State();
	private State sentState = new State();
	public State getSentState() {
		return sentState;
	}

	@Override
	public void acceptJoin(int playerIndex) {
		state.acceptJoinCount++;
	}

	@Override
	public void issueTargetOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, boolean queue) {
		state.issueTargetOrderCount++;
	}

	@Override
	public void issuePointOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, float x, float y,
			boolean queue) {
		state.issuePointOrderCount++;
	}

	@Override
	public void issueDropItemAtPointOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, float x, float y, boolean queue) {
		state.issueDropItemAtPointOrderCount++;
	}

	@Override
	public void issueDropItemAtTargetOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId,
			int targetHandleId, int targetHeroHandleId, boolean queue) {
		state.issueDropItemAtTargetOrderCount++;
	}

	@Override
	public void issueImmediateOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId,
			boolean queue) {
		state.issueImmediateOrderCount++;
	}

	@Override
	public void unitCancelTrainingItem(int playerIndex, int unitHandleId, int cancelIndex) {
		state.unitCancelTrainingItemCount++;
	}

	@Override
	public void startGame() {
		state.startGameCount++;
	}

	@Override
	public void finishedTurn(int gameTurnTick) {
		state.finishedTurnCount++;
		state.lastFinishTurnTick = gameTurnTick;
	}

	@Override
	public void heartbeat() {
		state.heartbeatCount++;
	}

	@Override
	public void send() {
		sentState.set(state);
	}

	@Override
	public void send(SocketAddress address) {
		sentState.set(state);
	}

	public static final class State {
		public int acceptJoinCount = 0;
		public int issueTargetOrderCount = 0;
		public int issuePointOrderCount = 0;
		public int issueDropItemAtPointOrderCount = 0;
		public int issueDropItemAtTargetOrderCount = 0;
		public int issueImmediateOrderCount = 0;
		public int unitCancelTrainingItemCount = 0;
		public int startGameCount = 0;
		public int finishedTurnCount = 0;
		public int lastFinishTurnTick = 0;
		public int heartbeatCount = 0;
		
		private void set(State other) {
			acceptJoinCount = other.acceptJoinCount;
			issueTargetOrderCount = other.issueTargetOrderCount;
			issuePointOrderCount = other.issuePointOrderCount;
			issueDropItemAtPointOrderCount = other.issueDropItemAtPointOrderCount;
			issueDropItemAtTargetOrderCount = other.issueDropItemAtTargetOrderCount;
			unitCancelTrainingItemCount = other.unitCancelTrainingItemCount;
			startGameCount = other.startGameCount;
			finishedTurnCount = other.finishedTurnCount;
			lastFinishTurnTick = other.lastFinishTurnTick;
			heartbeatCount = other.heartbeatCount;
		}
	}
}
