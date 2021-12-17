package com.etheller.warsmash.parsers.fdf.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.parsers.fdf.datamodel.fields.FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.RepeatingFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringPairFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetFloatFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetFontFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetRepeatingFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetStringFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetStringPairFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetTextJustifyFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetVector2FieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor.GetVector4FieldVisitor;

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

	public void add(final String fieldName, final FrameDefinitionField value) {
		final FrameDefinitionField field = this.nameToField.get(fieldName);
		List<FrameDefinitionField> fields;
		if (field == null) {
			final RepeatingFrameDefinitionField repeatingFrameDefinitionField = new RepeatingFrameDefinitionField();
			this.nameToField.put(fieldName, repeatingFrameDefinitionField);
			fields = repeatingFrameDefinitionField.getFields();
		}
		else {
			fields = field.visit(GetRepeatingFieldVisitor.INSTANCE);
		}
		fields.add(value);
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

	public boolean has(final String flag) {
		return this.flags.contains(flag);
	}

	public FrameDefinitionField get(final String fieldName) {
		return this.nameToField.get(fieldName);
	}

	@Override
	public String toString() {
		return "FrameDefinition [frameClass=" + this.frameClass + ", frameType=" + this.frameType + ", name="
				+ this.name + ", innerFrames=" + this.innerFrames + ", flags=" + this.flags + ", nameToField="
				+ this.nameToField + ", setPoints=" + this.setPoints + ", anchors=" + this.anchors + "]";
	}

	public String getFrameType() {
		return this.frameType;
	}

	public String getName() {
		return this.name;
	}

	public FrameClass getFrameClass() {
		return this.frameClass;
	}

	public List<FrameDefinition> getInnerFrames() {
		return this.innerFrames;
	}

	public List<AnchorDefinition> getAnchors() {
		return this.anchors;
	}

	public List<SetPointDefinition> getSetPoints() {
		return this.setPoints;
	}

	public String getString(final String id) {
		final FrameDefinitionField frameDefinitionField = this.nameToField.get(id);
		if (frameDefinitionField != null) {
			return frameDefinitionField.visit(GetStringFieldVisitor.INSTANCE);
		}
		return null;
	}

	public StringPairFrameDefinitionField getStringPair(final String id) {
		final FrameDefinitionField frameDefinitionField = this.nameToField.get(id);
		if (frameDefinitionField != null) {
			return frameDefinitionField.visit(GetStringPairFieldVisitor.INSTANCE);
		}
		return null;
	}

	public Float getFloat(final String id) {
		final FrameDefinitionField frameDefinitionField = this.nameToField.get(id);
		if (frameDefinitionField != null) {
			return frameDefinitionField.visit(GetFloatFieldVisitor.INSTANCE);
		}
		return null;
	}

	public Vector4Definition getVector4(final String id) {
		final FrameDefinitionField frameDefinitionField = this.nameToField.get(id);
		if (frameDefinitionField != null) {
			return frameDefinitionField.visit(GetVector4FieldVisitor.INSTANCE);
		}
		return null;
	}

	public Vector2Definition getVector2(final String id) {
		final FrameDefinitionField frameDefinitionField = this.nameToField.get(id);
		if (frameDefinitionField != null) {
			return frameDefinitionField.visit(GetVector2FieldVisitor.INSTANCE);
		}
		return null;
	}

	public FontDefinition getFont(final String id) {
		final FrameDefinitionField frameDefinitionField = this.nameToField.get(id);
		if (frameDefinitionField != null) {
			return frameDefinitionField.visit(GetFontFieldVisitor.INSTANCE);
		}
		return null;
	}

	public List<FrameDefinitionField> getFields(final String fieldName) {
		final FrameDefinitionField field = this.nameToField.get(fieldName);
		if (field != null) {
			return field.visit(GetRepeatingFieldVisitor.INSTANCE);
		}
		return null;
	}

	public TextJustify getTextJustify(final String id) {
		final FrameDefinitionField frameDefinitionField = this.nameToField.get(id);
		if (frameDefinitionField != null) {
			return frameDefinitionField.visit(GetTextJustifyFieldVisitor.INSTANCE);
		}
		return null;
	}
}
