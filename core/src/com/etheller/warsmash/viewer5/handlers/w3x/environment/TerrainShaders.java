package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import com.etheller.warsmash.viewer5.Shaders;

/**
 * Mostly copied from HiveWE!
 */
public class TerrainShaders {
	public static final class Cliffs {
		private Cliffs() {
		}

		public static final String vert() {
			return "#version 330 core\r\n" + //
					"\r\n" + //
					"in vec3 vPosition;\r\n" + //
					"in vec2 vUV;\r\n" + //
					"in vec3 vNormal;\r\n" + //
					"in vec4 vOffset;\r\n" + //
					"\r\n" + //
					"uniform mat4 MVP;\r\n" + //
					"\r\n" + //
					"uniform sampler2D height_texture;\r\n" + //
					"uniform sampler2D shadowMap;\r\n" + //
					"uniform float centerOffsetX;\r\n" + //
					"uniform float centerOffsetY;\r\n" + //
					"uniform sampler2D lightTexture;\r\n" + //
					"uniform float lightCount;\r\n" + //
					"uniform float lightTextureHeight;\r\n" + //
					"\r\n" + //
					"out vec3 UV;\r\n" + //
					"out vec3 Normal;\r\n" + //
					"out vec2 pathing_map_uv;\r\n" + //
					"out vec3 position;\r\n" + //
					"out vec2 v_suv;\r\n" + //
					"out vec3 shadeColor;\r\n" + //
					"\r\n" + //
					"void main() {\r\n" + //
					"	pathing_map_uv = (vec2(vPosition.y, -vPosition.x) / 128 + vOffset.xy) * 4;\r\n" + //
					" \r\n" + //
					"	ivec2 size = textureSize(height_texture, 0);\r\n" + //
					"   ivec2 shadowSize = textureSize(shadowMap, 0);\r\n" + //
					"	v_suv = pathing_map_uv / shadowSize;\r\n" + //
					"	float value = texture(height_texture, (vOffset.xy + vec2(vPosition.y + 64, -vPosition.x + 64) / 128.0) / vec2(size)).r;\r\n"
					+ //
					"\r\n" + //
					"   position = (vec3(vPosition.y, -vPosition.x, vPosition.z) + vec3(vOffset.xy, vOffset.z + value) * 128 );\r\n"
					+ //
					"   vec4 myposition = vec4(position, 1);\r\n" + //
					"   myposition.x += centerOffsetX;\r\n" + //
					"   myposition.y += centerOffsetY;\r\n" + //
					"   position.x /= (size.x * 128.0);\r\n" + //
					"   position.y /= (size.y * 128.0);\r\n" + //
					"	gl_Position = MVP * myposition;\r\n" + //
					"	UV = vec3(vUV, vOffset.a);\r\n" + //
					"\r\n" + //
					"	ivec2 height_pos = ivec2(vOffset.xy + vec2(vPosition.y, -vPosition.x) / 128);\r\n" + //
					"	ivec3 off = ivec3(1, 1, 0);\r\n" + //
					"	float hL = texelFetch(height_texture, height_pos - off.xz, 0).r;\r\n" + //
					"	float hR = texelFetch(height_texture, height_pos + off.xz, 0).r;\r\n" + //
					"	float hD = texelFetch(height_texture, height_pos - off.zy, 0).r;\r\n" + //
					"	float hU = texelFetch(height_texture, height_pos + off.zy, 0).r;\r\n" + //
					"	bool edgeX = (vPosition.y) == float((int(vPosition.y))/128*128);\r\n" + //
					"	bool edgeY = (vPosition.x) == float((int(vPosition.x))/128*128);\r\n" + //
					"	bool edgeZ = (vPosition.z) == float((int(vPosition.z))/128*128);\r\n" + //
					"	vec3 terrain_normal = vec3(vNormal.y, -vNormal.x, vNormal.z);\r\n" + //
					"	if(edgeX) {\r\n" + //
					"	  terrain_normal.x = hL - hR;\r\n" + //
					"	}\r\n" + //
					"	if(edgeY) {\r\n" + //
					"	  terrain_normal.y = hD - hU;\r\n" + //
					"	}\r\n" + //
					"	if(edgeZ) {\r\n" + //
					"	  terrain_normal.z = 2.0;\r\n" + //
					"	}\r\n" + //
					"	terrain_normal = normalize(terrain_normal);\r\n" + //
					"\r\n" + //
					"	Normal = terrain_normal;\r\n" + //
					Shaders.lightSystem("terrain_normal", "myposition.xyz", "lightTexture", "lightTextureHeight",
							"lightCount", true)
					+ "\r\n" + //
					"        shadeColor = clamp(lightFactor, 0.0, 1.0);\r\n" + //
					"}";
		}

