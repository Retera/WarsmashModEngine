package net.warsmash.uberserver;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.etheller.warsmash.util.War3ID;

import net.warsmash.networking.util.AbstractWriter;
import net.warsmash.nio.channels.WritableOutput;

public class GamingNetworkClientToServerWriter extends AbstractWriter implements GamingNetworkClientToServerListener {

	public GamingNetworkClientToServerWriter(final WritableOutput writableOutput) {
		super(writableOutput);
	}

	@Override
	public void handshake(final String gameId, final int version) {
		final War3ID war3Id = War3ID.fromString(gameId);
		beginMessage(Protocol.HANDSHAKE, 4 + 4);
		this.writeBuffer.putInt(war3Id.getValue());
		this.writeBuffer.putInt(version);
		send();
	}

	@Override
	public void login(String username, final char[] passwordHash) {
		if (username.length() > GamingNetwork.USERNAME_MAX_LENGTH) {
			username = username.substring(0, GamingNetwork.USERNAME_MAX_LENGTH);
		}
		final byte[] usernameBytes = username.getBytes();
		final int passwordHashUsedBytes = Math.min(passwordHash.length, GamingNetwork.PASSWORD_DATA_MAX_LENGTH);
		beginMessage(Protocol.LOGIN, 4 + usernameBytes.length + 4 + (passwordHashUsedBytes * 2));
		this.writeBuffer.putInt(usernameBytes.length);
		this.writeBuffer.put(usernameBytes);
		this.writeBuffer.putInt(passwordHashUsedBytes);
		for (int i = 0; i < passwordHashUsedBytes; i++) {
			this.writeBuffer.putChar(passwordHash[i]);
		}
		send();
	}

	@Override
	public void joinChannel(final long sessionToken, String channelName) {
		if (channelName.length() > GamingNetwork.CHANNEL_NAME_MAX_LENGTH) {
			channelName = channelName.substring(0, GamingNetwork.CHANNEL_NAME_MAX_LENGTH);
		}
		final byte[] channelNameBytes = channelName.getBytes(Charset.forName("utf-8"));
		beginMessage(Protocol.JOIN_CHANNEL, 8 + 4 + channelNameBytes.length);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(channelNameBytes.length);
		this.writeBuffer.put(channelNameBytes);
		send();
	}

	@Override
	public void emoteMessage(final long sessionToken, String text) {
		if (text.length() > GamingNetwork.MESSAGE_MAX_LENGTH) {
			text = text.substring(0, GamingNetwork.MESSAGE_MAX_LENGTH);
		}
		final byte[] bytes = text.getBytes(Charset.forName("utf-8"));
		beginMessage(Protocol.EMOTE_MESSAGE, 8 + 4 + bytes.length);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(bytes.length);
		this.writeBuffer.put(bytes);
		send();
	}

	@Override
	public void chatMessage(final long sessionToken, String text) {
		if (text.length() > GamingNetwork.MESSAGE_MAX_LENGTH) {
			text = text.substring(0, GamingNetwork.MESSAGE_MAX_LENGTH);
		}
		final byte[] bytes = text.getBytes(Charset.forName("utf-8"));
		beginMessage(Protocol.CHAT_MESSAGE, 8 + 4 + bytes.length);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(bytes.length);
		this.writeBuffer.put(bytes);
		send();
	}

	@Override
	public void createAccount(String username, final char[] passwordHash) {
		if (username.length() > GamingNetwork.USERNAME_MAX_LENGTH) {
			username = username.substring(0, GamingNetwork.USERNAME_MAX_LENGTH);
		}
		final byte[] usernameBytes = username.getBytes();
		final int passwordHashUsedBytes = Math.min(passwordHash.length, GamingNetwork.PASSWORD_DATA_MAX_LENGTH);
		beginMessage(Protocol.CREATE_ACCOUNT, 4 + usernameBytes.length + 4 + (passwordHashUsedBytes * 2));
		this.writeBuffer.putInt(usernameBytes.length);
		this.writeBuffer.put(usernameBytes);
		this.writeBuffer.putInt(passwordHashUsedBytes);
		for (int i = 0; i < passwordHashUsedBytes; i++) {
			this.writeBuffer.putChar(passwordHash[i]);
		}
		send();
	}

	@Override
	public void queryGamesList(final long sessionToken) {
		beginMessage(Protocol.QUERY_GAMES_LIST, 8);
		this.writeBuffer.putLong(sessionToken);
		send();
	}

