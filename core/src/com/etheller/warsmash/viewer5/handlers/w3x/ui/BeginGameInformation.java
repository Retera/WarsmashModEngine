package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.utils.IntIntMap;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.CurrentNetGameMapLookup;

public class BeginGameInformation {
	public CurrentNetGameMapLookup gameMapLookup;
	public byte[] hostInetAddress;
	public int hostUdpPort;
	public long sessionToken;
	public int localPlayerIndex;
	public IntIntMap serverSlotToMapSlot;
	public IntIntMap mapSlotToServerSlot;
	public boolean loadingStarted = false;
}
