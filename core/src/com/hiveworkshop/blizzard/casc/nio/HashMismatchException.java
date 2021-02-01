package com.hiveworkshop.blizzard.casc.nio;

import java.io.IOException;

public class HashMismatchException extends IOException {
	private static final long serialVersionUID = -7133950344327038673L;

	public HashMismatchException() {
	}

	public HashMismatchException(String message) {
		super(message);
	}

}
