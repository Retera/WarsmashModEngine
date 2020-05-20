package com.etheller.warsmash.parsers.fdf.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.parsers.fdf.datamodel.fields.FrameDefinitionField;

/**
 * Pretty sure this is probably not how it works in-game but this silly
 * everything class might help get a prototype running fast until I have a
 * better understanding of how I want these classes designed.
 */
public class FrameDefinition {
	private final FrameClass frameClass;
	private final String frameType;
	private final String name;
	private final List<FrameDefinition> innerFrames = new ArrayList<>();
	private final Set<String> flags = new HashSet<>();
	private final Map<String, FrameDefinitionField> nameToField = new HashMap<>();
	private final List<SetPointDefinition> setPoints = new ArrayList<>();
	private final List<AnchorDefinition> anchors = new ArrayList<>();

	public FrameDefinition(final FrameClass frameClass, final String frameType, final String name) {
		this.frameClass = frameClass;
		this.frameType = frameType;
		this.name = name;
	}

	public void inheritFrom(final FrameDefinition other, final boolean withChildren) {
		this.flags.addAll(other.flags);
		this.nameToField.putAll(other.nameToField);
		if (withChildren) {
			this.innerFrames.addAll(other.innerFrames);
		}
	}

	public void set(final String fieldName, final FrameDefinitionField value) {
		this.nameToField.put(fieldName, value);
	}

	public void add(final FrameDefinition childDefition) {
		this.innerFrames.add(childDefition);
	}

	public void add(final SetPointDefinition setPointDefinition) {
		this.setPoints.add(setPointDefinition);
	}

	public void add(final AnchorDefinition anchorDefinition) {
		this.anchors.add(anchorDefinition);
	}

	public void add(final String flag) {
		this.flags.add(flag);
	}

	@Override
	public String toString() {
		return "FrameDefinition [frameClass=" + this.frameClass + ", frameType=" + this.frameType + ", name="
				+ this.name + ", innerFrames=" + this.innerFrames + ", flags=" + this.flags + ", nameToField="
				+ this.nameToField + ", setPoints=" + this.setPoints + ", anchors=" + this.anchors + "]";
	}

}
