package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.Shaders;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler.ShaderEnvironmentType;

public class MdxShaders {
	public static final String vsHd = "#version 120\r\n" + Shaders.boneTexture + "\r\n" + //
			"    uniform mat4 u_VP;\r\n" + //
			"    uniform mat4 u_MV;\r\n" + //
			"    uniform vec3 u_eyePos;\r\n" + //
			"    uniform sampler2D u_lightTexture;\r\n" + //
			"    uniform float u_lightTextureHeight;\r\n" + //
			"    uniform float u_layerAlpha;\r\n" + //
			"    uniform bool u_hasBones;\r\n" + //
			"    " + //
			"    attribute vec3 a_position;\r\n" + //
			"    attribute vec3 a_normal;\r\n" + //
			"    attribute vec2 a_uv;\r\n" + //
			"    attribute vec4 a_tangent;\r\n" + //
			// TODO ONLY_TANGENTS
			"    \r\n" + //
			"    \r\n" + //
			"#define SKIN\r\n" + // TODO make this conditional
			Shaders.transforms + //
			"    \r\n" + //
			"    \r\n" + //
			"vec3 TBN(vec3 vector, vec3 tangent, vec3 binormal, vec3 normal) {\r\n" + //
			"  return vec3(dot(vector, tangent), dot(vector, binormal), dot(vector, normal));\r\n" + //
			"}\r\n" + //
			"    \r\n" + //
			"    \r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    varying float v_layerAlpha;\r\n" + //
			"    varying vec4 v_lightDir;\r\n" + //
			"    varying vec4 v_lightDir2;\r\n" + //
			"    varying vec4 v_lightDir3;\r\n" + //
			"    varying vec4 v_lightDir4;\r\n" + //
			"    varying vec4 v_lightDir5;\r\n" + //
			"    varying vec4 v_lightDir6;\r\n" + //
			"    varying vec4 v_lightDir7;\r\n" + //
			"    varying vec4 v_lightDir8;\r\n" + //
			"    varying vec3 v_eyeVec;\r\n" + //
			"    varying vec3 v_normal;\r\n" + //
			"    \r\n" + //
			"    \r\n" + //
			"    void main() {\r\n" + //
			"      vec3 position = a_position;\r\n" + //
			"      vec3 normal = a_normal;\r\n" + //
			"      vec3 tangent = a_tangent.xyz;\r\n" + //
			"      \r\n" + //
			// Re-orthogonalize the tangent in case it wasnt normalized.
			// See "One last thing" at
			// https://learnopengl.com/Advanced-Lighting/Normal-Mapping
			"      \r\n" + //
			"      tangent = normalize(tangent - dot(tangent, normal) * normal);\r\n" + //
			"      \r\n" + //
			"      vec3 binormal = cross(normal, tangent) * a_tangent.w;\r\n" + //
			"      \r\n" + //
			"      if (u_hasBones) {\r\n" + //
			"        #ifdef SKIN\r\n" + //
			"          transformSkin(position, normal, tangent, binormal);\r\n" + //
			"        #else\r\n" + //
			"          transformVertexGroupsHD(position, normal, tangent, binormal);\r\n" + //
			"        #endif\r\n" + //
			"      }\r\n" + //
			"      \r\n" + //
			"      vec3 position_mv = vec3(u_MV * vec4(position, 1));\r\n" + //
			"      \r\n" + //
			"      mat3 mv = mat3(u_MV);\r\n" + //
			"      vec3 t = normalize(mv * tangent);\r\n" + //
			"      vec3 b = normalize(mv * binormal);\r\n" + //
			"      vec3 n = normalize(mv * normal);\r\n" + //
			"      \r\n" + //
			"      v_eyeVec = normalize(TBN(normalize(mv * u_eyePos - position_mv), t, b, n));\r\n" + //
			"      \r\n" + //
			// TODO fix giant hack on lighting
			"      float rowPos = (0.5) / u_lightTextureHeight;\r\n" + //
			"      vec4 lightPosition = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n" + //
			"      vec4 lightExtra = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"      vec3 u_lightPos = mv * lightPosition.xyz;\r\n" + //
			"      vec3 lightDir;\r\n" + //
			"      if(lightExtra.x > 0.5) {\r\n" + //
			"          // Sunlight ('directional')\r\n" + //
			"      	   lightDir = normalize(u_lightPos);\r\n" + //
			"          v_lightDir = vec4(normalize(TBN(lightDir, t, b, n)), 1.0);\r\n" + //
			"      } else {\r\n" + //
			"          // Point light ('omnidirectional')\r\n" + //
			"          vec3 delta = u_lightPos - position_mv;\r\n" + //
			"          lightDir = normalize(delta);\r\n" + //
			"            float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"          v_lightDir = vec4(normalize(TBN(lightDir, t, b, n)), 1.0/pow(dist, 2.0));\r\n" + //
			"      }\r\n" + //
			"      \r\n" + //
			"      if( u_lightTextureHeight > 1.5 ) {\r\n" + //
			"          float rowPos = (1.5) / u_lightTextureHeight;\r\n" + //
			"          vec4 lightPosition2 = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n" + //
			"          vec4 lightExtra2 = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"          vec3 u_lightPos2 = mv * lightPosition2.xyz;\r\n" + //

