package com.etheller.warsmash.viewer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class ViewerNode {
	protected static final Vector3 locationHeap = new Vector3();
	protected static final Quaternion rotationHeap = new Quaternion();
	protected static final Vector3 scalingHeap = new Vector3();

	protected final Vector3 pivot;
	protected final Vector3 localLocation;
	protected final Quaternion localRotation;
	protected final Vector3 localScale;
	protected final Vector3 worldLocation;
	protected final Quaternion worldRotation;
	protected final Vector3 worldScale;
	protected final Vector3 inverseWorldLocation;
	protected final Quaternion inverseWorldRotation;
	protected final Vector3 inverseWorldScale;
	protected final Matrix4 localMatrix;
	protected final Matrix4 worldMatrix;
	protected final boolean dontInheritTranslation;
	protected final boolean dontInheritRotation;
	protected final boolean dontInheritScaling;
	protected boolean visible;
	protected boolean wasDirty;
	protected boolean dirty;

	protected ViewerNode parent;

	protected final List<ViewerNode> children;

	public ViewerNode() {
		this.pivot = new Vector3();
		this.localLocation = new Vector3();
		this.localRotation = new Quaternion(0, 0, 0, 1);
		this.localScale = new Vector3(1, 1, 1);
		this.worldLocation = new Vector3();
		this.worldRotation = new Quaternion();
		this.worldScale = new Vector3();
		this.inverseWorldLocation = new Vector3();
		this.inverseWorldRotation = new Quaternion();
		this.inverseWorldScale = new Vector3();
		this.localMatrix = new Matrix4();
		this.localMatrix.val[0] = 1;
		this.localMatrix.val[5] = 1;
		this.localMatrix.val[10] = 1;
		this.localMatrix.val[15] = 1;
		this.worldMatrix = new Matrix4();
		this.dontInheritTranslation = false;
		this.dontInheritRotation = false;
		this.dontInheritScaling = false;
		this.visible = true;
		this.wasDirty = false;
		this.dirty = true;
		this.children = new ArrayList<>();
	}

	public abstract void update(Scene scene);
}
