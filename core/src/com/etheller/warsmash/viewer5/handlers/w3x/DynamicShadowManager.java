package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WebGL;

public class DynamicShadowManager {
	public static boolean IS_SHADOW_MAPPING;

	private final Vector3 shadowVector = new Vector3();
	private final Matrix4 depthProjectionMatrix = new Matrix4();
	private final Matrix4 depthViewMatrix = new Matrix4();
	private final Matrix4 depthModelMatrix = new Matrix4();
	private final Matrix4 depthMVP = new Matrix4();
	private final Matrix4 biasMatrix = new Matrix4();
	private final Matrix4 depthBiasMVP = new Matrix4();

	public boolean setup(final WebGL webGL) {
		// The framebuffer, which regroups 0, 1, or more textures, and 0 or 1 depth
		// buffer.
		final GL30 gl = Gdx.gl30;
		this.framebufferName = 0;
		this.framebufferName = gl.glGenFramebuffer();
		gl.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebufferName);

		this.depthTexture = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.depthTexture);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT16, 1024, 1024, 0, GL30.GL_DEPTH_COMPONENT,
				GL30.GL_FLOAT, null);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
		Extensions.dynamicShadowExtension.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
				this.depthTexture, 0);

		Extensions.dynamicShadowExtension.glDrawBuffer(GL30.GL_NONE); // No color buffer is drawn to.

		// Always check that our framebuffer is ok
		if (gl.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			return false;
		}
		Gdx.gl30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		return true;
	}

	/**
	 * @return
	 */
	public Matrix4 prepareShadowMatrix() {
		final Vector3 lightInvDir = this.shadowVector;
		lightInvDir.set(500f, 2000, 2000);

		// Compute the MVP matrix from the light's point of view
		this.depthProjectionMatrix.setToOrtho(-10, 10, -10, 10, -10, 20);
		this.depthViewMatrix.set(this.depthProjectionMatrix);
		this.depthViewMatrix.setToLookAt(lightInvDir, Vector3.Zero, RenderMathUtils.VEC3_UNIT_Y);
		this.depthModelMatrix.idt();
		this.depthMVP.set(this.depthProjectionMatrix).mul(this.depthViewMatrix).mul(this.depthModelMatrix);

//		this.shader.setUniformMatrix("depthMVP", this.depthMVP);

		this.biasMatrix.val[Matrix4.M00] = 0.5f;
		this.biasMatrix.val[Matrix4.M10] = 0.0f;
		this.biasMatrix.val[Matrix4.M20] = 0.0f;
		this.biasMatrix.val[Matrix4.M30] = 0.5f;
		this.biasMatrix.val[Matrix4.M01] = 0.0f;
		this.biasMatrix.val[Matrix4.M11] = 0.5f;
		this.biasMatrix.val[Matrix4.M21] = 0.0f;
		this.biasMatrix.val[Matrix4.M31] = 0.5f;
		this.biasMatrix.val[Matrix4.M02] = 0.0f;
		this.biasMatrix.val[Matrix4.M12] = 0.0f;
		this.biasMatrix.val[Matrix4.M22] = 0.5f;
		this.biasMatrix.val[Matrix4.M32] = 0.5f;
		this.biasMatrix.val[Matrix4.M03] = 0.0f;
		this.biasMatrix.val[Matrix4.M13] = 0.0f;
		this.biasMatrix.val[Matrix4.M23] = 0.0f;
		this.biasMatrix.val[Matrix4.M33] = 1.0f;
		this.depthBiasMVP.set(this.biasMatrix).mul(this.depthMVP);

		return this.depthMVP;
	}

	public void beginShadowMap(final WebGL webGL) {
		IS_SHADOW_MAPPING = true;

		Gdx.gl30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebufferName);
		Extensions.dynamicShadowExtension.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
				this.depthTexture, 0);

		Extensions.dynamicShadowExtension.glDrawBuffer(GL30.GL_NONE); // No color buffer is drawn to.
		Gdx.gl30.glViewport(0, 0, 1024, 1024);

	}

	public Matrix4 getDepthBiasMVP() {
		return this.depthBiasMVP;
	}

	// Don't forget to change viewport back
	public void endShadowMap() {
		IS_SHADOW_MAPPING = false;
		Gdx.gl30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public int getDepthTexture() {
		return this.depthTexture;
	}

	public static final String vertexShader = "#version 330 core\r\n" + //
			"\r\n" + //
			"// Input vertex data, different for all executions of this shader.\r\n" + //
			"layout(location = 0) in vec3 vertexPosition_modelspace;\r\n" + //
			"\r\n" + //
			"// Values that stay constant for the whole mesh.\r\n" + //
			"uniform mat4 depthMVP;\r\n" + //
			"\r\n" + //
			"void main(){\r\n" + //
			" gl_Position =  depthMVP * vec4(vertexPosition_modelspace,1);\r\n" + //
			"}";

	public static final String fragmentShader = "#version 330 core\r\n" + //
			"\r\n" + //
			"// Ouput data\r\n" + //
			"layout(location = 0) out float fragmentdepth;\r\n" + //
			"\r\n" + //
			"void main(){\r\n" + //
			"    // Not really needed, OpenGL does it anyway\r\n" + //
			"    fragmentdepth = gl_FragCoord.z;\r\n" + //
			"}";
	private int depthTexture;
	private int framebufferName;

}