			"          vec3 lightDir2;\r\n" + //
			"          if(lightExtra2.x > 0.5) {\r\n" + //
			"              // Sunlight ('directional')\r\n" + //
			"          	   lightDir2 = normalize(u_lightPos2);\r\n" + //
			"              v_lightDir2 = vec4(normalize(TBN(lightDir2, t, b, n)), 1.0);\r\n" + //
			"          } else {\r\n" + //
			"              // Point light ('omnidirectional')\r\n" + //
			"              vec3 delta = u_lightPos2 - position_mv;\r\n" + //
			"              lightDir2 = normalize(delta);\r\n" + //
			"                float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"              v_lightDir2 = vec4(normalize(TBN(lightDir2, t, b, n)), 1.0/pow(dist, 2.0));\r\n" + //
			"          }\r\n" + //
			"          if( u_lightTextureHeight > 2.5 ) {\r\n" + //
			"              float rowPos = (2.5) / u_lightTextureHeight;\r\n" + //
			"              vec4 lightPosition3 = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n" + //
			"              vec4 lightExtra3 = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"              vec3 u_lightPos3 = mv * lightPosition3.xyz;\r\n" + //
			"              vec3 lightDir3;\r\n" + //
			"              if(lightExtra3.x > 0.5) {\r\n" + //
			"                  // Sunlight ('directional')\r\n" + //
			"              	   lightDir3 = normalize(u_lightPos3);\r\n" + //
			"                  v_lightDir3 = vec4(normalize(TBN(lightDir3, t, b, n)), 1.0);\r\n" + //
			"              } else {\r\n" + //
			"                  // Point light ('omnidirectional')\r\n" + //
			"                  vec3 delta = u_lightPos3 - position_mv;\r\n" + //
			"                  lightDir3 = normalize(delta);\r\n" + //
			"                    float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"                  v_lightDir3 = vec4(normalize(TBN(lightDir3, t, b, n)), 1.0/pow(dist, 2.0));\r\n" + //
			"              }\r\n" + //
			"              if( u_lightTextureHeight > 3.5 ) {\r\n" + //
			"                  float rowPos = (3.5) / u_lightTextureHeight;\r\n" + //
			"                  vec4 lightPosition4 = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n" + //
			"                  vec4 lightExtra4 = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"                  vec3 u_lightPos4 = mv * lightPosition4.xyz;\r\n" + //
			"                  vec3 lightDir4;\r\n" + //
			"                  if(lightExtra4.x > 0.5) {\r\n" + //
			"                      // Sunlight ('directional')\r\n" + //
			"                  	   lightDir4 = normalize(u_lightPos4);\r\n" + //
			"                      v_lightDir4 = vec4(normalize(TBN(lightDir4, t, b, n)), 1.0);\r\n" + //
			"                  } else {\r\n" + //
			"                      // Point light ('omnidirectional')\r\n" + //
			"                      vec3 delta = u_lightPos4 - position_mv;\r\n" + //
			"                      lightDir4 = normalize(delta);\r\n" + //
			"                        float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"                      v_lightDir4 = vec4(normalize(TBN(lightDir4, t, b, n)), 1.0/pow(dist, 2.0));\r\n" + //
			"                  }\r\n" + //
			"                  if( u_lightTextureHeight > 4.5 ) {\r\n" + //
			"                      float rowPos = (4.5) / u_lightTextureHeight;\r\n" + //
			"                      vec4 lightPosition5 = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n" + //
			"                      vec4 lightExtra5 = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"                      vec3 u_lightPos5 = mv * lightPosition5.xyz;\r\n" + //
			"                      vec3 lightDir5;\r\n" + //
			"                      if(lightExtra5.x > 0.5) {\r\n" + //
			"                          // Sunlight ('directional')\r\n" + //
			"                      	   lightDir5 = normalize(u_lightPos5);\r\n" + //
			"                          v_lightDir5 = vec4(normalize(TBN(lightDir5, t, b, n)), 1.0);\r\n" + //
			"                      } else {\r\n" + //
			"                          // Point light ('omnidirectional')\r\n" + //
			"                          vec3 delta = u_lightPos5 - position_mv;\r\n" + //
			"                          lightDir5 = normalize(delta);\r\n" + //
			"                            float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"                          v_lightDir5 = vec4(normalize(TBN(lightDir5, t, b, n)), 1.0/pow(dist, 2.0));\r\n"
			+ //
			"                      }\r\n" + //
			"                      if( u_lightTextureHeight > 5.5 ) {\r\n" + //
			"                          float rowPos = (5.5) / u_lightTextureHeight;\r\n" + //
			"                          vec4 lightPosition6 = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n" + //
			"                          vec4 lightExtra6 = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"                          vec3 u_lightPos6 = mv * lightPosition6.xyz;\r\n" + //
			"                          vec3 lightDir6;\r\n" + //
			"                          if(lightExtra6.x > 0.5) {\r\n" + //
			"                              // Sunlight ('directional')\r\n" + //
			"                          	   lightDir6 = normalize(u_lightPos6);\r\n" + //
			"                              v_lightDir6 = vec4(normalize(TBN(lightDir6, t, b, n)), 1.0);\r\n" + //
			"                          } else {\r\n" + //
			"                              // Point light ('omnidirectional')\r\n" + //
			"                              vec3 delta = u_lightPos6 - position_mv;\r\n" + //
			"                              lightDir6 = normalize(delta);\r\n" + //
			"                                float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"                              v_lightDir6 = vec4(normalize(TBN(lightDir6, t, b, n)), 1.0/pow(dist, 2.0));\r\n"
			+ //
			"                          }\r\n" + //
			"                          if( u_lightTextureHeight > 6.5 ) {\r\n" + //
			"                              float rowPos = (6.5) / u_lightTextureHeight;\r\n" + //
			"                              vec4 lightPosition7 = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n" + //
			"                              vec4 lightExtra7 = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"                              vec3 u_lightPos7 = mv * lightPosition7.xyz;\r\n" + //
			"                              vec3 lightDir7;\r\n" + //
			"                              if(lightExtra7.x > 0.5) {\r\n" + //
			"                                  // Sunlight ('directional')\r\n" + //
			"                              	   lightDir7 = normalize(u_lightPos7);\r\n" + //
			"                                  v_lightDir7 = vec4(normalize(TBN(lightDir7, t, b, n)), 1.0);\r\n" + //
			"                              } else {\r\n" + //
			"                                  // Point light ('omnidirectional')\r\n" + //
			"                                  vec3 delta = u_lightPos7 - position_mv;\r\n" + //
			"                                  lightDir7 = normalize(delta);\r\n" + //
			"                                    float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"                                  v_lightDir7 = vec4(normalize(TBN(lightDir7, t, b, n)), 1.0/pow(dist, 2.0));\r\n"
			+ //
			"                              }\r\n" + //
			"                              if( u_lightTextureHeight > 7.5 ) {\r\n" + //
			"                                  float rowPos = (7.5) / u_lightTextureHeight;\r\n" + //
			"                                  vec4 lightPosition8 = texture2D(u_lightTexture, vec2(0.125, rowPos));\r\n"
			+ //
			"                                  vec4 lightExtra8 = texture2D(u_lightTexture, vec2(0.375, rowPos));\r\n" + //
			"                                  vec3 u_lightPos8 = mv * lightPosition8.xyz;\r\n" + //
			"                                  vec3 lightDir8;\r\n" + //
			"                                  if(lightExtra8.x > 0.5) {\r\n" + //
			"                                      // Sunlight ('directional')\r\n" + //
			"                                  	   lightDir8 = normalize(u_lightPos8);\r\n" + //
			"                                      v_lightDir8 = vec4(normalize(TBN(lightDir8, t, b, n)), 1.0);\r\n" + //
			"                                  } else {\r\n" + //
			"                                      // Point light ('omnidirectional')\r\n" + //
			"                                      vec3 delta = u_lightPos8 - position_mv;\r\n" + //
			"                                      lightDir8 = normalize(delta);\r\n" + //
			"                                        float dist = length(delta) / 64.0 + 1.0;\r\n" + //
			"                                      v_lightDir8 = vec4(normalize(TBN(lightDir8, t, b, n)), 1.0/pow(dist, 2.0));\r\n"
			+ //
			"                                  }\r\n" + //
			"                              } else {\r\n" + //
			"                                  v_lightDir8 = vec4(0.0);\r\n" + //
			"                                  \r\n" + //
			"                              }\r\n" + //
			"                          } else {\r\n" + //
			"                              v_lightDir7 = vec4(0.0);\r\n" + //
			"                              \r\n" + //
			"                          }\r\n" + //
			"                      } else {\r\n" + //
			"                          v_lightDir6 = vec4(0.0);\r\n" + //
			"                          \r\n" + //
			"                      }\r\n" + //
			"                  } else {\r\n" + //
			"                      v_lightDir5 = vec4(0.0);\r\n" + //
			"                      \r\n" + //
			"                  }\r\n" + //
			"              } else {\r\n" + //
			"                  v_lightDir4 = vec4(0.0);\r\n" + //
			"                  \r\n" + //
			"              }\r\n" + //
			"          } else {\r\n" + //
			"              v_lightDir3 = vec4(0.0);\r\n" + //
			"              \r\n" + //
			"          }\r\n" + //
			"      } else {\r\n" + //
			"          v_lightDir2 = vec4(0.0);\r\n" + //
			"          \r\n" + //
			"      }\r\n" + //
			"      \r\n" + //
			"      v_uv = a_uv;\r\n" + //
			"      v_layerAlpha = u_layerAlpha;\r\n" + //
			"      \r\n" + //
			"      v_normal = normal;\r\n" + //
			"      // v_lightDirWorld = normalize(lightDir);\r\n" + //
			"      \r\n" + //
			// TODO ONLY_TANGENTS
			"      gl_Position = u_VP * vec4(position, 1.0);\r\n" + //
			"    }";

