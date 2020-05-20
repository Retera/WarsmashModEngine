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
public class FrameDefintion {
	private String frameType;
	private String name;
	private final List<FrameDefintion> innerFrames = new ArrayList<>();
	private final Set<String> flags = new HashSet<>();
	private final Map<String, FrameDefinitionField> nameToField = new HashMap<>();
	private final List<SetPointDefinition> setPoints = new ArrayList<>();
	private final List<AnchorDefinition> anchors = new ArrayList<>();

	public void inheritFrom(final FrameDefintion other, final boolean withChildren) {
		this.flags.addAll(other.flags);
		this.nameToField.putAll(other.nameToField);
		if (withChildren) {
			this.innerFrames.addAll(other.innerFrames);
		}
	}

	public void set(final String fieldName, final FrameDefinitionField value) {
		this.nameToField.put(fieldName, value);
	}

	public void add(final SetPointDefinition setPointDefinition) {
		this.setPoints.add(setPointDefinition);
	}

	public void add(final AnchorDefinition anchorDefinition) {
		this.anchors.add(anchorDefinition);
	}
}
