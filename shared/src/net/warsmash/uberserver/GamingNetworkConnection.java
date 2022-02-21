package net.warsmash.uberserver;

public interface GamingNetworkConnection extends GamingNetworkClientToServerListener {
	void addListener(GamingNetworkServerToClientListener listener);

	void userRequestDisconnect();

	boolean userRequestConnect();

	String getGatewayString();
}
