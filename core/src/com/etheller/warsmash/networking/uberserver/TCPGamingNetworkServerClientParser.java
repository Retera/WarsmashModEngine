package com.etheller.warsmash.networking.uberserver;

import java.nio.ByteBuffer;

import com.etheller.warsmash.util.War3ID;

import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;

public class TCPGamingNetworkServerClientParser implements TCPClientParser {
	private final GamingNetworkClientToServerListener listener;

	public TCPGamingNetworkServerClientParser(final GamingNetworkClientToServerListener listener) {
		this.listener = listener;
	}

	@Override
	public void parse(final ByteBuffer data) {
		while (data.remaining() > 8) {
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
					final String username = readString(64, data);
					final String password = readString(1024, data);
					this.listener.createAccount(username, password);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.LOGIN: {
					final String username = readString(64, data);
					final String password = readString(1024, data);
					this.listener.login(username, password);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.JOIN_CHANNEL: {
					final String channelName = readString(256, data);
					this.listener.joinChannel(channelName);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.CHAT_MESSAGE: {
					final String text = readString(256, data);
					this.listener.chatMessage(text);
					break;
				}
				case GamingNetworkClientToServerListener.Protocol.EMOTE_MESSAGE: {
					final String text = readString(256, data);
					this.listener.emoteMessage(text);
					break;
				}
				}
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

	@Override
	public void disconnected() {
		this.listener.disconnected();
	}

}
