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
			final int position = data.position();
			final int protocolMessageId = data.getInt(data.position() + 0);
			final int length = data.getInt(data.position() + 4);
			if (length < 0) {
				throw new IllegalStateException("Negative length value received from server while parsing: " + length);
			}
			if (data.remaining() >= (length + 8)) {
				data.position(position + 8);
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
				case GamingNetworkServerToClientListener.Protocol.BEGIN_GAMES_LIST: {
					this.listener.beginGamesList();
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.GAMES_LIST_ITEM: {
					final String gameName = readString(GamingNetwork.CHANNEL_NAME_MAX_LENGTH, data);
					final int openSlots = data.getInt();
					final int totalSlots = data.getInt();
					this.listener.gamesListItem(gameName, openSlots, totalSlots);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.END_GAMES_LIST: {
					this.listener.endGamesList();
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.JOINED_GAME: {
					final String gameName = readString(GamingNetwork.CHANNEL_NAME_MAX_LENGTH, data);
					final String mapName = readString(GamingNetwork.MAP_NAME_MAX_LENGTH, data);
					final long checksum = data.getLong();
					this.listener.joinedGame(gameName, mapName, checksum);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.JOIN_GAME_FAILED: {
					final int reasonOrdinal = data.getInt();
					JoinGameFailureReason reason;
					if ((reasonOrdinal >= 0) && (reasonOrdinal < JoinGameFailureReason.VALUES.length)) {
						reason = JoinGameFailureReason.VALUES[reasonOrdinal];
					}
					else {
						reason = null;
					}
					this.listener.joinGameFailed(reason);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.GAME_CREATION_FAILED: {
					final int reasonOrdinal = data.getInt();
					GameCreationFailureReason reason;
					if ((reasonOrdinal >= 0) && (reasonOrdinal < GameCreationFailureReason.VALUES.length)) {
						reason = GameCreationFailureReason.VALUES[reasonOrdinal];
					}
					else {
						reason = null;
					}
					this.listener.gameCreationFailed(reason);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.GAME_CREATION_OK: {
					this.listener.gameCreationOk();
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.CHANNEL_SERVER_MESSAGE: {
					final String username = readString(GamingNetwork.USERNAME_MAX_LENGTH, data);
					final int messageTypeOrdinal = data.getInt();
					ChannelServerMessageType messageType;
					if ((messageTypeOrdinal >= 0) && (messageTypeOrdinal < ChannelServerMessageType.VALUES.length)) {
						messageType = ChannelServerMessageType.VALUES[messageTypeOrdinal];
					}
					else {
						messageType = null;
					}

					this.listener.channelServerMessage(username, messageType);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.BEGIN_SEND_MAP: {
					this.listener.beginSendMap();
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.SEND_MAP_DATA: {
					final int sequenceNumber = data.getInt();
					final int mapDataPreLimit = data.limit();
					try {
						data.limit(position + 8 + length);
						this.listener.sendMapData(sequenceNumber, data);
					}
					finally {
						data.limit(mapDataPreLimit);
					}
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.END_SEND_MAP: {
					final int sequenceNumber = data.getInt();
					this.listener.endSendMap(sequenceNumber);
					break;
				}
				case GamingNetworkServerToClientListener.Protocol.SERVER_ERROR_MESSAGE: {
					final int messageTypeOrdinal = data.getInt();
					ServerErrorMessageType messageType;
					if ((messageTypeOrdinal >= 0) && (messageTypeOrdinal < ServerErrorMessageType.VALUES.length)) {
						messageType = ServerErrorMessageType.VALUES[messageTypeOrdinal];
					}
					else {
						messageType = null;
					}
					this.listener.serverErrorMessage(messageType);
					break;
				}
				default:
					break;
				}
				data.position(position + 8 + length);
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
