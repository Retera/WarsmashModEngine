package com.etheller.warsmash.networking.uberserver.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import net.warsmash.uberserver.PasswordResetFailureReason;

public class InRAMUserManager implements UserManager {
	private final List<UserImpl> users;
	private final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(17);

	private final transient XStream xstream = new XStream();

	public InRAMUserManager() {
		this.xstream
				.allowTypesByWildcard(new String[] { "com.etheller.warsmash.networking.uberserver.users.UserImpl" });
		final File usersFile = new File("users.db");
		if (!usersFile.exists()) {
			this.users = new ArrayList<>();
		}
		else {
			this.users = (List<UserImpl>) this.xstream.fromXML(usersFile);
			for (final UserImpl user : this.users) {
				user.resumeTransientFields(this);
			}
		}
	}

	@Override
	public UserImpl getUserByName(final String username) {
		for (final UserImpl user : this.users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public UserImpl createUser(final String username, final char[] password) {
		for (final UserImpl user : this.users) {
			if (user.getUsername().equals(username)) {
				return null;
			}
		}
		final String passwordHash = this.passwordAuthentication.hash(password);
		// TODO fix if users are given a way to delete accounts, can't do size+1
		final int userId = this.users.size() + 1;
		final UserImpl user = new UserImpl(username, passwordHash, userId,
				this.passwordAuthentication.hash(Integer.toHexString(userId + 0xFFFFFF00).toCharArray()), this);
		this.users.add(user);
		storeToHDD();
		return user;
	}

	private void storeToHDD() {
		final String usersXml = this.xstream.toXML(this.users);
		synchronized (this.users) {
			try (PrintWriter writer = new PrintWriter("users.db")) {
				writer.print(usersXml);
			}
			catch (final FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void passwordReset(final String username, final char[] password, final char[] newPassword,
			final PasswordResetListener authenticationListener) {
		final UserImpl user = getUserByName(username);
		if (user != null) {
			if (PasswordAuthentication.authenticate(password, user.getPasswordHash())) {
				user.setPasswordHash(this.passwordAuthentication.hash(newPassword));
				authenticationListener.resetOk();
			}
			else {
				authenticationListener.resetFailed(PasswordResetFailureReason.INVALID_CREDENTIALS);
			}
		}
		else {
			authenticationListener.resetFailed(PasswordResetFailureReason.UNKNOWN_USER);
		}
	}

	@Override
	public void notifyUsersUpdated() {
		storeToHDD();
	}
}