		public static final String frag = "#version 330 core\r\n" + //
				"\r\n" + //
				"uniform sampler2DArray cliff_textures;\r\n" + //
				"uniform sampler2D shadowMap;\r\n" + //
				"\r\n" + //
				"uniform bool show_lighting;\r\n" + //
				"\r\n" + //
				"in vec3 UV;\r\n" + //
				"in vec3 Normal;\r\n" + //
				"in vec2 pathing_map_uv;\r\n" + //
				"in vec3 position;\r\n" + //
				"in vec2 v_suv;\r\n" + //
				"in vec3 shadeColor;\r\n" + //
				"\r\n" + //
				"out vec4 color;\r\n" + //
				"\r\n" + //
				"void main() {\r\n" + //
				"	color = texture(cliff_textures, UV);\r\n" + //
				"\r\n" + //
				"   float shadow = texture2D(shadowMap, v_suv).r;\r\n" + //
				"   color.rgb *= (1.0 - shadow);\r\n" + //
				"	if (show_lighting) {\r\n" + //
				"		color.rgb *=  shadeColor;\r\n" + //
				"	}\r\n" + //
				"\r\n" + //
				"}";
	}

	public static final class Terrain {
		private Terrain() {
		}

		public static final String vert() {
			return "#version 330 core\r\n" + //
					"\r\n" + //
					"in vec2 vPosition;\r\n" + //
					"uniform mat4 MVP;\r\n" + //
					"uniform mat4 DepthBiasMVP;\r\n" + //
					"\r\n" + //
					"uniform sampler2D height_texture;\r\n" + //
					"uniform sampler2D height_cliff_texture;\r\n" + //
					"uniform usampler2D terrain_texture_list;\r\n" + //
					"uniform float centerOffsetX;\r\n" + //
					"uniform float centerOffsetY;\r\n" + //
					"uniform sampler2D lightTexture;\r\n" + //
					"uniform float lightCount;\r\n" + //
					"uniform float lightTextureHeight;\r\n" + //
					"\r\n" + //
					"out vec2 UV;\r\n" + //
					"flat out uvec4 texture_indices;\r\n" + //
					"out vec2 pathing_map_uv;\r\n" + //
					"out vec3 position;\r\n" + //
					"out vec3 ShadowCoord;\r\n" + //
					"out vec2 v_suv;\r\n" + //
					"out vec3 shadeColor;\r\n" + //
					"\r\n" + //
					"void main() { \r\n" + //
					"	ivec2 size = textureSize(terrain_texture_list, 0);\r\n" + //
					"	ivec2 pos = ivec2(gl_InstanceID % size.x, gl_InstanceID / size.x);\r\n" + //
					"\r\n" + //
					"	ivec2 height_pos = ivec2(vPosition + pos);\r\n" + //
					"	vec4 height = texelFetch(height_cliff_texture, height_pos, 0);\r\n" + //
					"\r\n" + //
					"	ivec3 off = ivec3(1, 1, 0);\r\n" + //
					"	float hL = texelFetch(height_texture, height_pos - off.xz, 0).r;\r\n" + //
					"	float hR = texelFetch(height_texture, height_pos + off.xz, 0).r;\r\n" + //
					"	float hD = texelFetch(height_texture, height_pos - off.zy, 0).r;\r\n" + //
					"	float hU = texelFetch(height_texture, height_pos + off.zy, 0).r;\r\n" + //
					"	vec3 normal = normalize(vec3(hL - hR, hD - hU, 2.0));\r\n" + //
					"\r\n" + //
					" UV = vec2(vPosition.x, 1 - vPosition.y);\r\n" + //
					// " UV = vec2(vPosition.x==0?0.01:0.99, vPosition.y==0?0.99:0.01);\r\n" + //
					"	texture_indices = texelFetch(terrain_texture_list, pos, 0);\r\n" + //
					"	pathing_map_uv = (vPosition + pos) * 4;	\r\n" + //
					"\r\n" + //
					"	// Cliff culling\r\n" + //
					"	vec3 positionWorld = vec3((vPosition.x + pos.x)*128.0 + centerOffsetX, (vPosition.y + pos.y)*128.0 + centerOffsetY, height.r*128.0);\r\n"
					+ //
					"	position = positionWorld;\r\n" + //
					"	gl_Position = ((texture_indices.a & 32768u) == 0u) ? MVP * vec4(position.xyz, 1) : vec4(2.0, 0.0, 0.0, 1.0);\r\n"
					+ //
					"	ShadowCoord = (((texture_indices.a & 32768u) == 0u) ? DepthBiasMVP * vec4(position.xyz, 1) : vec4(2.0, 0.0, 0.0, 1.0)).xyz;\r\n"
					+ //
					"   v_suv = (vPosition + pos) / size;\r\n" + //
					"	position.x = (position.x - centerOffsetX) / (size.x * 128.0);\r\n" + //
					"	position.y = (position.y - centerOffsetY) / (size.y * 128.0);\r\n" + //
					Shaders.lightSystem("normal", "positionWorld", "lightTexture", "lightTextureHeight", "lightCount",
							true)
					+ "\r\n" + //
					"        shadeColor = clamp(lightFactor, 0.0, 1.0);\r\n" + //
					"}";
		}

