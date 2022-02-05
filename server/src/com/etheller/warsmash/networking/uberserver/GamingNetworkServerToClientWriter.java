package com.etheller.warsmash.networking.uberserver;

import net.warsmash.networking.util.AbstractWriter;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.uberserver.AccountCreationFailureReason;
import net.warsmash.uberserver.GamingNetwork;
import net.warsmash.uberserver.GamingNetworkServerToClientListener;
import net.warsmash.uberserver.HandshakeDeniedReason;
import net.warsmash.uberserver.LoginFailureReason;

public class GamingNetworkServerToClientWriter extends AbstractWriter implements GamingNetworkServerToClientListener {

	public GamingNetworkServerToClientWriter(final WritableOutput writableOutput) {
		super(writableOutput);
	}

	@Override
	public void handshakeAccepted() {
		beginMessage(Protocol.HANDSHAKE_ACCEPTED, 0);
		send();
	}

	@Override
	public void handshakeDenied(final HandshakeDeniedReason reason) {
		beginMessage(Protocol.HANDSHAKE_DENIED, 4);
		this.writeBuffer.putInt(reason.ordinal());
		send();

	}

	@Override
	public void accountCreationOk() {
		beginMessage(Protocol.ACCOUNT_CREATION_OK, 0);
		send();
	}

	@Override
	public void accountCreationFailed(final AccountCreationFailureReason reason) {
		beginMessage(Protocol.ACCOUNT_CREATION_FAILED, 4);
		this.writeBuffer.putInt(reason.ordinal());
		send();
	}

	@Override
	public void loginOk(final long sessionToken, String welcomeMessage) {
		if (welcomeMessage.length() > GamingNetwork.MESSAGE_MAX_LENGTH) {
			welcomeMessage = welcomeMessage.substring(0, GamingNetwork.MESSAGE_MAX_LENGTH);
		}
		final byte[] bytes = welcomeMessage.getBytes();
		beginMessage(Protocol.LOGIN_OK, 8 + 4 + bytes.length);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(bytes.length);
		this.writeBuffer.put(bytes);
		send();
	}

	@Override
	public void loginFailed(final LoginFailureReason loginFailureReason) {
		beginMessage(Protocol.LOGIN_FAILED, 4);
		this.writeBuffer.putInt(loginFailureReason.ordinal());
		send();
	}

	@Override
	public void joinedChannel(String channelName) {
		if (channelName.length() > GamingNetwork.CHANNEL_NAME_MAX_LENGTH) {
			channelName = channelName.substring(0, GamingNetwork.CHANNEL_NAME_MAX_LENGTH);
		}
		final byte[] bytes = channelName.getBytes();
		beginMessage(Protocol.JOINED_CHANNEL, 4 + bytes.length);
		this.writeBuffer.putInt(bytes.length);
		this.writeBuffer.put(bytes);
		send();
	}

	@Override
	public void badSession() {
		beginMessage(Protocol.BAD_SESSION, 0);
		send();
	}

	@Override
	public void channelMessage(String userName, String message) {
		if (userName.length() > GamingNetwork.USERNAME_MAX_LENGTH) {
			userName = userName.substring(0, GamingNetwork.USERNAME_MAX_LENGTH);
		}
		if (message.length() > GamingNetwork.MESSAGE_MAX_LENGTH) {
			message = message.substring(0, GamingNetwork.MESSAGE_MAX_LENGTH);
		}
		final byte[] userNameBytes = userName.getBytes();
		final byte[] messageBytes = message.getBytes();
		beginMessage(Protocol.CHANNEL_MESSAGE, 4 + userNameBytes.length + 4 + messageBytes.length);
		this.writeBuffer.putInt(userNameBytes.length);
		this.writeBuffer.put(userNameBytes);
		this.writeBuffer.putInt(messageBytes.length);
		this.writeBuffer.put(messageBytes);
		send();
	}

	@Override
	public void channelEmote(String userName, String message) {
		if (userName.length() > GamingNetwork.USERNAME_MAX_LENGTH) {
			userName = userName.substring(0, GamingNetwork.USERNAME_MAX_LENGTH);
		}
		if (message.length() > GamingNetwork.MESSAGE_MAX_LENGTH) {
			message = message.substring(0, GamingNetwork.MESSAGE_MAX_LENGTH);
		}
		final byte[] userNameBytes = userName.getBytes();
		final byte[] messageBytes = message.getBytes();
		beginMessage(Protocol.CHANNEL_EMOTE, 4 + userNameBytes.length + 4 + messageBytes.length);
		this.writeBuffer.putInt(userNameBytes.length);
		this.writeBuffer.put(userNameBytes);
		this.writeBuffer.putInt(messageBytes.length);
		this.writeBuffer.put(messageBytes);
		send();
	}
}
