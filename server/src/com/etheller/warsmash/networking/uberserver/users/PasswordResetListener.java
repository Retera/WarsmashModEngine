package com.etheller.warsmash.networking.uberserver.users;

import net.warsmash.uberserver.PasswordResetFailureReason;

public interface PasswordResetListener {
	void resetFailed(PasswordResetFailureReason reason);

	void resetOk();
}