		public static final String frag = "#version 330 core\r\n" + //
				"\r\n" + //
				"uniform bool show_pathing_map;\r\n" + //
				"uniform bool show_lighting;\r\n" + //
				"\r\n" + //
				"uniform sampler2DArray sample0;\r\n" + //
				"uniform sampler2DArray sample1;\r\n" + //
				"uniform sampler2DArray sample2;\r\n" + //
				"uniform sampler2DArray sample3;\r\n" + //
				"uniform sampler2DArray sample4;\r\n" + //
				"uniform sampler2DArray sample5;\r\n" + //
				"uniform sampler2DArray sample6;\r\n" + //
				"uniform sampler2DArray sample7;\r\n" + //
				"uniform sampler2DArray sample8;\r\n" + //
				"uniform sampler2DArray sample9;\r\n" + //
				"uniform sampler2DArray sample10;\r\n" + //
				"uniform sampler2DArray sample11;\r\n" + //
				"uniform sampler2DArray sample12;\r\n" + //
				"uniform sampler2DArray sample13;\r\n" + //
				"uniform sampler2DArray sample14;\r\n" + //
				"uniform sampler2DArray sample15;\r\n" + //
				"uniform sampler2DArray sample16;\r\n" + //
				"\r\n" + //
//				"layout (binding = 20) uniform usampler2D pathing_map_static;\r\n" + //
//				"layout (binding = 21) uniform usampler2D pathing_map_dynamic;\r\n" + //
				"uniform sampler2D shadowMap;\r\n" + //
				"\r\n" + //
				"in vec2 UV;\r\n" + //
				"flat in uvec4 texture_indices;\r\n" + //
				"in vec2 pathing_map_uv;\r\n" + //
				"in vec3 position;\r\n" + //
				"in vec3 ShadowCoord;\r\n" + //
				"in vec2 v_suv;\r\n" + //
				"in vec3 shadeColor;\r\n" + //
				"\r\n" + //
				"out vec4 color;\r\n" + //
//				"layout (location = 1) out vec4 position;\r\n" + //
				"\r\n" + //
				"vec4 get_fragment(uint id, vec3 uv) {\r\n" + //
				"	vec2 dx = dFdx(uv.xy);\r\n" + //
				"	vec2 dy = dFdy(uv.xy);\r\n" + //
				"\r\n" + //
				"	switch(id) {\r\n" + //
				"		case 0u:\r\n" + //
				"			return textureGrad(sample0, uv, dx, dy);\r\n" + //
				"		case 1u:\r\n" + //
				"			return textureGrad(sample1, uv, dx, dy);\r\n" + //
				"		case 2u:\r\n" + //
				"			return textureGrad(sample2, uv, dx, dy);\r\n" + //
				"		case 3u:\r\n" + //
				"			return textureGrad(sample3, uv, dx, dy);\r\n" + //
				"		case 4u:\r\n" + //
				"			return textureGrad(sample4, uv, dx, dy);\r\n" + //
				"		case 5u:\r\n" + //
				"			return textureGrad(sample5, uv, dx, dy);\r\n" + //
				"		case 6u:\r\n" + //
				"			return textureGrad(sample6, uv, dx, dy);\r\n" + //
				"		case 7u:\r\n" + //
				"			return textureGrad(sample7, uv, dx, dy);\r\n" + //
				"		case 8u:\r\n" + //
				"			return textureGrad(sample8, uv, dx, dy);\r\n" + //
				"		case 9u:\r\n" + //
				"			return textureGrad(sample9, uv, dx, dy);\r\n" + //
				"		case 10u:\r\n" + //
				"			return textureGrad(sample10, uv, dx, dy);\r\n" + //
				"		case 11u:\r\n" + //
				"			return textureGrad(sample11, uv, dx, dy);\r\n" + //
				"		case 12u:\r\n" + //
				"			return textureGrad(sample12, uv, dx, dy);\r\n" + //
				"		case 13u:\r\n" + //
				"			return textureGrad(sample13, uv, dx, dy);\r\n" + //
				"		case 14u:\r\n" + //
				"			return textureGrad(sample14, uv, dx, dy);\r\n" + //
				"		case 15u:\r\n" + //
				"			return textureGrad(sample15, uv, dx, dy);\r\n" + //
				"		case 16u:\r\n" + //
				"			return textureGrad(sample16, uv, dx, dy);\r\n" + //
				"		case 17u:\r\n" + //
				"			return vec4(0, 0, 0, 0);\r\n" + //
				"	}\r\n" + //
				"}\r\n" + //
				"\r\n" + //
				"\r\n" + //
				"void main() {\r\n" + //
				"	color = get_fragment(texture_indices.a & 31u, vec3(UV, texture_indices.a >> 5));\r\n" + //
				"	color = color * color.a + get_fragment(texture_indices.b & 31u, vec3(UV, texture_indices.b >> 5)) * (1 - color.a);\r\n"
				+ //
				"	color = color * color.a + get_fragment(texture_indices.g & 31u, vec3(UV, texture_indices.g >> 5)) * (1 - color.a);\r\n"
				+ //
				"	color = color * color.a + get_fragment(texture_indices.r & 31u, vec3(UV, texture_indices.r >> 5)) * (1 - color.a);\r\n"
				+ //
				"   float shadow = texture2D(shadowMap, v_suv).r;\r\n" + //
//				"   float visibility = 1.0;\r\n" + //
//				"   if ( texture2D(shadowMap, ShadowCoord.xy).z > ShadowCoord.z ) {\r\n" + //
//				"       visibility = 0.5;\r\n" + //
//				"   }\r\n" + //
				"\r\n" + //
				"	if (show_lighting) {\r\n" + //
				"     color = vec4(color.xyz * (1.0 - shadow) * shadeColor, 1.0);\r\n" + //
				"	} else {\r\n" + //
				"     color = vec4(color.xyz * (1.0 - shadow), 1.0);\r\n" + //
				"	}\r\n" + //
//				"\r\n" + //
//				"	if (show_pathing_map) {\r\n" + //
//				"		uint byte_static = texelFetch(pathing_map_static, ivec2(pathing_map_uv), 0).r;\r\n" + //
//				"		uint byte_dynamic = texelFetch(pathing_map_dynamic, ivec2(pathing_map_uv), 0).r;\r\n" + //
//				"		uint final = byte_static.r | byte_dynamic.r;\r\n" + //
//				"\r\n" + //
//				"		vec4 pathing_static_color = vec4((final & 2) >> 1, (final & 4) >> 2, (final & 8) >> 3, 0.25);\r\n"
//				+ //
//				"\r\n" + //
//				"		color = length(pathing_static_color.rgb) > 0 ? color * 0.75 + pathing_static_color * 0.5 : color;\r\n"
//				+ //
//				"	}\r\n" + //
				"}";
	}

