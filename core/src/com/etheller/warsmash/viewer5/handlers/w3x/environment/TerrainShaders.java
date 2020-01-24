package com.etheller.warsmash.viewer5.handlers.w3x.environment;

public class TerrainShaders {
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

		public static final String frag = "#version 450 core\r\n" + //
				"\r\n" + //
				"layout (binding = 0) uniform sampler2DArray cliff_textures;\r\n" + //
				"layout (binding = 2) uniform usampler2D pathing_map_static;\r\n" + //
				"\r\n" + //
				"layout (location = 1) uniform bool show_pathing_map_static;\r\n" + //
				"layout (location = 2) uniform bool show_lighting;\r\n" + //
				"\r\n" + //
				"layout (location = 0) in vec3 UV;\r\n" + //
				"layout (location = 1) in vec3 Normal;\r\n" + //
				"layout (location = 2) in vec2 pathing_map_uv;\r\n" + //
				"\r\n" + //
				"out vec4 color;\r\n" + //
				"\r\n" + //
				"void main() {\r\n" + //
				"	color = texture(cliff_textures, UV);\r\n" + //
				"\r\n" + //
				"	if (show_lighting) {\r\n" + //
				"		vec3 light_direction = vec3(-0.3, -0.3, 0.25);\r\n" + //
				"		light_direction = normalize(light_direction);\r\n" + //
				"\r\n" + //
				"		color.rgb *= clamp(dot(Normal, light_direction) + 0.45, 0, 1);\r\n" + //
				"	}\r\n" + //
				"\r\n" + //
				"	uvec4 byte = texelFetch(pathing_map_static, ivec2(pathing_map_uv), 0);\r\n" + //
				"	if (show_pathing_map_static) {\r\n" + //
				"		vec4 pathing_color = vec4(min(byte.r & 2, 1), min(byte.r & 4, 1), min(byte.r & 8, 1), 0.25);\r\n"
				+ //
				"		color = length(pathing_color.rgb) > 0 ? color * 0.75 + pathing_color * 0.5 : color;\r\n" + //
				"	}\r\n" + //
				"}";
	}

	public static final class Terrain {
		private Terrain() {
		}

		public static final String vert = "#version 450 core\r\n" + //
				"\r\n" + //
				"layout (location = 0) in vec2 vPosition;\r\n" + //
				"layout (location = 1) uniform mat4 MVP;\r\n" + //
				"\r\n" + //
				"layout (binding = 0) uniform sampler2D height_texture;\r\n" + //
				"layout (binding = 1) uniform sampler2D height_cliff_texture;\r\n" + //
				"layout (binding = 2) uniform usampler2D terrain_texture_list;\r\n" + //
				"layout (location = 4) uniform float centerOffsetX;\r\n" + //
				"layout (location = 5) uniform float centerOffsetY;\r\n" + //
				"\r\n" + //
				"layout (location = 0) out vec2 UV;\r\n" + //
				"layout (location = 1) out flat uvec4 texture_indices;\r\n" + //
				"layout (location = 2) out vec2 pathing_map_uv;\r\n" + //
				"layout (location = 3) out vec3 normal;\r\n" + //
				"layout (location = 6) out float v2Position;\r\n" + //
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
				"	normal = normalize(vec3(hL - hR, hD - hU, 2.0));\r\n" + //
				"\r\n" + //
				"	UV = vec2(vPosition.x, 1 - vPosition.y);\r\n" + //
				"	texture_indices = texelFetch(terrain_texture_list, pos, 0);\r\n" + //
				"	pathing_map_uv = (vPosition + pos) * 4;	\r\n" + //
				"\r\n" + //
				"	// Cliff culling\r\n" + //
				"	gl_Position = ((texture_indices.a & 32768) == 0) ? MVP * vec4((vPosition.x + pos.x)*128.0 + centerOffsetX, (vPosition.y + pos.y)*128.0 + centerOffsetY, height.r*128.0, 1) : vec4(2.0, 0.0, 0.0, 1.0);\r\n"
				+ //
//				"   v2Position = float(texture_indices.r+texture_indices.b+texture_indices.g+texture_indices.a);\r\n" + //
				"}";

		public static final String frag = "#version 450 core\r\n" + //
				"\r\n" + //
				"layout (location = 2) uniform bool show_pathing_map;\r\n" + //
				"layout (location = 3) uniform bool show_lighting;\r\n" + //
				"\r\n" + //
				"layout (binding = 3) uniform sampler2DArray sample0;\r\n" + //
				"layout (binding = 4) uniform sampler2DArray sample1;\r\n" + //
				"layout (binding = 5) uniform sampler2DArray sample2;\r\n" + //
				"layout (binding = 6) uniform sampler2DArray sample3;\r\n" + //
				"layout (binding = 7) uniform sampler2DArray sample4;\r\n" + //
				"layout (binding = 8) uniform sampler2DArray sample5;\r\n" + //
				"layout (binding = 9) uniform sampler2DArray sample6;\r\n" + //
				"layout (binding = 10) uniform sampler2DArray sample7;\r\n" + //
				"layout (binding = 11) uniform sampler2DArray sample8;\r\n" + //
				"layout (binding = 12) uniform sampler2DArray sample9;\r\n" + //
				"layout (binding = 13) uniform sampler2DArray sample10;\r\n" + //
				"layout (binding = 14) uniform sampler2DArray sample11;\r\n" + //
				"layout (binding = 15) uniform sampler2DArray sample12;\r\n" + //
				"layout (binding = 16) uniform sampler2DArray sample13;\r\n" + //
				"layout (binding = 17) uniform sampler2DArray sample14;\r\n" + //
				"layout (binding = 18) uniform sampler2DArray sample15;\r\n" + //
				"layout (binding = 19) uniform sampler2DArray sample16;\r\n" + //
				"\r\n" + //
				"layout (binding = 20) uniform usampler2D pathing_map_static;\r\n" + //
				"layout (binding = 21) uniform usampler2D pathing_map_dynamic;\r\n" + //
				"\r\n" + //
				"layout (location = 0) in vec2 UV;\r\n" + //
				"layout (location = 1) in flat uvec4 texture_indices;\r\n" + //
				"layout (location = 2) in vec2 pathing_map_uv;\r\n" + //
				"layout (location = 3) in vec3 normal;\r\n" + //
				"layout (location = 6) in float v2Position;\r\n" + //
				"\r\n" + //
				"layout (location = 0) out vec4 color;\r\n" + //
				"layout (location = 1) out vec4 position;\r\n" + //
				"\r\n" + //
				"vec4 get_fragment(uint id, vec3 uv) {\r\n" + //
				"	vec2 dx = dFdx(uv.xy);\r\n" + //
				"	vec2 dy = dFdy(uv.xy);\r\n" + //
				"\r\n" + //
				"	switch(id) {\r\n" + //
				"		case 0:\r\n" + //
				"			return textureGrad(sample0, uv, dx, dy);\r\n" + //
				"		case 1:\r\n" + //
				"			return textureGrad(sample1, uv, dx, dy);\r\n" + //
				"		case 2:\r\n" + //
				"			return textureGrad(sample2, uv, dx, dy);\r\n" + //
				"		case 3:\r\n" + //
				"			return textureGrad(sample3, uv, dx, dy);\r\n" + //
				"		case 4:\r\n" + //
				"			return textureGrad(sample4, uv, dx, dy);\r\n" + //
				"		case 5:\r\n" + //
				"			return textureGrad(sample5, uv, dx, dy);\r\n" + //
				"		case 6:\r\n" + //
				"			return textureGrad(sample6, uv, dx, dy);\r\n" + //
				"		case 7:\r\n" + //
				"			return textureGrad(sample7, uv, dx, dy);\r\n" + //
				"		case 8:\r\n" + //
				"			return textureGrad(sample8, uv, dx, dy);\r\n" + //
				"		case 9:\r\n" + //
				"			return textureGrad(sample9, uv, dx, dy);\r\n" + //
				"		case 10:\r\n" + //
				"			return textureGrad(sample10, uv, dx, dy);\r\n" + //
				"		case 11:\r\n" + //
				"			return textureGrad(sample11, uv, dx, dy);\r\n" + //
				"		case 12:\r\n" + //
				"			return textureGrad(sample12, uv, dx, dy);\r\n" + //
				"		case 13:\r\n" + //
				"			return textureGrad(sample13, uv, dx, dy);\r\n" + //
				"		case 14:\r\n" + //
				"			return textureGrad(sample14, uv, dx, dy);\r\n" + //
				"		case 15:\r\n" + //
				"			return textureGrad(sample15, uv, dx, dy);\r\n" + //
				"		case 16:\r\n" + //
				"			return textureGrad(sample16, uv, dx, dy);\r\n" + //
				"		case 17:\r\n" + //
				"			return vec4(0, 0, 0, 0);\r\n" + //
				"	}\r\n" + //
				"}\r\n" + //
				"\r\n" + //
				"\r\n" + //
				"void main() {\r\n" + //
				"	color = get_fragment(texture_indices.a & 31, vec3(UV, texture_indices.a >> 5));\r\n" + //
				"	color = color * color.a + get_fragment(texture_indices.b & 31, vec3(UV, texture_indices.b >> 5)) * (1 - color.a);\r\n"
				+ //
				"	color = color * color.a + get_fragment(texture_indices.g & 31, vec3(UV, texture_indices.g >> 5)) * (1 - color.a);\r\n"
				+ //
				"	color = color * color.a + get_fragment(texture_indices.r & 31, vec3(UV, texture_indices.r >> 5)) * (1 - color.a);\r\n"
				+ //
				"\r\n" + //
				"	if (show_lighting) {\r\n" + //
				"		vec3 light_direction = vec3(-0.3, -0.3, 0.25);\r\n" + //
				"		light_direction = normalize(light_direction);\r\n" + //
				"\r\n" + //
				"		color.rgb *= clamp(dot(normal, light_direction) + 0.45, 0, 1);\r\n" + //
				"	}\r\n" + //
				"\r\n" + //
				"	if (show_pathing_map) {\r\n" + //
				"		uint byte_static = texelFetch(pathing_map_static, ivec2(pathing_map_uv), 0).r;\r\n" + //
				"		uint byte_dynamic = texelFetch(pathing_map_dynamic, ivec2(pathing_map_uv), 0).r;\r\n" + //
				"		uint final = byte_static.r | byte_dynamic.r;\r\n" + //
				"\r\n" + //
				"		vec4 pathing_static_color = vec4((final & 2) >> 1, (final & 4) >> 2, (final & 8) >> 3, 0.25);\r\n"
				+ //
				"\r\n" + //
				"		color = length(pathing_static_color.rgb) > 0 ? color * 0.75 + pathing_static_color * 0.5 : color;\r\n"
				+ //
				"	}\r\n" + //
//				"	color = vec4(texture_indices.a,texture_indices.b,texture_indices.g,1.0);\r\n" + //
				"}";
	}

	public static final class Water {
		private Water() {
		}

		public static final String vert = "#version 450 core\r\n" + //
				"\r\n" + //
				"layout (location = 0) in vec2 vPosition;\r\n" + //
				"\r\n" + //
				"layout (binding = 0) uniform sampler2D water_height_texture;\r\n" + //
				"layout (binding = 1) uniform sampler2D ground_height_texture;\r\n" + //
				"layout (binding = 2) uniform sampler2D water_exists_texture;\r\n" + //
				"layout (location = 7) uniform float centerOffsetX;\r\n" + //
				"layout (location = 8) uniform float centerOffsetY;\r\n" + //
				"\r\n" + //
				"layout (location = 0) uniform mat4 MVP;\r\n" + //
				"layout (location = 1) uniform vec4 shallow_color_min;\r\n" + //
				"layout (location = 2) uniform vec4 shallow_color_max;\r\n" + //
				"layout (location = 3) uniform vec4 deep_color_min;\r\n" + //
				"layout (location = 4) uniform vec4 deep_color_max;\r\n" + //
				"layout (location = 5) uniform float water_offset;\r\n" + //
				"\r\n" + //
				"out vec2 UV;\r\n" + //
				"out vec4 Color;\r\n" + //
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
				"	gl_Position = is_water ? MVP * vec4((vPosition.x + pos.x)*128.0 + centerOffsetX, (vPosition.y + pos.y)*128.0 + centerOffsetY, water_height*128.0, 1) : vec4(2.0, 0.0, 0.0, 1.0);\r\n"
				+ //
				"\r\n" + //
				"	UV = vec2(vPosition.x, vPosition.y);\r\n" + //
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
				" }";

		public static final String frag = "#version 450 core\r\n" + //
				"\r\n" + //
				"layout (binding = 3) uniform sampler2DArray water_textures;\r\n" + //
				"layout (binding = 2) uniform sampler2D water_exists_texture;\r\n" + //
				"\r\n" + //
				"\r\n" + //
				"layout (location = 6) uniform int current_texture;\r\n" + //
				"\r\n" + //
				"in vec2 UV;\r\n" + //
				"in vec4 Color;\r\n" + //
				"\r\n" + //
				"out vec4 outColor;\r\n" + //
				"\r\n" + //
				"void main() {\r\n" + //
				"	outColor = texture(water_textures, vec3(UV, current_texture)) * Color;\r\n" + //
				"}";
	}
}
