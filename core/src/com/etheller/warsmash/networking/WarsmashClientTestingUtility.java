package com.etheller.warsmash.networking;

import net.warsmash.networking.udp.OrderedUdpClient;
import net.warsmash.uberserver.GamingNetwork;

import java.io.IOException;
import java.net.InetAddress;

public class WarsmashClientTestingUtility implements ServerToClientListener {
    private final OrderedUdpClient udpClient;
    private final WarsmashClientWriter writer;
    private int myTurn = -1;

    public WarsmashClientTestingUtility(InetAddress serverHost, int udpSingleGamePort, long sessionToken) throws IOException {
        udpClient = new OrderedUdpClient(serverHost, udpSingleGamePort, new WarsmashClientParser(this));
        writer = new WarsmashClientWriter(udpClient, sessionToken);
    }

    public void startThread() {
        new Thread(this.udpClient).start();
    }

    public WarsmashClientWriter getWriter() {
        return writer;
    }

    @Override
    public void acceptJoin(int playerIndex) {
        System.out.println("WarsmashClientTestingUtility.acceptJoin");
        System.out.println("playerIndex = " + playerIndex);
    }

    @Override
    public void issueTargetOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, int targetHandleId, boolean queue) {
        System.out.println("WarsmashClientTestingUtility.issueTargetOrder");
        System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = " + abilityHandleId + ", orderId = " + orderId + ", targetHandleId = " + targetHandleId + ", queue = " + queue);
    }

    @Override
    public void issuePointOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, float x, float y, boolean queue) {
        System.out.println("WarsmashClientTestingUtility.issuePointOrder");
        System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = " + abilityHandleId + ", orderId = " + orderId + ", x = " + x + ", y = " + y + ", queue = " + queue);
    }

    @Override
    public void issueDropItemAtPointOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, int targetHandleId, float x, float y, boolean queue) {
        System.out.println("WarsmashClientTestingUtility.issueDropItemAtPointOrder");
        System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = " + abilityHandleId + ", orderId = " + orderId + ", targetHandleId = " + targetHandleId + ", x = " + x + ", y = " + y + ", queue = " + queue);
    }

    @Override
    public void issueDropItemAtTargetOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, int targetHandleId, int targetHeroHandleId, boolean queue) {
        System.out.println("WarsmashClientTestingUtility.issueDropItemAtTargetOrder");
        System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = " + abilityHandleId + ", orderId = " + orderId + ", targetHandleId = " + targetHandleId + ", targetHeroHandleId = " + targetHeroHandleId + ", queue = " + queue);
    }

    @Override
    public void issueImmediateOrder(int playerIndex, int unitHandleId, int abilityHandleId, int orderId, boolean queue) {
        System.out.println("WarsmashClientTestingUtility.issueImmediateOrder");
        System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = " + abilityHandleId + ", orderId = " + orderId + ", queue = " + queue);
    }

    @Override
    public void unitCancelTrainingItem(int playerIndex, int unitHandleId, int cancelIndex) {
        System.out.println("WarsmashClientTestingUtility.unitCancelTrainingItem");
        System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", cancelIndex = " + cancelIndex);
    }

    @Override
    public void startGame() {
        System.out.println("WarsmashClientTestingUtility.startGame");
        System.out.println();
    }

    @Override
    public void finishedTurn(int gameTurnTick) {
        System.out.println("WarsmashClientTestingUtility.finishedTurn");
        System.out.println("gameTurnTick = " + gameTurnTick);

        while(myTurn <= gameTurnTick) {
            myTurn++;
            if(myTurn % 4 == 3) {
                writer.issuePointOrder(999, 1234, 8192, 0, 0, false);
            }
            writer.finishedTurn(myTurn);
            writer.send();
        }
    }

    @Override
    public void heartbeat() {
        System.out.println("WarsmashClientTestingUtility.heartbeat");
        System.out.println();
    }

    public static void main(String[] args) {
        long sessionToken = 1337002L;
        try {
            InetAddress localHost = InetAddress.getByName("creative.etheller.com");
            int udpSingleGamePort = GamingNetwork.UDP_SINGLE_GAME_PORT;
            WarsmashClientTestingUtility warsmashClientTestingUtility = new WarsmashClientTestingUtility(localHost, udpSingleGamePort, sessionToken);
            warsmashClientTestingUtility.startThread();
            WarsmashClientWriter writer = warsmashClientTestingUtility.getWriter();
            writer.joinGame();
            writer.send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