	public static final class Water {
		private Water() {
		}

		public static final String vert() {
			return "#version 330 core\r\n" + //
					"\r\n" + //
					"in vec2 vPosition;\r\n" + //
					"\r\n" + //
					"uniform sampler2D water_height_texture;\r\n" + //
					"uniform sampler2D ground_height_texture;\r\n" + //
					"uniform sampler2D water_exists_texture;\r\n" + //
					"uniform float centerOffsetX;\r\n" + //
					"uniform float centerOffsetY;\r\n" + //
					"\r\n" + //
					"uniform mat4 MVP;\r\n" + //
					"uniform vec4 shallow_color_min;\r\n" + //
					"uniform vec4 shallow_color_max;\r\n" + //
					"uniform vec4 deep_color_min;\r\n" + //
					"uniform vec4 deep_color_max;\r\n" + //
					"uniform float water_offset;\r\n" + //
					"uniform sampler2D lightTexture;\r\n" + //
					"uniform float lightCount;\r\n" + //
					"uniform float lightTextureHeight;\r\n" + //
					"\r\n" + //
					"out vec2 UV;\r\n" + //
					"out vec4 Color;\r\n" + //
					"out vec2 position;\r\n" + //
					"out vec3 shadeColor;\r\n" + //
					"\r\n" + //
					"const float min_depth = 10.f / 128;\r\n" + //
					"const float deeplevel = 64.f / 128;\r\n" + //
					"const float maxdepth = 72.f / 128;\r\n" + //
					"\r\n" + //
					"void main() { \r\n" + //
					"	ivec2 size = textureSize(water_height_texture, 0) - 1;\r\n" + //
					"	ivec2 pos = ivec2(gl_InstanceID % size.x, gl_InstanceID / size.x);\r\n" + //
					"	ivec2 height_pos = ivec2(vPosition + pos);\r\n" + //
					"	float water_height = texelFetch(water_height_texture, height_pos, 0).r + water_offset;\r\n" + //
					"\r\n" + //
					"	bool is_water = texelFetch(water_exists_texture, pos, 0).r > 0\r\n" + //
					"	 || texelFetch(water_exists_texture, pos + ivec2(1, 0), 0).r > 0\r\n" + //
					"	 || texelFetch(water_exists_texture, pos + ivec2(1, 1), 0).r > 0\r\n" + //
					"	 || texelFetch(water_exists_texture, pos + ivec2(0, 1), 0).r > 0;\r\n" + //
					"\r\n" + //
					"   position = vec2((vPosition.x + pos.x)*128.0 + centerOffsetX, (vPosition.y + pos.y)*128.0 + centerOffsetY);\r\n"
					+ //
					"   vec4 myposition = vec4(position.xy, water_height*128.0, 1);\r\n" + //
					"   vec3 Normal = vec3(0,0,1);\r\n" + //
					"	gl_Position = is_water ? MVP * myposition : vec4(2.0, 0.0, 0.0, 1.0);\r\n" + //
					"\r\n" + //
					"	UV = vec2((vPosition.x + pos.x%2)/2.0, (vPosition.y + pos.y%2)/2.0);\r\n" + //
					"\r\n" + //
					"	float ground_height = texelFetch(ground_height_texture, height_pos, 0).r;\r\n" + //
					"	float value = clamp(water_height - ground_height, 0.f, 1.f);\r\n" + //
					"	if (value <= deeplevel) {\r\n" + //
					"		value = max(0.f, value - min_depth) / (deeplevel - min_depth);\r\n" + //
					"		Color = shallow_color_min * (1.f - value) + shallow_color_max * value;\r\n" + //
					"	} else {\r\n" + //
					"		value = clamp(value - deeplevel, 0.f, maxdepth - deeplevel) / (maxdepth - deeplevel);\r\n" + //
					"		Color = deep_color_min * (1.f - value) + deep_color_max * value;\r\n" + //
					"	}\r\n" + //
					Shaders.lightSystem("Normal", "myposition.xyz", "lightTexture", "lightTextureHeight", "lightCount",
							true)
					+ "\r\n" + //
					"        shadeColor = clamp(lightFactor, 0.0, 1.0);\r\n" + //
					" }";
		}

		public static final String frag = "#version 330 core\r\n" + //
				"\r\n" + //
				"uniform sampler2DArray water_textures;\r\n" + //
				"uniform sampler2D water_exists_texture;\r\n" + //
				"\r\n" + //
				"\r\n" + //
				"uniform int current_texture;\r\n" + //
				"uniform vec4 mapBounds;\r\n" + //
				"\r\n" + //
				"in vec2 UV;\r\n" + //
				"in vec4 Color;\r\n" + //
				"in vec2 position;\r\n" + //
				"in vec3 shadeColor;\r\n" + //
				"\r\n" + //
				"out vec4 outColor;\r\n" + //
				"\r\n" + //
				"void main() {\r\n" + //
				"   vec2 d2 = min(position - mapBounds.xy, mapBounds.zw - position);\r\n" + //
				"   float d1 = clamp(min(d2.x, d2.y) / 64.0 + 1.0, 0.0, 1.0) * 0.8 + 0.2;;\r\n" + //
				"	outColor = texture(water_textures, vec3(UV, current_texture)) * vec4(Color.rgb * d1 * shadeColor, Color.a);\r\n"
				+ //
				"}";
	}
}
