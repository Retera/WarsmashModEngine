package com.etheller.warsmash.viewer5;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler.ShaderEnvironmentType;

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
			"  ";

	public static String lightSystem(final String normalName, final String positionName, final String lightTexture,
			final String lightTextureHeight, final String lightCount, final boolean terrain) {
		new RuntimeException("lightSystem").printStackTrace();
		return "        vec3 lightFactor = vec3(0.0,0.0,0.0);\r\n" + //
				"        for(float lightIndex = 0.5; lightIndex < " + lightCount + "; lightIndex += 1.0) {\r\n" + //
				"          float rowPos = (lightIndex) / " + lightTextureHeight + ";\r\n" + //
				"          vec4 lightPosition = texture2D(" + lightTexture + ", vec2(0.125, rowPos));\r\n" + //
				"          vec3 lightExtra = texture2D(" + lightTexture + ", vec2(0.375, rowPos)).xyz;\r\n" + //
				"          vec4 lightColor = texture2D(" + lightTexture + ", vec2(0.625, rowPos));\r\n" + //
				"          vec4 lightAmbColor = texture2D(" + lightTexture + ", vec2(0.875, rowPos));\r\n" + //
				"          if(lightExtra.x > 1.5) {\r\n" + //
				"            // Ambient light;\r\n" + //
				"            float dist = length(" + positionName + " - vec3(lightPosition." + (terrain ? "xyw" : "xyz")
				+ "));\r\n" + //
				"            float attenuationStart = lightExtra.y;\r\n" + //
				"            float attenuationEnd = lightExtra.z;\r\n" + //
				"            if( dist <= attenuationEnd ) {\r\n" + //
				"              float attenuationDist = clamp((dist-attenuationStart), 0.001, (attenuationEnd-attenuationStart));\r\n"
				+ //
				"              float attenuationFactor = 1.0/(attenuationDist);\r\n" + //
				"              lightFactor += attenuationFactor * lightAmbColor.a * lightAmbColor.rgb;\r\n" + //
				"              \r\n" + //
				"            }\r\n" + //
				"          } else if(lightExtra.x > 0.5) {\r\n" + //
				"            // Directional (sun) light;\r\n" + //
				"            vec3 lightDirection = vec3(lightPosition.xyz);\r\n" + //
				"            vec3 lightFactorContribution = lightColor.a * lightColor.rgb * clamp(dot(" + normalName
				+ ", lightDirection), 0.0, 1.0);\r\n" + //
				"            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {\r\n"
				+ //
				"              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);\r\n" + //
				"            }\r\n" + //
				"            lightFactor += lightFactorContribution + lightAmbColor.a * lightAmbColor.rgb;\r\n" + //
				"          } else {\r\n" + //
				"            // Omnidirectional light;\r\n" + //
				"            vec3 deltaBtwn = " + positionName + " - lightPosition.xyz;\r\n" + //
				"            float dist = length(" + positionName + " - vec3(lightPosition." + (terrain ? "xyz" : "xyz")
				+ ")) / 64.0 + 1.0;\r\n" + //
				"            vec3 lightDirection = normalize(-deltaBtwn);\r\n" + //
				"            vec3 lightFactorContribution = (lightColor.a/(pow(dist, 2.0))) * lightColor.rgb * clamp(dot("
				+ normalName + ", lightDirection), 0.0, 1.0);\r\n" + //
				"            if(lightFactorContribution.r > 1.0 || lightFactorContribution.g > 1.0 || lightFactorContribution.b > 1.0) {\r\n"
				+ //
				"              lightFactorContribution = clamp(lightFactorContribution, 0.0, 1.0);\r\n" + //
				"            }\r\n" + //
				"            lightFactor += lightFactorContribution + (lightAmbColor.a/(pow(dist, 2.0))) * lightAmbColor.rgb;\r\n"
				+ //
				"          }\r\n" + //
				"        }\r\n" + //
				(MdxHandler.CURRENT_SHADER_TYPE == ShaderEnvironmentType.MENU
						? "        vec4 sRGB = vec4(lightFactor, 1.0);" + //
								"        bvec4 cutoff = lessThan(sRGB, vec4(0.04045));" + //
								"        vec4 higher = pow((sRGB + vec4(0.055))/vec4(1.055), vec4(2.4));" + //
								"        vec4 lower = sRGB/vec4(12.92);" + //
								"" + //
								"        lightFactor = (higher * (vec4(1.0) - vec4(cutoff)) + lower * vec4(cutoff)).xyz;"
						: "");
	}
}
