package com.etheller.warsmash.desktop;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.etheller.warsmash.WarsmashGdxMapGame;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.DynamicShadowExtension;
import com.etheller.warsmash.viewer5.gl.Extensions;

public class DesktopLauncher {
	public static void main(final String[] arg) {
		Extensions.angleInstancedArrays = new ANGLEInstancedArrays() {
			@Override
			public void glVertexAttribDivisorANGLE(final int index, final int divisor) {
				GL33.glVertexAttribDivisor(index, divisor);
			}

			@Override
			public void glDrawElementsInstancedANGLE(final int mode, final int count, final int type,
					final int indicesOffset, final int instanceCount) {
				GL31.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
			}

			@Override
			public void glDrawArraysInstancedANGLE(final int mode, final int first, final int count,
					final int instanceCount) {
				GL31.glDrawArraysInstanced(mode, first, count, instanceCount);
			}
		};
		Extensions.dynamicShadowExtension = new DynamicShadowExtension() {
			@Override
			public void glFramebufferTexture(final int target, final int attachment, final int texture,
					final int level) {
				GL32.glFramebufferTexture(target, attachment, texture, level);
			}

			@Override
			public void glDrawBuffer(final int mode) {
				GL11.glDrawBuffer(mode);
			}
		};
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = true;
		config.gles30ContextMajorVersion = 3;
		config.gles30ContextMinorVersion = 3;
		config.samples = 16;
//		config.fullscreen = false;
//		final DisplayMode desktopDisplayMode = LwjglApplicationConfiguration.getDesktopDisplayMode();
//		config.width = desktopDisplayMode.width;
//		config.height = desktopDisplayMode.height;
		new LwjglApplication(new WarsmashGdxMapGame(), config);
	}
}
