package net.warsmash.uberserver;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import net.warsmash.nio.channels.tcp.TCPClientParser;

public class TCPGamingNetworkServerToClientParser implements TCPClientParser {
	private final GamingNetworkServerToClientListener listener;

	public TCPGamingNetworkServerToClientParser(final GamingNetworkServerToClientListener listener) {
		this.listener = listener;
	}

	@Override
	public void parse(final ByteBuffer data) {
		while (data.remaining() >= 8) {
			final int protocolMessageId = data.getInt(data.position() + 0);
			final int length = data.getInt(data.position() + 4);
			if (data.remaining() >= length) {
				data.position(data.position() + 8);
				switch (protocolMessageId) {
				case GamingNetworkServerToClientListener.Protocol.HANDSHAKE_ACCEPTED: {
					this.listener.handshakeAccepted();
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.HANDSHAKE_DENIED: {
					final int reasonOrdinal = data.getInt();
					HandshakeDeniedReason reason;
					if ((reasonOrdinal >= 0) && (reasonOrdinal < HandshakeDeniedReason.VALUES.length)) {
						reason = HandshakeDeniedReason.VALUES[reasonOrdinal];
					}
					else {
						reason = null;
					}
					this.listener.handshakeDenied(reason);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.ACCOUNT_CREATION_OK: {
					this.listener.accountCreationOk();
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.ACCOUNT_CREATION_FAILED: {
					final int reasonOrdinal = data.getInt();
					AccountCreationFailureReason reason;
					if ((reasonOrdinal >= 0) && (reasonOrdinal < AccountCreationFailureReason.VALUES.length)) {
						reason = AccountCreationFailureReason.VALUES[reasonOrdinal];
					}
					else {
						reason = null;
					}
					this.listener.accountCreationFailed(reason);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.LOGIN_OK: {
					final long sessionToken = data.getLong();
					final String welcomeMessage = readString(GamingNetwork.MESSAGE_MAX_LENGTH, data);
					this.listener.loginOk(sessionToken, welcomeMessage);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.LOGIN_FAILED: {
					final int reasonOrdinal = data.getInt();
					LoginFailureReason reason;
					if ((reasonOrdinal >= 0) && (reasonOrdinal < LoginFailureReason.VALUES.length)) {
						reason = LoginFailureReason.VALUES[reasonOrdinal];
					}
					else {
						reason = null;
					}
					this.listener.loginFailed(reason);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.JOINED_CHANNEL: {
					final String channelName = readString(GamingNetwork.CHANNEL_NAME_MAX_LENGTH, data);
					this.listener.joinedChannel(channelName);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.BAD_SESSION: {
					this.listener.badSession();
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.CHANNEL_MESSAGE: {
					final String username = readString(GamingNetwork.USERNAME_MAX_LENGTH, data);
					final String message = readString(GamingNetwork.MESSAGE_MAX_LENGTH, data);
					this.listener.channelMessage(username, message);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.CHANNEL_EMOTE: {
					final String username = readString(GamingNetwork.USERNAME_MAX_LENGTH, data);
					final String message = readString(GamingNetwork.MESSAGE_MAX_LENGTH, data);
					this.listener.channelEmote(username, message);
					break;
				}
				}
			}
			else {
				break;
			}
		}
	}

	public String readString(final int maxLength, final ByteBuffer data) {
		final int usernameStringLength = Math.min(maxLength, data.getInt());
		final byte[] usernameStringBytes = new byte[usernameStringLength];
		data.get(usernameStringBytes);
		final String username = new String(usernameStringBytes, Charset.forName("utf-8"));
		return username;
	}

	public char[] readChars(final int maxLength, final ByteBuffer data) {
		final int usernameStringLength = Math.min(maxLength, data.getInt());
		final char[] charArray = new char[usernameStringLength];
		for (int i = 0; i < usernameStringLength; i++) {
			charArray[i] = data.getChar();
		}
		return charArray;
	}

	@Override
	public void disconnected() {
		this.listener.disconnected();
	}

}
