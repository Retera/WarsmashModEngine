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
	public static final String transforms = "#ifdef SKIN\r\n" + //
			"attribute vec4 a_bones;\r\n" + //
			"attribute vec4 a_weights;\r\n" + //
			"void transformSkin(inout vec3 position, inout vec3 normal, inout vec3 tangent, inout vec3 binormal) {\r\n"
			+ //
			"  mat4 bone = mat4(0);\r\n" + //
			"  bone += fetchMatrix(a_bones[0], 0.0) * a_weights[0];\r\n" + //
			"  bone += fetchMatrix(a_bones[1], 0.0) * a_weights[1];\r\n" + //
			"  bone += fetchMatrix(a_bones[2], 0.0) * a_weights[2];\r\n" + //
			"  bone += fetchMatrix(a_bones[3], 0.0) * a_weights[3];\r\n" + //
			"  mat3 rotation = mat3(bone);\r\n" + //
			"  position = vec3(bone * vec4(position, 1.0));\r\n" + //
			"  normal = rotation * normal;\r\n" + //
			"  tangent = rotation * tangent;\r\n" + //
			"  binormal = rotation * binormal;\r\n" + //
			"}\r\n" + //
			"#else\r\n" + //
			"attribute vec4 a_bones;\r\n" + //
			"#ifdef EXTENDED_BONES\r\n" + //
			"attribute vec4 a_extendedBones;\r\n" + //
			"#endif\r\n" + //
			"attribute float a_boneNumber;\r\n" + //
			"mat4 getVertexGroupMatrix() {\r\n" + //
			"  mat4 bone;\r\n" + //
			"  // For the broken models out there, since the game supports this.\r\n" + //
			"  if (a_boneNumber > 0.0) {\r\n" + //
			"    for (int i = 0; i < 4; i++) {\r\n" + //
			"      if (a_bones[i] > 0.0) {\r\n" + //
			"        bone += fetchMatrix(a_bones[i] - 1.0, 0.0);\r\n" + //
			"      }\r\n" + //
			"    }\r\n" + //
			"    #ifdef EXTENDED_BONES\r\n" + //
			"      for (int i = 0; i < 4; i++) {\r\n" + //
			"        if (a_extendedBones[i] > 0.0) {\r\n" + //
			"          bone += fetchMatrix(a_extendedBones[i] - 1.0, 0.0);\r\n" + //
			"        }\r\n" + //
			"      }\r\n" + //
			"    #endif\r\n" + //
			"  }\r\n" + //
			"  return bone / a_boneNumber;\r\n" + //
			"}\r\n" + //
			"void transformVertexGroups(inout vec3 position, inout vec3 normal) {\r\n" + //
			"  mat4 bone = getVertexGroupMatrix();\r\n" + //
			"  mat3 rotation = mat3(bone);\r\n" + //
			"  position = vec3(bone * vec4(position, 1.0));\r\n" + //
			"  normal = normalize(rotation * normal);\r\n" + //
			"}\r\n" + //
			"void transformVertexGroupsHD(inout vec3 position, inout vec3 normal, inout vec3 tangent, inout vec3 binormal) {\r\n"
			+ //
			"  mat4 bone = getVertexGroupMatrix();\r\n" + //
			"  mat3 rotation = mat3(bone);\r\n" + //
			"  position = vec3(bone * vec4(position, 1.0));\r\n" + //
			"  normal = normalize(rotation * normal);\r\n" + //
			"  tangent = normalize(rotation * tangent);\r\n" + //
			"  binormal = normalize(rotation * binormal);\r\n" + //
			"}\r\n" + //
			"#endif";

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
			final String lightTextureHeight, final String lightCount, final String lightOmitOffset,
			final boolean terrain) {
		return "        vec3 lightFactor = vec3(0.0,0.0,0.0);\r\n" + //
				"        for(float lightIndex = 0.5" + lightOmitOffset + "; lightIndex < " + lightCount
				+ "; lightIndex += 1.0) {\r\n" + //
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
				"        }\r\n";
	}

	public static String fogSystem(final boolean supportsUnfoggedMaterial,
			final String fogIncreasesPixelColorBranchCondition) {
		String firstLine;
		if (supportsUnfoggedMaterial) {
			firstLine = "      if(!u_unfogged && u_fogParams.x > 0.5) {\r\n";
		}
		else {
			firstLine = "      if(u_fogParams.x > 0.5) {\r\n";
		}
		String rgbModification;
		if (fogIncreasesPixelColorBranchCondition != null) {
			// additive particles and geoset materials would become giant white squares
			// if we allowed fog to increase their pixel color like how it does to solid
			// objects, so for those cases we introduce a branch
			rgbModification = //
					"        if (" + fogIncreasesPixelColorBranchCondition + ") {\r\n" + //
							"          color.rgb = color.rgb * (1.0 - fogAmount) + u_fogColor.rgb * fogAmount;\r\n" + //
							"        } else {\r\n" + //
							"          color.rgb = color.rgb * (vec3(1.0 - fogAmount) + u_fogColor.rgb * fogAmount);\r\n"
							+ //
							"        }\r\n" //
			;
		}
		else {
			// if no additive support, we do the simple equation for adding to the pixel
			// color proportionally
			rgbModification = //
					"        color.rgb = color.rgb * (1.0 - fogAmount) + u_fogColor.rgb * fogAmount;\r\n" //
			;
		}
		return firstLine + //
				"        float fogAmount = clamp(((1.0 / gl_FragCoord.w) - u_fogParams.y) / (u_fogParams.z - u_fogParams.y), 0.0, 1.0);\r\n"
				+ //
				rgbModification + //
				"      }\r\n";
	}
}
