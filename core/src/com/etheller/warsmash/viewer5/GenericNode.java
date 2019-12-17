package com.etheller.warsmash.viewer5;

import java.util.List;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class GenericNode {

	protected Vector3 pivot;
	protected Vector3 localLocation;
	protected Quaternion localRotation;
	protected Vector3 localScale;
	protected Vector3 worldLocation;
	protected Quaternion worldRotation;
	protected Vector3 worldScale;
	protected Vector3 inverseWorldLocation;
	protected Quaternion inverseWorldRotation;
	protected Vector3 inverseWorldScale;
	protected Matrix4 localMatrix;
	protected Matrix4 worldMatrix;
	protected GenericNode parent;
	protected List<GenericNode> children;
	public boolean dontInheritTranslation;
	public boolean dontInheritRotation;
	public boolean dontInheritScaling;
	protected boolean visible;
	protected boolean wasDirty;
	protected boolean dirty;

	protected abstract void update(float dt, Scene scene);
}
