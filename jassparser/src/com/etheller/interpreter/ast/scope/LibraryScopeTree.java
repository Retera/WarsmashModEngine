package com.etheller.interpreter.ast.scope;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.etheller.interpreter.ast.qualifier.JassQualifier;

public class LibraryScopeTree {
	private final LinkedList<Node> scopes = new LinkedList<>();

	public <T> T forEachPossibleResolvedIdentifier(final String identifier, final ScopeTreeHandler<T> handler) {
		final Iterator<Node> descendingIterator = this.scopes.descendingIterator();
		final StringBuilder sb = new StringBuilder();
		while (descendingIterator.hasNext()) {
			final Node node = descendingIterator.next();
			for (final String prefix : node.prefixes) {
				sb.setLength(0);
				sb.append(prefix);
				sb.append(identifier);
				final T userValue = handler.identifier(sb.toString());
				if (userValue != null) {
					return userValue;
				}
			}
		}
		return handler.identifier(identifier);
	}

	public LibraryScopeTree descend(final String namespace, final boolean library) {
		final StringBuilder qualifiedNameBuilder = new StringBuilder();
		if (!this.scopes.isEmpty()) {
			final Node last = this.scopes.getLast();
			qualifiedNameBuilder.append(last.fullyQualifiedPublicName);
			qualifiedNameBuilder.append('_');
		}
		qualifiedNameBuilder.append(namespace);
		final Node node = new Node(namespace, qualifiedNameBuilder.toString());

		final LibraryScopeTree child = new LibraryScopeTree();
		child.scopes.addAll(this.scopes);
		child.scopes.addLast(node);
		return child;
	}

	public String getQualifiedIdentifier(final String identifier, final EnumSet<JassQualifier> qualifiers) {
		if (qualifiers.contains(JassQualifier.PRIVATE)) {
			return getQualifiedNamePrivate(identifier);
		}
		else if (qualifiers.contains(JassQualifier.PUBLIC)) {
			return getQualifiedNamePublic(identifier);
		}
		return identifier;
	}

	public String getQualifiedNamePublic(final String identifier) {
		if (this.scopes.isEmpty()) {
			throw new IllegalStateException("Unable to apply public qualifier outside scope: " + identifier);
		}
		final Node last = this.scopes.getLast();
		return last.getPublicPrefix() + identifier;
	}

	public String getQualifiedNamePrivate(final String identifier) {
		if (this.scopes.isEmpty()) {
			throw new IllegalStateException("Unable to apply private qualifier outside scope: " + identifier);
		}
		final Node last = this.scopes.getLast();
		return last.getPrivatePrefix() + identifier;
	}

	public boolean isEmpty() {
		return this.scopes.isEmpty();
	}

	public String getNamespace() {
		if (isEmpty()) {
			return null;
		}
		final Node last = this.scopes.getLast();
		return last.namespace;
	}

	private static final class Node {
		private final String namespace;
		private final String fullyQualifiedPublicName;
		private final List<String> prefixes;

		public Node(final String namespace, final String fullyQualifiedPublicName) {
			this.namespace = namespace;
			this.fullyQualifiedPublicName = fullyQualifiedPublicName;
			this.prefixes = new ArrayList<>();
			// check for private
			this.prefixes.add(getPrivatePrefix());
			// check for public
			this.prefixes.add(getPublicPrefix());
		}

		public String getPrivatePrefix() {
			return this.fullyQualifiedPublicName + this.namespace.hashCode() + "__";
		}

		public String getPublicPrefix() {
			return this.fullyQualifiedPublicName + "_";
		}
	}

	public static interface ScopeTreeHandler<T> {
		// return non-null to interrupt
		public T identifier(String identifier);
	}
}
