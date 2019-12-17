package com.etheller.warsmash.viewer5;

public class Shaders {
	public static final String boneTexture = ""//
			+ "    uniform sampler2D u_boneMap;\r\n" + //
			"    uniform float u_vectorSize;\r\n" + //
			"    uniform float u_rowSize;\r\n" + //
			"    mat4 fetchMatrix(float column, float row) {\r\n" + //
			"      column *= u_vectorSize * 4.0;\r\n" + //
			"      row *= u_rowSize;\r\n" + //
			"      // Add in half texel to sample in the middle of the texel.\r\n" + //
			"      // Otherwise, since the sample is directly on the boundry, small floating point errors can cause the sample to get the wrong pixel.\r\n"
			+ //
			"      // This is mostly noticable with NPOT textures, which the bone maps are.\r\n" + //
			"      column += 0.5 * u_vectorSize;\r\n" + //
			"      row += 0.5 * u_rowSize;\r\n" + //
			"      return mat4(texture2D(u_boneMap, vec2(column, row)),\r\n" + //
			"                  texture2D(u_boneMap, vec2(column + u_vectorSize, row)),\r\n" + //
			"                  texture2D(u_boneMap, vec2(column + u_vectorSize * 2.0, row)),\r\n" + //
			"                  texture2D(u_boneMap, vec2(column + u_vectorSize * 3.0, row)));\r\n" + //
			"    }";

	public static final String decodeFloat = "\r\n" + //
			"    vec2 decodeFloat2(float f) {\r\n" + //
			"      vec2 v;\r\n" + //
			"      v[1] = floor(f / 256.0);\r\n" + //
			"      v[0] = floor(f - v[1] * 256.0);\r\n" + //
			"      return v;\r\n" + //
			"    }\r\n" + //
			"    vec3 decodeFloat3(float f) {\r\n" + //
			"      vec3 v;\r\n" + //
			"      v[2] = floor(f / 65536.0);\r\n" + //
			"      v[1] = floor((f - v[2] * 65536.0) / 256.0);\r\n" + //
			"      v[0] = floor(f - v[2] * 65536.0 - v[1] * 256.0);\r\n" + //
			"      return v;\r\n" + //
			"    }\r\n" + //
			"    vec4 decodeFloat4(float v) {\r\n" + //
			"      vec4 enc = vec4(1.0, 255.0, 65025.0, 16581375.0) * v;\r\n" + //
			"      enc = fract(enc);\r\n" + //
			"      enc -= enc.yzww * vec4(1.0 / 255.0, 1.0 / 255.0, 1.0 / 255.0, 0.0);\r\n" + //
			"      return enc;\r\n" + //
			"    }";

	public static final String quatTransform = "\r\n" + //
			"    // A 2D quaternion*vector.\r\n" + //
			"    // q is the zw components of the original quaternion.\r\n" + //
			"    vec2 quat_transform(vec2 q, vec2 v) {\r\n" + //
			"      vec2 uv = vec2(-q.x * v.y, q.x * v.x);\r\n" + //
			"      vec2 uuv = vec2(-q.x * uv.y, q.x * uv.x);\r\n" + //
			"      return v + 2.0 * (uv * q.y + uuv);\r\n" + //
			"    }\r\n" + //
			"    // A 2D quaternion*vector.\r\n" + //
			"    // q is the zw components of the original quaternion.\r\n" + //
			"    vec3 quat_transform(vec2 q, vec3 v) {\r\n" + //
			"      return vec3(quat_transform(q, v.xy), v.z);\r\n" + //
			"    }\r\n" + //
			"  `,";
}
