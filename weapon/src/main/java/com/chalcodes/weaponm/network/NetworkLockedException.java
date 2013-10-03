package com.chalcodes.weaponm.network;

import java.io.IOException;

public class NetworkLockedException extends IOException {
	private static final long serialVersionUID = 1L;
	
	NetworkLockedException() {
		super("Network is locked.");
	}
}
