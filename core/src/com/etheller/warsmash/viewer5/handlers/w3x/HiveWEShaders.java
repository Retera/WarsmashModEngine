package com.etheller.warsmash.viewer5.handlers.w3x;

public class HiveWEShaders {
	public static final class Cliffs {
		private Cliffs() {
		}

		public static final String vert = "#version 450 core\r\n" + //
				"\r\n" + //
				"layout (location = 0) in vec3 vPosition;\r\n" + //
				"layout (location = 1) in vec2 vUV;\r\n" + //
				"layout (location = 2) in vec3 vNormal;\r\n" + //
				"layout (location = 3) in vec4 vOffset;\r\n" + //
				"\r\n" + //
				"layout (location = 0) uniform mat4 MVP;\r\n" + //
				"\r\n" + //
				"layout (binding = 1) uniform sampler2D height_texture;\r\n" + //
				"\r\n" + //
				"layout (location = 0) out vec3 UV;\r\n" + //
				"layout (location = 1) out vec3 Normal;\r\n" + //
				"layout (location = 2) out vec2 pathing_map_uv;\r\n" + //
				"\r\n" + //
				"void main() {\r\n" + //
				"	pathing_map_uv = (vec2(vPosition.x + 128, vPosition.y) / 128 + vOffset.xy) * 4;\r\n" + //
				" \r\n" + //
				"	ivec2 size = textureSize(height_texture, 0);\r\n" + //
				"	float value = texture(height_texture, (vOffset.xy + vec2(vPosition.x + 192, vPosition.y + 64) / 128) / vec2(size)).r;\r\n"
				+ //
				"\r\n" + //
				"	gl_Position = MVP * vec4(vPosition + vec3(vOffset.xy + vec2(1, 0), vOffset.z + value) * 128, 1);\r\n"
				+ //
				"	UV = vec3(vUV, vOffset.a);\r\n" + //
				"\r\n" + //
				"	ivec2 height_pos = ivec2(vOffset.xy + vec2(vPosition.x + 128, vPosition.y) / 128);\r\n" + //
				"	ivec3 off = ivec3(1, 1, 0);\r\n" + //
				"	float hL = texelFetch(height_texture, height_pos - off.xz, 0).r;\r\n" + //
				"	float hR = texelFetch(height_texture, height_pos + off.xz, 0).r;\r\n" + //
				"	float hD = texelFetch(height_texture, height_pos - off.zy, 0).r;\r\n" + //
				"	float hU = texelFetch(height_texture, height_pos + off.zy, 0).r;\r\n" + //
				"	vec3 terrain_normal = normalize(vec3(hL - hR, hD - hU, 2.0));\r\n" + //
				"\r\n" + //
				"	Normal = terrain_normal;\r\n" + //
				"}";
	}
}
