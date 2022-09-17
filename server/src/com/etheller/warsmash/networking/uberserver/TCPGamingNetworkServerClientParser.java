package com.etheller.warsmash.networking.uberserver;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.etheller.warsmash.util.War3ID;

import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.uberserver.GamingNetwork;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;
import net.warsmash.uberserver.HostedGameVisibility;
import net.warsmash.uberserver.LobbyGameSpeed;
import net.warsmash.uberserver.LobbyPlayerType;

public class TCPGamingNetworkServerClientParser implements TCPClientParser {
	private final GamingNetworkClientToServerListener listener;

	public TCPGamingNetworkServerClientParser(final GamingNetworkClientToServerListener listener) {
		this.listener = listener;
	}

	@Override
	public void parse(final ByteBuffer data) {
		while (data.remaining() >= 8) {
			final int position = data.position();
			final int protocolMessageId = data.getInt(position + 0);
			final int length = data.getInt(position + 4);
			if (length < 0) {
				throw new IllegalStateException("Negative length value received from client while parsing: " + length);
			}
			if (data.remaining() >= (length + 8)) {
				data.position(position + 8);
				switch (protocolMessageId) {
				case GamingNetworkClientToServerListener.Protocol.HANDSHAKE: {
					final int gameIdInt = data.getInt();
					final int gameVersion = data.getInt();
					this.listener.handshake(new War3ID(gameIdInt).toString(), gameVersion);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.CREATE_ACCOUNT: {
					final String username = readString(GamingNetwork.USERNAME_MAX_LENGTH, data);
					final char[] password = readChars(GamingNetwork.PASSWORD_DATA_MAX_LENGTH, data);
					this.listener.createAccount(username, password);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.LOGIN: {
					final String username = readString(GamingNetwork.USERNAME_MAX_LENGTH, data);
					final char[] password = readChars(GamingNetwork.PASSWORD_DATA_MAX_LENGTH, data);
					this.listener.login(username, password);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.JOIN_CHANNEL: {
					final long sessionToken = data.getLong();
					final String channelName = readString(GamingNetwork.MESSAGE_MAX_LENGTH, data);
					this.listener.joinChannel(sessionToken, channelName);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.CHAT_MESSAGE: {
					final long sessionToken = data.getLong();
					final String text = readString(GamingNetwork.MESSAGE_MAX_LENGTH, data);
					this.listener.chatMessage(sessionToken, text);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.EMOTE_MESSAGE: {
					final long sessionToken = data.getLong();
					final String text = readString(GamingNetwork.MESSAGE_MAX_LENGTH, data);
					this.listener.emoteMessage(sessionToken, text);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.QUERY_GAMES_LIST: {
					final long sessionToken = data.getLong();
					this.listener.queryGamesList(sessionToken);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.QUERY_GAME_INFO: {
					final long sessionToken = data.getLong();
					final String gameName = readString(GamingNetwork.CHANNEL_NAME_MAX_LENGTH, data);
					this.listener.queryGameInfo(sessionToken, gameName);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.JOIN_GAME: {
					final long sessionToken = data.getLong();
					final String gameName = readString(GamingNetwork.CHANNEL_NAME_MAX_LENGTH, data);
					this.listener.joinGame(sessionToken, gameName);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.CREATE_GAME: {
					final long sessionToken = data.getLong();
					final String gameName = readString(GamingNetwork.CHANNEL_NAME_MAX_LENGTH, data);
					final String mapName = readString(GamingNetwork.MAP_NAME_MAX_LENGTH, data);
					final int totalSlots = data.getInt();
					final LobbyGameSpeed gameSpeed = LobbyGameSpeed.VALUES[data.getInt()];
					final HostedGameVisibility visibility = HostedGameVisibility.VALUES[data.getInt()];
					final long mapChecksum = data.getLong();
					this.listener.createGame(sessionToken, gameName, mapName, totalSlots, gameSpeed, visibility,
							mapChecksum);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.LEAVE_GAME: {
					final long sessionToken = data.getLong();
					this.listener.leaveGame(sessionToken);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.UPLOAD_MAP_DATA: {
					final long sessionToken = data.getLong();
					final int sequenceNumber = data.getInt();
					final int mapDataPreLimit = data.limit();
					try {
						data.limit(position + 8 + length);
						this.listener.uploadMapData(sessionToken, sequenceNumber, data);
					}
					finally {
						data.limit(mapDataPreLimit);
					}
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.UPLOAD_MAP_DATA_DONE: {
					final long sessionToken = data.getLong();
					final int sequenceNumber = data.getInt();
					this.listener.mapDone(sessionToken, sequenceNumber);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.REQUEST_MAP: {
					final long sessionToken = data.getLong();
					this.listener.requestMap(sessionToken);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.GAME_LOBBY_SET_PLAYER_SLOT: {
					final long sessionToken = data.getLong();
					final int slot = data.getInt();
					final int playerTypeOrdinal = data.getInt();
					LobbyPlayerType playerType;
					if ((playerTypeOrdinal >= 0) && (playerTypeOrdinal < LobbyPlayerType.VALUES.length)) {
						playerType = LobbyPlayerType.VALUES[playerTypeOrdinal];
					}
					else {
						playerType = null;
					}
					this.listener.gameLobbySetPlayerSlot(sessionToken, slot, playerType);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.GAME_LOBBY_SET_PLAYER_RACE: {
					final long sessionToken = data.getLong();
					final int slot = data.getInt();
					final int raceItemIndex = data.getInt();
					this.listener.gameLobbySetPlayerRace(sessionToken, slot, raceItemIndex);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.GAME_LOBBY_START_GAME: {
					final long sessionToken = data.getLong();
					this.listener.gameLobbyStartGame(sessionToken);
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