	public static final String fsHd() {
		return "#version 120\r\n" + //
				"\r\n" + //
				"\r\n" + //
				"\r\n" + //
				"uniform sampler2D u_lightTexture;\r\n" + //
				"uniform float u_lightTextureHeight;\r\n" + //
				"\r\n" + //
				"uniform sampler2D u_diffuseMap;\r\n" + //
				"uniform sampler2D u_normalsMap;\r\n" + //
				"uniform sampler2D u_ormMap;\r\n" + //
				"uniform sampler2D u_emissiveMap;\r\n" + //
				"uniform sampler2D u_teamColorMap;\r\n" + //
				"uniform sampler2D u_environmentMap;\r\n" + //
				"uniform float u_filterMode;\r\n" + //
				"// uniform sampler2D u_lutMap;\r\n" + //
				"// uniform sampler2D u_envDiffuseMap;\r\n" + //
				"// uniform sampler2D u_envSpecularMap;\r\n" + //
				"varying vec2 v_uv;\r\n" + //
				"varying float v_layerAlpha;\r\n" + //
				"varying vec4 v_lightDir;\r\n" + //
				"varying vec4 v_lightDir2;\r\n" + //
				"varying vec4 v_lightDir3;\r\n" + //
				"varying vec4 v_lightDir4;\r\n" + //
				"varying vec4 v_lightDir5;\r\n" + //
				"varying vec4 v_lightDir6;\r\n" + //
				"varying vec4 v_lightDir7;\r\n" + //
				"varying vec4 v_lightDir8;\r\n" + //
				"varying vec3 v_eyeVec;\r\n" + //
				"varying vec3 v_normal;\r\n" + //
				"// varying vec3 v_lightDirWorld;\r\n" + //
				"#if defined(ONLY_TANGENTS)\r\n" + //
				"varying vec3 v_tangent;\r\n" + //
				"#endif\r\n" + //
				"vec3 decodeNormal() {\r\n" + //
				"  vec2 xy = texture2D(u_normalsMap, v_uv).yx * 2.0 - 1.0;\r\n" + //
				"  \r\n" + //
				"  return vec3(xy, sqrt(1.0 - dot(xy, xy)));\r\n" + //
				"}\r\n" + //
				"const vec2 invAtan = vec2(0.1591, 0.3183);\r\n" + //
				"vec2 sampleEnvironmentMap(vec3 normal) {\r\n" + //
				"  vec2 uv = vec2(atan(normal.x, normal.y), -asin(normal.z));\r\n" + //
				"  uv *= invAtan;\r\n" + //
				"  uv += 0.5;\r\n" + //
				"  return uv;\r\n" + //
				"}\r\n" + //
				"vec4 getDiffuseColor() {\r\n" + //
				"  vec4 color = texture2D(u_diffuseMap, v_uv);\r\n" + //
				"  // 1bit Alpha\r\n" + //
				"  if (u_filterMode == 1.0 && color.a < 0.75) {\r\n" + //
				"    discard;\r\n" + //
				"  }\r\n" + //
				"  return color;\r\n" + //
				"}\r\n" + //
				"vec4 getOrmColor() {\r\n" + //
				"  return texture2D(u_ormMap, v_uv);\r\n" + //
				"}\r\n" + //
				"vec3 getEmissiveColor() {\r\n" + //
				"  return texture2D(u_emissiveMap, v_uv).rgb;\r\n" + //
				"}\r\n" + //
				"vec3 getTeamColor() {\r\n" + //
				"  return texture2D(u_teamColorMap, v_uv).rgb;\r\n" + //
				"}\r\n" + //
				"vec3 getEnvironmentMapColor() {\r\n" + //
				"  return texture2D(u_environmentMap, v_uv).rgb;\r\n" + //
				"}\r\n" + //
				"// const float PI = 3.14159265359;\r\n" + //
				"// const float RECIPROCAL_PI = 0.31830988618;\r\n" + //
				"// const float RECIPROCAL_PI2 = 0.15915494;\r\n" + //
				"// const float LN2 = 0.6931472;\r\n" + //
				"// const float ENV_LODS = 6.0;\r\n" + //
				"// vec4 SRGBtoLinear(vec4 srgb) {\r\n" + //
				"//     vec3 linOut = pow(srgb.xyz, vec3(2.2));\r\n" + //
				"//     return vec4(linOut, srgb.w);;\r\n" + //
				"// }\r\n" + //
				"// vec4 RGBMToLinear(in vec4 value) {\r\n" + //
				"//     float maxRange = 6.0;\r\n" + //
				"//     return vec4(value.xyz * value.w * maxRange, 1.0);\r\n" + //
				"// }\r\n" + //
				"// vec3 linearToSRGB(vec3 color) {\r\n" + //
				"//     return pow(color, vec3(1.0 / 2.2));\r\n" + //
				"// }\r\n" + //
				"// // vec3 getNormal() {\r\n" + //
				"// //     vec3 pos_dx = dFdx(vMPos.xyz);\r\n" + //
				"// //     vec3 pos_dy = dFdy(vMPos.xyz);\r\n" + //
				"// //     vec2 tex_dx = dFdx(vUv);\r\n" + //
				"// //     vec2 tex_dy = dFdy(vUv);\r\n" + //
				"// //     vec3 t = normalize(pos_dx * tex_dy.t - pos_dy * tex_dx.t);\r\n" + //
				"// //     vec3 b = normalize(-pos_dx * tex_dy.s + pos_dy * tex_dx.s);\r\n" + //
				"// //     mat3 tbn = mat3(t, b, normalize(vNormal));\r\n" + //
				"// //     vec3 n = texture2D(tNormal, vUv * uNormalUVScale).rgb * 2.0 - 1.0;\r\n" + //
				"// //     n.xy *= uNormalScale;\r\n" + //
				"// //     vec3 normal = normalize(tbn * n);\r\n" + //
				"// //     // Get world normal from view normal (normalMatrix * normal)\r\n" + //
				"// //     return normalize((vec4(normal, 0.0) * viewMatrix).xyz);\r\n" + //
				"// // }\r\n" + //
				"// vec3 specularReflection(vec3 specularEnvR0, vec3 specularEnvR90, float VdH) {\r\n" + //
				"//     return specularEnvR0 + (specularEnvR90 - specularEnvR0) * pow(clamp(1.0 - VdH, 0.0, 1.0), 5.0);\r\n"
				+ //
				"// }\r\n" + //
				"// float geometricOcclusion(float NdL, float NdV, float roughness) {\r\n" + //
				"//     float r = roughness;\r\n" + //
				"//     float attenuationL = 2.0 * NdL / (NdL + sqrt(r * r + (1.0 - r * r) * (NdL * NdL)));\r\n" + //
				"//     float attenuationV = 2.0 * NdV / (NdV + sqrt(r * r + (1.0 - r * r) * (NdV * NdV)));\r\n" + //
				"//     return attenuationL * attenuationV;\r\n" + //
				"// }\r\n" + //
				"// float microfacetDistribution(float roughness, float NdH) {\r\n" + //
				"//     float roughnessSq = roughness * roughness;\r\n" + //
				"//     float f = (NdH * roughnessSq - NdH) * NdH + 1.0;\r\n" + //
				"//     return roughnessSq / (PI * f * f);\r\n" + //
				"// }\r\n" + //
				"// vec2 cartesianToPolar(vec3 n) {\r\n" + //
				"//     vec2 uv;\r\n" + //
				"//     uv.x = atan(n.z, n.x) * RECIPROCAL_PI2 + 0.5;\r\n" + //
				"//     uv.y = asin(n.y) * RECIPROCAL_PI + 0.5;\r\n" + //
				"//     return uv;\r\n" + //
				"// }\r\n" + //
				"// void getIBLContribution(inout vec3 diffuse, inout vec3 specular, float NdV, float roughness, vec3 n, vec3 reflection, vec3 diffuseColor, vec3 specularColor) {\r\n"
				+ //
				"//   vec3 brdf = SRGBtoLinear(texture2D(u_lutMap, vec2(NdV, roughness))).rgb;\r\n" + //
				"//   vec3 diffuseLight = RGBMToLinear(texture2D(u_envDiffuseMap, sampleEnvironmentMap(n))).rgb;\r\n" + //
				"//   // Sample 2 levels and mix between to get smoother degradation\r\n" + //
				"//   float blend = roughness * ENV_LODS;\r\n" + //
				"//   float level0 = floor(blend);\r\n" + //
				"//   float level1 = min(ENV_LODS, level0 + 1.0);\r\n" + //
				"//   blend -= level0;\r\n" + //
				"  \r\n" + //
				"//   // Sample the specular env map atlas depending on the roughness value\r\n" + //
				"//   vec2 uvSpec = sampleEnvironmentMap(reflection);\r\n" + //
				"//   uvSpec.y /= 2.0;\r\n" + //
				"//   vec2 uv0 = uvSpec;\r\n" + //
				"//   vec2 uv1 = uvSpec;\r\n" + //
				"//   uv0 /= pow(2.0, level0);\r\n" + //
				"//   uv0.y += 1.0 - exp(-LN2 * level0);\r\n" + //
				"//   uv1 /= pow(2.0, level1);\r\n" + //
				"//   uv1.y += 1.0 - exp(-LN2 * level1);\r\n" + //
				"//   vec3 specular0 = RGBMToLinear(texture2D(u_envSpecularMap, uv0)).rgb;\r\n" + //
				"//   vec3 specular1 = RGBMToLinear(texture2D(u_envSpecularMap, uv1)).rgb;\r\n" + //
				"//   vec3 specularLight = mix(specular0, specular1, blend);\r\n" + //
				"//   diffuse = diffuseLight * diffuseColor;\r\n" + //
				"  \r\n" + //
				"//   // Bit of extra reflection for smooth materials\r\n" + //
				"//   float reflectivity = pow((1.0 - roughness), 2.0) * 0.05;\r\n" + //
				"//   specular = specularLight * (specularColor * brdf.x + brdf.y + reflectivity);\r\n" + //
				"//   // specular *= uEnvSpecular;\r\n" + //
				"// }\r\n" + //
				"// void PBR() {\r\n" + //
				"//   vec4 baseDiffuseColor = getDiffuseColor();\r\n" + //
				"//   vec3 baseColor = baseDiffuseColor.rgb;\r\n" + //
				"//   vec4 orm = getOrmColor();\r\n" + //
				"//   vec3 tc = getTeamColor();\r\n" + //
				"//   float tcFactor = getOrmColor().a;\r\n" + //
				"//   if (tcFactor > 0.1) {\r\n" + //
				"//     baseColor *= tc * tcFactor;\r\n" + //
				"//   }\r\n" + //
				"//   float roughness = clamp(orm.g, 0.04, 1.0);\r\n" + //
				"//   float metallic = clamp(orm.b, 0.04, 1.0);\r\n" + //
				"//   vec3 f0 = vec3(0.04);\r\n" + //
				"//   vec3 diffuseColor = baseColor * (vec3(1.0) - f0) * (1.0 - metallic);\r\n" + //
				"//   vec3 specularColor = mix(f0, baseColor, metallic);\r\n" + //
				"//   vec3 specularEnvR0 = specularColor;\r\n" + //
				"//   vec3 specularEnvR90 = vec3(clamp(max(max(specularColor.r, specularColor.g), specularColor.b) * 25.0, 0.0, 1.0));\r\n"
				+ //
				"//   vec3 N = v_normal;\r\n" + //
				"//   vec3 V = normalize(v_eyeVec);\r\n" + //
				"//   vec3 L = normalize(v_lightDirWorld);\r\n" + //
				"//   vec3 H = normalize(L + V);\r\n" + //
				"//   vec3 reflection = normalize(reflect(-V, N));\r\n" + //
				"//   float NdL = clamp(dot(N, L), 0.001, 1.0);\r\n" + //
				"//   float NdV = clamp(abs(dot(N, V)), 0.001, 1.0);\r\n" + //
				"//   float NdH = clamp(dot(N, H), 0.0, 1.0);\r\n" + //
				"//   float LdH = clamp(dot(L, H), 0.0, 1.0);\r\n" + //
				"//   float VdH = clamp(dot(V, H), 0.0, 1.0);\r\n" + //
				"//   vec3 F = specularReflection(specularEnvR0, specularEnvR90, VdH);\r\n" + //
				"//   float G = geometricOcclusion(NdL, NdV, roughness);\r\n" + //
				"//   float D = microfacetDistribution(roughness, NdH);\r\n" + //
				"//   vec3 diffuseContrib = (1.0 - F) * (diffuseColor / PI);\r\n" + //
				"//   vec3 specContrib = F * G * D / (4.0 * NdL * NdV);\r\n" + //
				"  \r\n" + //
				"//   // Shading based off lights\r\n" + //
				"//   // vec3 color = NdL * uLightColor * (diffuseContrib + specContrib);\r\n" + //
				"//   vec3 color = NdL * (diffuseContrib + specContrib);\r\n" + //
				"//   // Calculate IBL lighting\r\n" + //
				"//   vec3 diffuseIBL;\r\n" + //
				"//   vec3 specularIBL;\r\n" + //
				"//   getIBLContribution(diffuseIBL, specularIBL, NdV, roughness, N, reflection, diffuseColor, specularColor);\r\n"
				+ //
				"//   // Add IBL on top of color\r\n" + //
				"//   color +=  specularIBL;\r\n" + //
				"//   color *= orm.r;\r\n" + //
				"//   color += getEmissiveColor();\r\n" + //
				"//   // Convert to sRGB to display\r\n" + //
				"//   gl_FragColor.rgb = color;\r\n" + //
				"//   gl_FragColor.a = baseDiffuseColor.a;\r\n" + //
				"// }\r\n" + //
				"void onlyDiffuse() {\r\n" + //
				"  vec4 baseColor = getDiffuseColor();\r\n" + //
				"  vec3 tc = getTeamColor();\r\n" + //
				"  float tcFactor = getOrmColor().a;\r\n" + //
				"  if (tcFactor > 0.1) {\r\n" + //
				"    baseColor.rgb *= tc * tcFactor;\r\n" + //
				"  }\r\n" + //
				"  gl_FragColor = baseColor;\r\n" + //
				"}\r\n" + //
				"void onlyNormalMap() {\r\n" + //
				"  gl_FragColor = vec4(decodeNormal(), 1.0);\r\n" + //
				"}\r\n" + //
				"void onlyOcclusion() {\r\n" + //
				"  gl_FragColor = vec4(getOrmColor().rrr, 1.0);\r\n" + //
				"}\r\n" + //
				"void onlyRoughness() {\r\n" + //
				"  gl_FragColor = vec4(getOrmColor().ggg, 1.0);\r\n" + //
				"}\r\n" + //
				"void onlyMetallic() {\r\n" + //
				"  gl_FragColor = vec4(getOrmColor().bbb, 1.0);\r\n" + //
				"}\r\n" + //
				"void onlyTeamColorFactor() {\r\n" + //
				"  gl_FragColor = vec4(getOrmColor().aaa, 1.0);\r\n" + //
				"}\r\n" + //
				"void onlyEmissiveMap() {\r\n" + //
				"  gl_FragColor = vec4(getEmissiveColor(), 1.0);\r\n" + //
				"}\r\n" + //
				"void onlyTexCoords() {\r\n" + //
				"  gl_FragColor = vec4(v_uv, 0.0, 1.0);\r\n" + //
				"}\r\n" + //
				"void onlyNormals() {\r\n" + //
				"  gl_FragColor = vec4(v_normal, 1.0);\r\n" + //
				"}\r\n" + //
				"#if defined(ONLY_TANGENTS)\r\n" + //
				"void onlyTangents() {\r\n" + //
				"  gl_FragColor = vec4(v_tangent, 1.0);\r\n" + //
				"}\r\n" + //
				"#endif\r\n" + //
				"void applyLight(vec4 thisLightColor, vec4 thisLightDir, vec3 normal, vec3 baseColor, vec3 tc, vec4 ormTexel, vec3 reflectionsTexel, float tcFactor, inout vec3 color, inout vec3 lambertFactorSum) {\r\n"
				+ //
				"  if (thisLightColor.a > 0) {;\r\n" + //
				"    float lambertFactor = clamp(dot(normal, thisLightDir.xyz), 0.0, 1.0);\r\n" + //
				"    \r\n" + //
				"			vec3 reflectDir = reflect(-thisLightDir.xyz, normal);\r\n" + //
				"			vec3 halfwayDir = normalize(thisLightDir.xyz + v_eyeVec);\r\n" + //
				"			float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);\r\n" + //
				"			vec3 specular = vec3(max(-ormTexel.g+0.5, 0.0)+ormTexel.b) * spec /* * (reflectionsTexel.rgb * (1.0 - ormTexel.g) + ormTexel.g * baseColor.rgb) */ *  thisLightColor.rgb;\r\n"
				+ //
				"    lambertFactorSum += clamp(lambertFactor, 0.0, 1.0) * thisLightColor.rgb * thisLightColor.a * thisLightDir.a;\r\n"
				+ //
				"        color += (specular) * thisLightColor.a * thisLightDir.a;\r\n" + //
				"  };\r\n" + //
				"}\r\n" + //
				"void lambert() {\r\n" + //
				"  vec4 baseColor = getDiffuseColor();\r\n" + //
				"  vec3 normal = decodeNormal();\r\n" + //
				"  vec4 orm = getOrmColor();\r\n" + //
				"  vec3 environmentMapColor = getEnvironmentMapColor();\r\n" + //
				"  vec3 emissive = getEmissiveColor();\r\n" + //
				"  vec3 tc = getTeamColor();\r\n" + //
				"  float aoFactor = orm.r;\r\n" + //
				"  float tcFactor = orm.a;\r\n" + //
				"  vec3 lambertFactorSum = vec3(0.0);\r\n" + //
//				"void applyLight(vec4 thisLightColor, vec3 thisLightDir, vec3 normal, vec3 baseColor, vec3 tc, float tcFactor, output vec3 color) {\r\n" + //
				"  vec3 color = vec3(0.0);\r\n" + //
				"  \r\n" + //
				"  float rowPos = (0.5) / u_lightTextureHeight;\r\n" + //
				"  vec4 lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"  applyLight(lightColor, v_lightDir, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"  if( u_lightTextureHeight > 1.5 ) {\r\n" + //
				"    rowPos = (1.5) / u_lightTextureHeight;\r\n" + //
				"    lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"    applyLight(lightColor, v_lightDir2, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"    if( u_lightTextureHeight > 2.5 ) {\r\n" + //
				"      rowPos = (2.5) / u_lightTextureHeight;\r\n" + //
				"      lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"      applyLight(lightColor, v_lightDir3, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"      if( u_lightTextureHeight > 3.5 ) {\r\n" + //
				"        rowPos = (3.5) / u_lightTextureHeight;\r\n" + //
				"        lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"        applyLight(lightColor, v_lightDir4, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"        if( u_lightTextureHeight > 4.5 ) {\r\n" + //
				"          rowPos = (4.5) / u_lightTextureHeight;\r\n" + //
				"          lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"          applyLight(lightColor, v_lightDir5, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"          if( u_lightTextureHeight > 5.5 ) {\r\n" + //
				"            rowPos = (5.5) / u_lightTextureHeight;\r\n" + //
				"            lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"            applyLight(lightColor, v_lightDir6, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"            if( u_lightTextureHeight > 6.5 ) {\r\n" + //
				"              rowPos = (6.5) / u_lightTextureHeight;\r\n" + //
				"              lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"              applyLight(lightColor, v_lightDir7, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"              if( u_lightTextureHeight > 7.5 ) {\r\n" + //
				"                rowPos = (7.5) / u_lightTextureHeight;\r\n" + //
				"                lightColor = texture2D(u_lightTexture, vec2(0.625, rowPos));\r\n" + //
				"                applyLight(lightColor, v_lightDir8, normal, baseColor.rgb, tc, orm, environmentMapColor, tcFactor, color, lambertFactorSum);\r\n"
				+ //
				"              }\r\n" + //
				"            }\r\n" + //
				"          }\r\n" + //
				"        }\r\n" + //
				"      }\r\n" + //
				"    }\r\n" + //
				"  }\r\n" + //
				"    vec3 diffuse = baseColor.rgb;\r\n" + //
				(MdxHandler.CURRENT_SHADER_TYPE != ShaderEnvironmentType.MENU ? "  if (tcFactor > 0.1) {\r\n" + //
						"      diffuse = diffuse * (1.0 - tcFactor) + diffuse * tc * tcFactor;\r\n" + //
						"    }\r\n" : "\r\n")
				+ //
				"  color = clamp(color, 0.0, 1.0) + diffuse * lambertFactorSum + emissive;\r\n" + //
				"  gl_FragColor = vec4(color, baseColor.a);\r\n" + //
				"}\r\n" + //
				"void main() {\r\n" + //
				"  #if defined(ONLY_DIFFUSE)\r\n" + //
				"  onlyDiffuse();\r\n" + //
				"  #elif defined(ONLY_NORMAL_MAP)\r\n" + //
				"  onlyNormalMap();\r\n" + //
				"  #elif defined(ONLY_OCCLUSION)\r\n" + //
				"  onlyOcclusion();\r\n" + //
				"  #elif defined(ONLY_ROUGHNESS)\r\n" + //
				"  onlyRoughness();\r\n" + //
				"  #elif defined(ONLY_METALLIC)\r\n" + //
				"  onlyMetallic();\r\n" + //
				"  #elif defined(ONLY_TC_FACTOR)\r\n" + //
				"  onlyTeamColorFactor();\r\n" + //
				"  #elif defined(ONLY_EMISSIVE)\r\n" + //
				"  onlyEmissiveMap();\r\n" + //
				"  #elif defined(ONLY_TEXCOORDS)\r\n" + //
				"  onlyTexCoords();\r\n" + //
				"  #elif defined(ONLY_NORMALS)\r\n" + //
				"  onlyNormals();\r\n" + //
				"  #elif defined(ONLY_TANGENTS)\r\n" + //
				"  onlyTangents();\r\n" + //
				"  #else\r\n" + //
				"  lambert();\r\n" + //
				"  #endif\r\n" + //
				"}";
	}

	public static final String vsSimple = "\r\n" + //
			"    uniform mat4 u_VP;\r\n" + //
			"    attribute vec3 a_m0;\r\n" + //
			"    attribute vec3 a_m1;\r\n" + //
			"    attribute vec3 a_m2;\r\n" + //
			"    attribute vec3 a_m3;\r\n" + //
			"    attribute vec3 a_position;\r\n" + //
			"    attribute vec2 a_uv;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    void main() {\r\n" + //
			"      v_uv = a_uv;\r\n" + //
			"      gl_Position = u_VP * mat4(a_m0, 0.0, a_m1, 0.0, a_m2, 0.0, a_m3, 1.0) * vec4(a_position, 1.0);\r\n" + //
			"    }\r\n";

	public static final String fsSimple = "\r\n" + //
			"    precision mediump float;\r\n" + //
			"    uniform sampler2D u_texture;\r\n" + //
			"    uniform float u_filterMode;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    void main() {\r\n" + //
			"      vec4 color = texture2D(u_texture, v_uv);\r\n" + //
			"      // 1bit Alpha\r\n" + //
			"      if (u_filterMode == 1.0 && color.a < 0.75) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			"      gl_FragColor = color;\r\n" + //
			"    }\r\n";

	public static final String vsComplex() {
		return "\r\n" + //
				"\r\n" + //
				"    uniform mat4 u_mvp;\r\n" + //
				"    uniform vec4 u_vertexColor;\r\n" + //
				"    uniform vec4 u_geosetColor;\r\n" + //
				"    uniform float u_layerAlpha;\r\n" + //
				"    uniform vec2 u_uvTrans;\r\n" + //
				"    uniform vec2 u_uvRot;\r\n" + //
				"    uniform float u_uvScale;\r\n" + //
				"    uniform bool u_hasBones;\r\n" + //
				"    uniform bool u_unshaded;\r\n" + //
				"    attribute vec3 a_position;\r\n" + //
				"    attribute vec3 a_normal;\r\n" + //
				"    attribute vec2 a_uv;\r\n" + //
				"    attribute vec4 a_bones;\r\n" + //
				"    #ifdef EXTENDED_BONES\r\n" + //
				"    attribute vec4 a_extendedBones;\r\n" + //
				"    #endif\r\n" + //
				"    attribute float a_boneNumber;\r\n" + //
				"    varying vec2 v_uv;\r\n" + //
				"    varying vec4 v_color;\r\n" + //
				"    varying vec4 v_uvTransRot;\r\n" + //
				"    varying float v_uvScale;\r\n" + //
				"    uniform sampler2D u_lightTexture;\r\n" + //
				"    uniform float u_lightCount;\r\n" + //
				"    uniform float u_lightTextureHeight;\r\n" + //
				Shaders.boneTexture + "\r\n" + //
				"    void transform(inout vec3 position, inout vec3 normal) {\r\n" + //
				"      // For the broken models out there, since the game supports this.\r\n" + //
				"      if (a_boneNumber > 0.0) {\r\n" + //
				"        vec4 position4 = vec4(position, 1.0);\r\n" + //
				"        vec4 normal4 = vec4(normal, 0.0);\r\n" + //
				"        mat4 bone;\r\n" + //
				"        vec4 p = vec4(0.0,0.0,0.0,0.0);\r\n" + //
				"        vec4 n = vec4(0.0,0.0,0.0,0.0);\r\n" + //
				"        for (int i = 0; i < 4; i++) {\r\n" + //
				"          if (a_bones[i] > 0.0) {\r\n" + //
				"            bone = fetchMatrix(a_bones[i] - 1.0, 0.0);\r\n" + //
				"            p += bone * position4;\r\n" + //
				"            n += bone * normal4;\r\n" + //
				"          }\r\n" + //
				"        }\r\n" + //
				"        #ifdef EXTENDED_BONES\r\n" + //
				"          for (int i = 0; i < 4; i++) {\r\n" + //
				"            if (a_extendedBones[i] > 0.0) {\r\n" + //
				"              bone = fetchMatrix(a_extendedBones[i] - 1.0, 0.0);\r\n" + //
				"              p += bone * position4;\r\n" + //
				"              n += bone * normal4;\r\n" + //
				"            }\r\n" + //
				"          }\r\n" + //
				"        #endif\r\n" + //
				"        position = p.xyz / a_boneNumber;\r\n" + //
				"        normal = normalize(n.xyz);\r\n" + //
				"      } else {\r\n" + //
				"        position.x += 100.0;\r\n" + //
				"      }\r\n" + //
				"\r\n" + //
				"    }\r\n" + //
				"    void main() {\r\n" + //
				"      vec3 position = a_position;\r\n" + //
				"      vec3 normal = a_normal;\r\n" + //
				"      if (u_hasBones) {\r\n" + //
				"        transform(position, normal);\r\n" + //
				"      }\r\n" + //
				"      v_uv = a_uv;\r\n" + //
				"      v_color = u_vertexColor * u_geosetColor.bgra * vec4(1.0, 1.0, 1.0, u_layerAlpha);\r\n" + //
				"      v_uvTransRot = vec4(u_uvTrans, u_uvRot);\r\n" + //
				"      v_uvScale = u_uvScale;\r\n" + //
				"      gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
				"      if(!u_unshaded) {\r\n" + //
				Shaders.lightSystem("normal", "position", "u_lightTexture", "u_lightTextureHeight", "u_lightCount",
						false)
				+ "\r\n" + //
				"        v_color.xyz *= clamp(lightFactor, 0.0, 1.0);\r\n" + //
				"      }\r\n" + //
				"    }";
	}

	public static final String fsComplex = Shaders.quatTransform + "\r\n\r\n" + //
			"    uniform sampler2D u_texture;\r\n" + //
			"    uniform vec4 u_vertexColor;\r\n" + //
			"    uniform float u_filterMode;\r\n" + //
			"    uniform bool u_unfogged;\r\n" + //
			"    uniform vec4 u_fogColor;\r\n" + //
			"    uniform vec4 u_fogParams;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    varying vec4 v_color;\r\n" + //
			"    varying vec4 v_uvTransRot;\r\n" + //
			"    varying float v_uvScale;\r\n" + //
			"    void main() {\r\n" + //
			"      vec2 uv = v_uv;\r\n" + //
			"      // Translation animation\r\n" + //
			"      uv += v_uvTransRot.xy;\r\n" + //
			"      // Rotation animation\r\n" + //
			"      uv = quat_transform(v_uvTransRot.zw, uv - 0.5) + 0.5;\r\n" + //
			"      // Scale animation\r\n" + //
			"      uv = v_uvScale * (uv - 0.5) + 0.5;\r\n" + //
			"      vec4 texel = texture2D(u_texture, uv);\r\n" + //
			"      vec4 color = texel * v_color;\r\n" + //
			"      // 1bit Alpha\r\n" + //
			"      if (u_vertexColor.a == 1.0 && u_filterMode == 1.0 && color.a < 0.75) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			"      // \"Close to 0 alpha\"\r\n" + //
			"      if (u_filterMode >= 5.0 && color.a < 0.02) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			Shaders.fogSystem(true, "u_filterMode < 3.0 || u_filterMode > 4.0") + //
			"      gl_FragColor = color;\r\n" + //
			"    }";

	public static final String fsComplexShadowMap = "\r\n\r\n" + //
			Shaders.quatTransform + "\r\n\r\n" + //
			"    uniform sampler2D u_texture;\r\n" + //
			"    uniform float u_filterMode;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    varying vec4 v_color;\r\n" + //
			"    varying vec4 v_uvTransRot;\r\n" + //
			"    varying float v_uvScale;\r\n" + //
			"    varying vec3 v_normal;\r\n" + //
//			"    layout(location = 0) out float fragmentdepth;\r\n" + //
			"    void main() {\r\n" + //
			"      vec2 uv = v_uv;\r\n" + //
			"      // Translation animation\r\n" + //
			"      uv += v_uvTransRot.xy;\r\n" + //
			"      // Rotation animation\r\n" + //
			"      uv = quat_transform(v_uvTransRot.zw, uv - 0.5) + 0.5;\r\n" + //
			"      // Scale animation\r\n" + //
			"      uv = v_uvScale * (uv - 0.5) + 0.5;\r\n" + //
			"      vec4 texel = texture2D(u_texture, uv);\r\n" + //
			"      vec4 color = texel * v_color;\r\n" + //
			"      // 1bit Alpha\r\n" + //
			"      if (u_filterMode == 1.0 && color.a < 0.75) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			"      // \"Close to 0 alpha\"\r\n" + //
			"      if (u_filterMode >= 5.0 && color.a < 0.02) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			"      gl_FragColor = vec4(0.0, 0, 0, 1.0);//gl_FragCoord.z;\r\n" + //
			"    }";

	public static final String vsParticles() {
		return "\r\n" + //
				"    #define EMITTER_PARTICLE2 0.0\r\n" + //
				"    #define EMITTER_RIBBON 1.0\r\n" + //
				"    #define EMITTER_SPLAT 2.0\r\n" + //
				"    #define EMITTER_UBERSPLAT 3.0\r\n" + //
				"    #define HEAD 0.0\r\n" + //
				"    uniform mat4 u_mvp;\r\n" + //
				"    uniform mediump float u_emitter;\r\n" + //
				"    // Shared\r\n" + //
				"    uniform vec4 u_colors[3];\r\n" + //
				"    uniform vec3 u_vertices[4];\r\n" + //
				"    uniform vec3 u_intervals[4];\r\n" + //
				"    uniform float u_lifeSpan;\r\n" + //
				"    uniform float u_columns;\r\n" + //
				"    uniform float u_rows;\r\n" + //
				"    // Particle2\r\n" + //
				"    uniform vec3 u_scaling;\r\n" + //
				"    uniform vec3 u_cameraZ;\r\n" + //
				"    uniform float u_timeMiddle;\r\n" + //
				"    uniform bool u_teamColored;\r\n" + //
				"    uniform bool u_unshaded;\r\n" + //
				"    uniform sampler2D u_lightTexture;\r\n" + //
				"    uniform float u_lightCount;\r\n" + //
				"    uniform float u_lightTextureHeight;\r\n" + //
				"    // Splat and Uber.\r\n" + //
				"    uniform vec3 u_intervalTimes;\r\n" + //
				"    // Vertices\r\n" + //
				"    attribute float a_position;\r\n" + //
				"    // Instances\r\n" + //
				"    attribute vec3 a_p0;\r\n" + //
				"    attribute vec3 a_p1;\r\n" + //
				"    attribute vec3 a_p2;\r\n" + //
				"    attribute vec3 a_p3;\r\n" + //
				"    attribute float a_health;\r\n" + //
				"    attribute vec4 a_color;\r\n" + //
				"    attribute float a_tail;\r\n" + //
				"    attribute vec3 a_leftRightTop;\r\n" + //
				"    varying vec2 v_texcoord;\r\n" + //
				"    varying vec4 v_color;\r\n" + //
				"    float getCell(vec3 interval, float factor) {\r\n" + //
				"      float start = interval[0];\r\n" + //
				"      float end = interval[1];\r\n" + //
				"      float repeat = interval[2];\r\n" + //
				"      float spriteCount = end - start;\r\n" + //
				"      if (spriteCount > 0.0) {\r\n" + //
				"        // Repeating speeds up the sprite animation, which makes it effectively run N times in its interval.\r\n"
				+ //
				"        // E.g. if repeat is 4, the sprite animation will be seen 4 times, and thus also run 4 times as fast.\r\n"
				+ //
				"        // The sprite index is limited to the number of actual sprites.\r\n" + //
				"        return min(start + mod(floor(spriteCount * repeat * factor), spriteCount), u_columns * u_rows - 1.0);\r\n"
				+ //
				"      }\r\n" + //
				"      return start;\r\n" + //
				"    }\r\n" + //
				"    void particle2() {\r\n" + //
				"      float factor = (u_lifeSpan - a_health) / u_lifeSpan;\r\n" + //
				"      int index = 0;\r\n" + //
				"      if (factor < u_timeMiddle) {\r\n" + //
				"        factor = factor / u_timeMiddle;\r\n" + //
				"        index = 0;\r\n" + //
				"      } else {\r\n" + //
				"        factor = (factor - u_timeMiddle) / (1.0 - u_timeMiddle);\r\n" + //
				"        index = 1;\r\n" + //
				"      }\r\n" + //
				"      factor = min(factor, 1.0);\r\n" + //
				"      float scale = mix(u_scaling[index], u_scaling[index + 1], factor);\r\n" + //
				"      vec4 color = mix(u_colors[index], u_colors[index + 1], factor);\r\n" + //
				"      float cell = 0.0;\r\n" + //
				"      if (u_teamColored) {\r\n" + //
				"        cell = a_leftRightTop[0];\r\n" + //
				"      } else {\r\n" + //
				"        vec3 interval;\r\n" + //
				"        if (a_tail == HEAD) {\r\n" + //
				"          interval = u_intervals[index];\r\n" + //
				"        } else {\r\n" + //
				"          interval = u_intervals[index + 2];\r\n" + //
				"        }\r\n" + //
				"        cell = getCell(interval, factor);\r\n" + //
				"      }\r\n" + //
				"      float left = floor(mod(cell, u_columns));\r\n" + //
				"      float top = floor(cell / u_columns);\r\n" + //
				"      float right = left + 1.0;\r\n" + //
				"      float bottom = top + 1.0;\r\n" + //
				"      left /= u_columns;\r\n" + //
				"      right /= u_columns;\r\n" + //
				"      top /= u_rows;\r\n" + //
				"      bottom /= u_rows;\r\n" + //
				"      if (a_position == 0.0) {\r\n" + //
				"        v_texcoord = vec2(right, top);\r\n" + //
				"      } else if (a_position == 1.0) {\r\n" + //
				"        v_texcoord = vec2(left, top);\r\n" + //
				"      } else if (a_position == 2.0) {\r\n" + //
				"        v_texcoord = vec2(left, bottom);\r\n" + //
				"      } else if (a_position == 3.0) {\r\n" + //
				"        v_texcoord = vec2(right, bottom);\r\n" + //
				"      }\r\n" + //
				"      v_color = color;\r\n" + //
				"      \r\n" + //
				"      vec3 lightingNormal;\r\n" + //
				"      vec3 position;\r\n" + //
				"      if (a_tail == HEAD) {\r\n" + //
				"        vec3 vertices[4];\r\n" + //
				"        if(a_p1[0] != 0.0 || a_p1[1] != 0.0) {\r\n" + //
				"          lightingNormal = vec3(0.0, 0.0, 1.0);\r\n" + //
				"          vec3 vx;\r\n" + //
				"          vx[0] = a_p1[0];\r\n" + //
				"          vx[1] = a_p1[1];\r\n" + //
				"          vx[2] = 0.0;\r\n" + //
				"          vx = normalize(vx);\r\n" + //
				"          vec3 vy;\r\n" + //
				"          vy[0] = -vx[1];\r\n" + //
				"          vy[1] = vx[0];\r\n" + //
				"          vy[2] = 0.0;\r\n" + //
				"          vertices[3] = - vx - vy;\r\n" + //
				"          vertices[0] = vx - vy;\r\n" + //
				"          vertices[1] = -vertices[3];\r\n" + //
				"          vertices[2] = -vertices[0];\r\n" + //
				"        } else {\r\n" + //
				"          lightingNormal = normalize(u_cameraZ);\r\n" + //
				"          vertices[0] = u_vertices[0];\r\n" + //
				"          vertices[1] = u_vertices[1];\r\n" + //
				"          vertices[2] = u_vertices[2];\r\n" + //
				"          vertices[3] = u_vertices[3];\r\n" + //
				"        }\r\n" + //
				"        position =  a_p0 + (vertices[int(a_position)] * scale);\r\n" + //
				"        gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
				"      } else {\r\n" + //
				"        // Get the normal to the tail in camera space.\r\n" + //
				"        // This allows to build a 2D rectangle around the 3D tail.\r\n" + //
				"        vec3 normal = cross(u_cameraZ, normalize(a_p1 - a_p0));\r\n" + //
				"        vec3 boundary = normal * scale * a_p2[0];\r\n" + //
				"        if (a_position == 0.0) {\r\n" + //
				"          position = a_p0 - boundary;\r\n" + //
				"        } else if (a_position == 1.0) {\r\n" + //
				"          position = a_p1 - boundary;\r\n" + //
				"        } else if (a_position == 2.0) {\r\n" + //
				"          position = a_p1 + boundary;\r\n" + //
				"        } else if (a_position == 3.0) {\r\n" + //
				"          position = a_p0 + boundary;\r\n" + //
				"        }\r\n" + //
				"        gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
				"        lightingNormal = normalize(u_cameraZ);\r\n" + //
				"      }\r\n" + //
				"      if(!u_unshaded) {\r\n" + //
				Shaders.lightSystem("lightingNormal", "position", "u_lightTexture", "u_lightTextureHeight",
						"u_lightCount", false)
				+ "\r\n" + //
				"        v_color.xyz *= clamp(lightFactor, 0.0, 1.0);\r\n" + //
				"      }\r\n" + //
				"    }\r\n" + //
				"    void ribbon() {\r\n" + //
				"      vec3 position;\r\n" + //
				"      float left = a_leftRightTop[0] / 255.0;\r\n" + //
				"      float right = a_leftRightTop[1] / 255.0;\r\n" + //
				"      float top = a_leftRightTop[2] / 255.0;\r\n" + //
				"      float bottom = top + 1.0;\r\n" + //
				"      if (a_position == 0.0) {\r\n" + //
				"        v_texcoord = vec2(right, top);\r\n" + //
				"        position = a_p0;\r\n" + //
				"      } else if (a_position == 1.0) {\r\n" + //
				"        v_texcoord = vec2(right, bottom);\r\n" + //
				"        position = a_p1;\r\n" + //
				"      } else if (a_position == 2.0) {\r\n" + //
				"        v_texcoord = vec2(left, bottom);\r\n" + //
				"        position = a_p2;\r\n" + //
				"      } else if (a_position == 3.0) {\r\n" + //
				"        v_texcoord = vec2(left, top);\r\n" + //
				"        position = a_p3;\r\n" + //
				"      }\r\n" + //
				"      v_texcoord[0] /= u_columns;\r\n" + //
				"      v_texcoord[1] /= u_rows;\r\n" + //
				"      v_color = a_color;\r\n" + //
				"      gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
				"    }\r\n" + //
				"    void splat() {\r\n" + //
				"      float factor = u_lifeSpan - a_health;\r\n" + //
				"      int index;\r\n" + //
				"      if (factor < u_intervalTimes[0]) {\r\n" + //
				"        factor = factor / u_intervalTimes[0];\r\n" + //
				"        index = 0;\r\n" + //
				"      } else {\r\n" + //
				"        factor = (factor - u_intervalTimes[0]) / u_intervalTimes[1];\r\n" + //
				"        index = 1;\r\n" + //
				"      }\r\n" + //
				"      float cell = getCell(u_intervals[index], factor);\r\n" + //
				"      float left = floor(mod(cell, u_columns));\r\n" + //
				"      float top = floor(cell / u_columns);\r\n" + //
				"      float right = left + 1.0;\r\n" + //
				"      float bottom = top + 1.0;\r\n" + //
				"      vec3 position;\r\n" + //
				"      if (a_position == 0.0) {\r\n" + //
				"        v_texcoord = vec2(left, top);\r\n" + //
				"        position = a_p0;\r\n" + //
				"      } else if (a_position == 1.0) {\r\n" + //
				"        v_texcoord = vec2(left, bottom);\r\n" + //
				"        position = a_p1;\r\n" + //
				"      } else if (a_position == 2.0) {\r\n" + //
				"        v_texcoord = vec2(right, bottom);\r\n" + //
				"        position = a_p2;\r\n" + //
				"      } else if (a_position == 3.0) {\r\n" + //
				"        v_texcoord = vec2(right, top);\r\n" + //
				"        position = a_p3;\r\n" + //
				"      }\r\n" + //
				"      vec3 lightingNormal = normalize(a_leftRightTop-127.5);\r\n" + //
				"      v_texcoord[0] /= u_columns;\r\n" + //
				"      v_texcoord[1] /= u_rows;\r\n" + //
				"      v_color = mix(u_colors[index], u_colors[index + 1], factor) / 255.0;\r\n" + //
				"      gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
				"      if(!u_unshaded) {\r\n" + //
				Shaders.lightSystem("lightingNormal", "position", "u_lightTexture", "u_lightTextureHeight",
						"u_lightCount", false)
				+ "\r\n" + //
				"        v_color.xyz *= clamp(lightFactor, 0.0, 1.0);\r\n" + //
				"      }\r\n" + //
				"    }\r\n" + //
				"    void ubersplat() {\r\n" + //
				"      float factor = u_lifeSpan - a_health;\r\n" + //
				"      vec4 color;\r\n" + //
				"      if (factor < u_intervalTimes[0]) {\r\n" + //
				"        color = mix(u_colors[0], u_colors[1], factor / u_intervalTimes[0]);\r\n" + //
				"      } else if (factor < u_intervalTimes[0] + u_intervalTimes[1]) {\r\n" + //
				"        color = u_colors[1];\r\n" + //
				"      } else {\r\n" + //
				"        color = mix(u_colors[1], u_colors[2], (factor - u_intervalTimes[0] - u_intervalTimes[1]) / u_intervalTimes[2]);\r\n"
				+ //
				"      }\r\n" + //
				"      vec3 position;\r\n" + //
				"      if (a_position == 0.0) {\r\n" + //
				"        v_texcoord = vec2(0.0, 0.0);\r\n" + //
				"        position = a_p0;\r\n" + //
				"      } else if (a_position == 1.0) {\r\n" + //
				"        v_texcoord = vec2(0.0, 1.0);\r\n" + //
				"        position = a_p1;\r\n" + //
				"      } else if (a_position == 2.0) {\r\n" + //
				"        v_texcoord = vec2(1.0, 1.0);\r\n" + //
				"        position = a_p2;\r\n" + //
				"      } else if (a_position == 3.0) {\r\n" + //
				"        v_texcoord = vec2(1.0, 0.0);\r\n" + //
				"        position = a_p3;\r\n" + //
				"      }\r\n" + //
				"      v_color = color / 255.0;\r\n" + //
				"      gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
				"    }\r\n" + //
				"    void main() {\r\n" + //
				"      if (u_emitter == EMITTER_PARTICLE2) {\r\n" + //
				"        particle2();\r\n" + //
				"      } else if (u_emitter == EMITTER_RIBBON) {\r\n" + //
				"        ribbon();\r\n" + //
				"      } else if (u_emitter == EMITTER_SPLAT) {\r\n" + //
				"        splat();\r\n" + //
				"      } else if (u_emitter == EMITTER_UBERSPLAT) {\r\n" + //
				"        ubersplat();\r\n" + //
				"      }\r\n" + //
				"    }";
	}

	public static final String fsParticles = "\r\n" + //
			"    #define EMITTER_RIBBON 1.0\r\n" + //
			"    uniform sampler2D u_texture;\r\n" + //
			"    uniform mediump float u_emitter;\r\n" + //
			"    uniform float u_filterMode;\r\n" + //
			"    varying vec2 v_texcoord;\r\n" + //
			"    varying vec4 v_color;\r\n" + //
			"    uniform bool u_unfogged;\r\n" + //
			"    uniform vec4 u_fogColor;\r\n" + //
			"    uniform vec4 u_fogParams;\r\n" + //
			"    void main() {\r\n" + //
			"      vec4 texel = texture2D(u_texture, v_texcoord);\r\n" + //
			"      vec4 color = texel * v_color;\r\n" + //
			"      // 1bit Alpha, used by ribbon emitters.\r\n" + //
			"      if (u_emitter == EMITTER_RIBBON && u_filterMode == 1.0 && color.a < 0.75) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			Shaders.fogSystem(true,
					"(u_filterMode != 1.0 && u_filterMode != 4.0 && u_emitter != EMITTER_RIBBON) || ((u_filterMode < 3.0 || u_filterMode > 4.0) && u_emitter == EMITTER_RIBBON)")
			+ //
			"      gl_FragColor = color;\r\n" + //
			"    }";

	public static final String vsLightning = "\r\n" + //
			"    uniform mat4 u_VP;\r\n" + //
			"    attribute vec3 a_position;\r\n" + //
			"    attribute vec2 a_uv;\r\n" + //
			"    attribute float a_outwardHeight;\r\n" + //
			"    attribute vec4 a_color;\r\n" + //
			"    uniform vec3 u_cameraZ;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    varying vec4 v_color;\r\n" + //
			"    void main() {\r\n" + //
			"      v_uv = a_uv;\r\n" + //
			"      v_color = a_color;\r\n" + //
			"      gl_Position = u_VP * vec4(a_position, 1.0);\r\n" + //
			"    }\r\n";

	public static final String fsLightning = "\r\n" + //
	// " precision mediump float;\r\n" + //
			"    uniform sampler2D u_texture;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    varying vec4 v_color;\r\n" + //
			"    void main() {\r\n" + //
			"      vec4 color = texture2D(u_texture, v_uv);\r\n" + //
			"      gl_FragColor = color * v_color;\r\n" + //
			"    }\r\n";
}
