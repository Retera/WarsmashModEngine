package com.etheller.warsmash.networking.uberserver;

import java.nio.ByteBuffer;

import com.etheller.warsmash.util.War3ID;

import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.uberserver.GamingNetwork;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;

public class TCPGamingNetworkServerClientParser implements TCPClientParser {
	private final GamingNetworkClientToServerListener listener;

	public TCPGamingNetworkServerClientParser(final GamingNetworkClientToServerListener listener) {
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
		final String username = new String(usernameStringBytes);
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
