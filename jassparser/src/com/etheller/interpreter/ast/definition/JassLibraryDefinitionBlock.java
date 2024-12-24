package com.etheller.interpreter.ast.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.statement.JassCallStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.JassType;

public class JassLibraryDefinitionBlock implements JassDefinitionBlock {
	private final int lineNo;
	private final String sourceFile;
	private final String libraryName;
	private final List<JassLibraryRequirementDefinition> requirements;
	private final List<JassDefinitionBlock> blocks;
	private final String initializerName;
	private final boolean once;

	public JassLibraryDefinitionBlock(final int lineNo, final String sourceFile, final String libraryName,
			final List<JassLibraryRequirementDefinition> requirements, final List<JassDefinitionBlock> blocks,
			final String initializerName, final boolean once) {
		this.lineNo = lineNo;
		this.sourceFile = sourceFile;
		this.libraryName = libraryName;
		this.requirements = requirements;
		this.blocks = blocks;
		this.initializerName = initializerName;
		this.once = once;
	}

	public String getLibraryName() {
		return this.libraryName;
	}

	public List<JassLibraryRequirementDefinition> getRequirements() {
		return this.requirements;
	}

	public List<JassDefinitionBlock> getBlocks() {
		return this.blocks;
	}

	public String getInitializerName() {
		return this.initializerName;
	}

	public boolean isOnce() {
		return this.once;
	}

	@Override
	public void define(final Scope scope, final JassProgram jassProgram) {
		final String constantName = "LIBRARY_" + this.libraryName;
		if (scope.getAssignableGlobal(constantName) != null) {
			if (!this.once) {
				throw new IllegalStateException("Library declared multiple times: " + this.libraryName);
			}
		}
		else {
			final Scope childScope = scope.createNestedScope(this.libraryName, true);
			scope.createGlobal(EnumSet.noneOf(JassQualifier.class), constantName, JassType.BOOLEAN,
					BooleanJassValue.TRUE);
			for (final JassDefinitionBlock block : this.blocks) {
				block.define(childScope, jassProgram);
			}
			if (this.initializerName != null) {
				childScope.defineGlobals(this.lineNo, this.sourceFile, Arrays.<JassStatement>asList(
						new JassCallStatement(this.initializerName, Collections.emptyList())), childScope);
			}
		}
	}

	public static void topologicalSort(final List<JassLibraryDefinitionBlock> libraries) {
		final Map<String, TopoSortNode> nameToLibrary = new HashMap<>();
		final Set<TopoSortNode> processingSet = new LinkedHashSet<>();
		final List<TopoSortNode> nodes = new ArrayList<>();
		for (final JassLibraryDefinitionBlock library : libraries) {
			final TopoSortNode node = new TopoSortNode(library);
			final TopoSortNode previousValue = nameToLibrary.put(library.getLibraryName(), node);
			if (previousValue != null) {
				if (!node.library.isOnce() || !previousValue.library.isOnce()) {
					throw new IllegalStateException(
							"Library declared multiple times: " + node.library.getLibraryName());
				}
			}
			if (library.getRequirements().isEmpty()) {
				processingSet.add(node);
			}
			nodes.add(node);
		}
		libraries.clear();
		for (final TopoSortNode node : nodes) {
			for (final JassLibraryRequirementDefinition requirement : node.library.getRequirements()) {
				final String requirementName = requirement.getRequirement();
				final TopoSortNode requirementLibrary = nameToLibrary.get(requirementName);
				if (requirementLibrary == null) {
					if (!requirement.isOptional()) {
						throw new IllegalStateException("library " + node.library.getLibraryName() + " requires "
								+ requirementName + ", but \"" + requirementName + "\" was not found!");
					}
				}
				else {
					requirementLibrary.dependentChildren.add(node);
					node.requirements.add(requirementLibrary);
				}
			}
		}
		while (!processingSet.isEmpty()) {
			final Iterator<TopoSortNode> iterator = processingSet.iterator();
			final TopoSortNode currentNode = iterator.next();
			iterator.remove();
			libraries.add(currentNode.library);
			for (final TopoSortNode childOfCurrent : currentNode.dependentChildren) {
				childOfCurrent.requirements.remove(currentNode);
				if (childOfCurrent.requirements.isEmpty()) {
					processingSet.add(childOfCurrent);
				}
			}
		}
		for (final TopoSortNode node : nodes) {
			if (!node.requirements.isEmpty()) {
				throw new IllegalStateException("Some dependency loop was detected!");
			}
		}
	}

	private static final class TopoSortNode {
		private final JassLibraryDefinitionBlock library;
		private final List<TopoSortNode> dependentChildren = new ArrayList<>();
		private final Set<TopoSortNode> requirements = new HashSet<>();

		public TopoSortNode(final JassLibraryDefinitionBlock library) {
			this.library = library;
		}
	}

}