	@Override
	public void queryGameInfo(final long sessionToken, String gameName) {
		if (gameName.length() > GamingNetwork.CHANNEL_NAME_MAX_LENGTH) {
			gameName = gameName.substring(0, GamingNetwork.CHANNEL_NAME_MAX_LENGTH);
		}
		final byte[] channelNameBytes = gameName.getBytes(Charset.forName("utf-8"));
		beginMessage(Protocol.QUERY_GAME_INFO, 8 + 4 + channelNameBytes.length);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(channelNameBytes.length);
		this.writeBuffer.put(channelNameBytes);
		send();
	}

	@Override
	public void joinGame(final long sessionToken, String gameName) {
		if (gameName.length() > GamingNetwork.CHANNEL_NAME_MAX_LENGTH) {
			gameName = gameName.substring(0, GamingNetwork.CHANNEL_NAME_MAX_LENGTH);
		}
		final byte[] channelNameBytes = gameName.getBytes(Charset.forName("utf-8"));
		beginMessage(Protocol.JOIN_GAME, 8 + 4 + channelNameBytes.length);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(channelNameBytes.length);
		this.writeBuffer.put(channelNameBytes);
		send();
	}

	@Override
	public void createGame(final long sessionToken, String gameName, String mapName, final int totalSlots,
			final LobbyGameSpeed gameSpeed, final HostedGameVisibility visibility, long mapChecksum) {
		if (gameName.length() > GamingNetwork.CHANNEL_NAME_MAX_LENGTH) {
			gameName = gameName.substring(0, GamingNetwork.CHANNEL_NAME_MAX_LENGTH);
		}
		final byte[] channelNameBytes = gameName.getBytes(Charset.forName("utf-8"));
		if (mapName.length() > GamingNetwork.MAP_NAME_MAX_LENGTH) {
			mapName = mapName.substring(0, GamingNetwork.MAP_NAME_MAX_LENGTH);
		}
		final byte[] mapNameBytes = mapName.getBytes(Charset.forName("utf-8"));
		beginMessage(Protocol.CREATE_GAME, 8 + 4 + channelNameBytes.length + 4 + mapNameBytes.length + 4 + 4 + 4 + 8);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(channelNameBytes.length);
		this.writeBuffer.put(channelNameBytes);
		this.writeBuffer.putInt(mapNameBytes.length);
		this.writeBuffer.put(mapNameBytes);
		this.writeBuffer.putInt(totalSlots);
		this.writeBuffer.putInt(gameSpeed.ordinal());
		this.writeBuffer.putInt(visibility.ordinal());
		this.writeBuffer.putLong(mapChecksum);
		send();
	}

	@Override
	public void leaveGame(long sessionToken) {
		beginMessage(Protocol.LEAVE_GAME, 8);
		this.writeBuffer.putLong(sessionToken);
		send();
	}

	@Override
	public void uploadMapData(long sessionToken, int sequenceNumber, ByteBuffer data) {
		beginMessage(Protocol.UPLOAD_MAP_DATA, 8 + 4 + data.remaining());
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(sequenceNumber);
		this.writeBuffer.put(data);
		send();
	}

	@Override
	public void mapDone(long sessionToken, int sequenceNumber) {
		beginMessage(Protocol.UPLOAD_MAP_DATA_DONE, 8 + 4);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(sequenceNumber);
		send();
	}

	@Override
	public void requestMap(long sessionToken) {
		beginMessage(Protocol.REQUEST_MAP, 8);
		this.writeBuffer.putLong(sessionToken);
		send();
	}

	@Override
	public void gameLobbySetPlayerSlot(long sessionToken, int slot, LobbyPlayerType lobbyPlayerType) {
		beginMessage(Protocol.GAME_LOBBY_SET_PLAYER_SLOT, 8 + 4 + 4);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(slot);
		this.writeBuffer.putInt(lobbyPlayerType.ordinal());
		send();
	}

	@Override
	public void gameLobbySetPlayerRace(long sessionToken, int slot, int raceItemIndex) {
		beginMessage(Protocol.GAME_LOBBY_SET_PLAYER_RACE, 8 + 4 + 4);
		this.writeBuffer.putLong(sessionToken);
		this.writeBuffer.putInt(slot);
		this.writeBuffer.putInt(raceItemIndex);
		send();
	}

	@Override
	public void disconnected() {
		throw new UnsupportedOperationException();
	}
}
