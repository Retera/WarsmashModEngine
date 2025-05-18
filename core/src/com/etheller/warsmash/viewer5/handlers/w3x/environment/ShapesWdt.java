package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.etheller.warsmash.util.RenderMathUtils;

public class ShapesWdt {
	public static ShapesWdt INSTANCE = new ShapesWdt();
	static {
		INSTANCE.init();
	}

	public int vertexBuffer;
	public int indexBuffer;

	float[][] quadVertices = { { 1, 1 }, { 0, 1 }, { 0, 0 }, { 1, 0 }, { 0.5f, 0.5f } };

	int[][] quadIndices = { { 1, 4, 0 }, { 2, 4, 1 }, { 3, 4, 2 }, { 0, 4, 3 } };

	public void init() {
		this.vertexBuffer = Gdx.gl30.glGenBuffer();
		Gdx.gl30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vertexBuffer);
		Gdx.gl30.glBufferData(GL30.GL_ARRAY_BUFFER, this.quadVertices.length * 8,
				RenderMathUtils.wrapPairs(this.quadVertices), GL30.GL_STATIC_DRAW);

		this.indexBuffer = Gdx.gl30.glGenBuffer();
		Gdx.gl30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer);
		Gdx.gl30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, this.quadIndices.length * 3 * 4,
				RenderMathUtils.wrap(this.quadIndices), GL30.GL_STATIC_DRAW);

	}
}
