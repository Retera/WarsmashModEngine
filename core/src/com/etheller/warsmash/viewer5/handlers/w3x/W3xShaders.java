package com.etheller.warsmash.viewer5.handlers.w3x;

import com.etheller.warsmash.viewer5.Shaders;

public class W3xShaders {
	public static final class UberSplat {
		private UberSplat() {
		}

		public static final String vert() {
			return "\r\n" + //
					"\r\n" + //
					"    uniform mat4 u_mvp;\r\n" + //
					"    uniform sampler2D u_heightMap;\r\n" + //
					"    uniform vec2 u_pixel;\r\n" + //
					"    uniform vec2 u_size;\r\n" + //
					"    uniform vec2 u_shadowPixel;\r\n" + //
					"    uniform vec2 u_centerOffset;\r\n" + //
					"    uniform sampler2D u_lightTexture;\r\n" + //
					"    uniform float u_lightCount;\r\n" + //
					"    uniform float u_lightTextureHeight;\r\n" + //
					"    attribute vec3 a_position;\r\n" + //
					"    attribute vec2 a_uv;\r\n" + //
					"    attribute float a_absoluteHeight;\r\n" + //
					"    varying vec2 v_uv;\r\n" + //
					"    varying vec2 v_suv;\r\n" + //
					"    varying vec3 v_normal;\r\n" + //
					"    varying float a_positionHeight;\r\n" + //
					"    varying vec3 shadeColor;\r\n" + //
					"    const float normalDist = 0.25;\r\n" + //
					"    void main() {\r\n" + //
					"      vec2 halfPixel = u_pixel * 0.5;\r\n" + //
					"      vec2 base = (a_position.xy - u_centerOffset) / 128.0;\r\n" + //
					"      float height;\r\n" + //
					"      float hL;\r\n" + //
					"      float hR;\r\n" + //
					"      float hD;\r\n" + //
					"      float hU;\r\n" + //
					"      if (a_absoluteHeight < -256.0) {\r\n" + //
					"        height = texture2D(u_heightMap, base * u_pixel + halfPixel).r * 128.0;\r\n" + //
					"        hL = texture2D(u_heightMap, vec2(base - vec2(normalDist, 0.0)) * u_pixel + halfPixel).r;\r\n"
					+ //
					"        hR = texture2D(u_heightMap, vec2(base + vec2(normalDist, 0.0)) * u_pixel + halfPixel).r;\r\n"
					+ //
					"        hD = texture2D(u_heightMap, vec2(base - vec2(0.0, normalDist)) * u_pixel + halfPixel).r;\r\n"
					+ //
					"        hU = texture2D(u_heightMap, vec2(base + vec2(0.0, normalDist)) * u_pixel + halfPixel).r;\r\n"
					+ //
					"      } else {\r\n" + //
					"        height = a_absoluteHeight;\r\n" + //
					"        hL = a_absoluteHeight;\r\n" + //
					"        hR = a_absoluteHeight;\r\n" + //
					"        hD = a_absoluteHeight;\r\n" + //
					"        hU = a_absoluteHeight;\r\n" + //
					"      }\r\n" + //
					"      v_normal = normalize(vec3(hL - hR, hD - hU, normalDist * 2.0));\r\n" + //
					"      v_uv = a_uv;\r\n" + //
					"      v_suv = base / u_size;\r\n" + //
					"      vec3 myposition = vec3(a_position.xy, height + a_position.z);\r\n" + //
					"      gl_Position = u_mvp * vec4(myposition.xyz, 1.0);\r\n" + //
					"      a_positionHeight = a_position.z;\r\n" + //
					Shaders.lightSystem("v_normal", "myposition", "u_lightTexture", "u_lightTextureHeight",
							"u_lightCount", true)
					+ "\r\n" + //
					"        shadeColor = clamp(lightFactor, 0.0, 1.0);\r\n" + //
					"    }\r\n" + //
					" ";
		}

		public static final String frag = "\r\n" + //
				"    uniform sampler2D u_texture;\r\n" + //
				"    uniform sampler2D u_shadowMap;\r\n" + //
				"    uniform vec4 u_color;\r\n" + //
				"    uniform bool u_show_lighting;\r\n" + //
				"    varying vec2 v_uv;\r\n" + //
				"    varying vec2 v_suv;\r\n" + //
				"    varying vec3 v_normal;\r\n" + //
				"    varying float a_positionHeight;\r\n" + //
				"    varying vec3 shadeColor;\r\n" + //
				// " const vec3 lightDirection = normalize(vec3(-0.3, -0.3, 0.25));\r\n" + //
				"    void main() {\r\n" + //
				"      if (any(bvec4(lessThan(v_uv, vec2(0.0)), greaterThan(v_uv, vec2(1.0))))) {\r\n" + //
				"        discard;\r\n" + //
				"      }\r\n" + //
				"      vec4 color = texture2D(u_texture, clamp(v_uv, 0.0, 1.0)).rgba * u_color;\r\n" + //
				"      float shadow = texture2D(u_shadowMap, v_suv).r;\r\n" + //
				// " color.xyz *= clamp(dot(v_normal, lightDirection) + 0.45, 0.0, 1.0);\r\n" +
				// //
				"      if (a_positionHeight <= 4.0) {;\r\n" + //
				"        color.xyz *= 1.0 - shadow;\r\n" + //
				"      };\r\n" + //
				"      if (u_show_lighting) {;\r\n" + //
				"        color.xyz *= shadeColor;\r\n" + //
				"      };\r\n" + //
				"      gl_FragColor = color;\r\n" + //
				"    }\r\n" + //
				"  ";
	}
}
