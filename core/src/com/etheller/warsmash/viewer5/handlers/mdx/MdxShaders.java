package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.Shaders;

public class MdxShaders {
	public static final String vsHd = Shaders.boneTexture + "\r\n" + //
			"    uniform mat4 u_mvp;\r\n" + //
			"    uniform float u_layerAlpha;\r\n" + //
			"    attribute vec3 a_position;\r\n" + //
			"    attribute vec3 a_normal;\r\n" + //
			"    attribute vec2 a_uv;\r\n" + //
			"    attribute vec4 a_bones;\r\n" + //
			"    attribute vec4 a_weights;\r\n" + //
			"    varying vec3 v_normal;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    varying float v_layerAlpha;\r\n" + //
			"    void transform(inout vec3 position, inout vec3 normal) {\r\n" + //
			"      mat4 bone;\r\n" + //
			"      bone += fetchMatrix(a_bones[0], 0.0) * a_weights[0];\r\n" + //
			"      bone += fetchMatrix(a_bones[1], 0.0) * a_weights[1];\r\n" + //
			"      bone += fetchMatrix(a_bones[2], 0.0) * a_weights[2];\r\n" + //
			"      bone += fetchMatrix(a_bones[3], 0.0) * a_weights[3];\r\n" + //
			"      position = vec3(bone * vec4(position, 1.0));\r\n" + //
			"      normal = mat3(bone) * normal;\r\n" + //
			"    }\r\n" + //
			"    void main() {\r\n" + //
			"      vec3 position = a_position;\r\n" + //
			"      vec3 normal = a_normal;\r\n" + //
			"      transform(position, normal);\r\n" + //
			"      v_normal = normal;\r\n" + //
			"      v_uv = a_uv;\r\n" + //
			"      v_layerAlpha = u_layerAlpha;\r\n" + //
			"      gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
			"    }";

	public static final String fsHd = "\r\n" + //
			"    uniform sampler2D u_diffuseMap;\r\n" + //
			"    uniform sampler2D u_ormMap;\r\n" + //
			"    uniform sampler2D u_teamColorMap;\r\n" + //
			"    uniform float u_filterMode;\r\n" + //
			"    varying vec3 v_normal;\r\n" + //
			"    varying vec2 v_uv;\r\n" + //
			"    varying float v_layerAlpha;\r\n" + //
			"    void main() {\r\n" + //
			"      vec4 texel = texture2D(u_diffuseMap, v_uv);\r\n" + //
			"      vec4 color = vec4(texel.rgb, texel.a * v_layerAlpha);\r\n" + //
			"      vec4 orma = texture2D(u_ormMap, v_uv);\r\n" + //
			"      if (orma.a > 0.1) {\r\n" + //
			"        color *= texture2D(u_teamColorMap, v_uv) * orma.a;\r\n" + //
			"      }\r\n" + //
			"      // 1bit Alpha\r\n" + //
			"      if (u_filterMode == 1.0 && color.a < 0.75) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			"      gl_FragColor = color;\r\n" + //
			"    }";

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
				"      return 0.0;\r\n" + //
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
				"          vertices[2] = - vx - vy;\r\n" + //
				"          vertices[1] = vx - vy;\r\n" + //
				"          vertices[0] = -vertices[2];\r\n" + //
				"          vertices[3] = -vertices[1];\r\n" + //
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
				"      v_texcoord[0] /= u_columns;\r\n" + //
				"      v_texcoord[1] /= u_rows;\r\n" + //
				"      v_color = mix(u_colors[index], u_colors[index + 1], factor) / 255.0;\r\n" + //
				"      gl_Position = u_mvp * vec4(position, 1.0);\r\n" + //
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
			"    void main() {\r\n" + //
			"      vec4 texel = texture2D(u_texture, v_texcoord);\r\n" + //
			"      vec4 color = texel * v_color;\r\n" + //
			"      // 1bit Alpha, used by ribbon emitters.\r\n" + //
			"      if (u_emitter == EMITTER_RIBBON && u_filterMode == 1.0 && color.a < 0.75) {\r\n" + //
			"        discard;\r\n" + //
			"      }\r\n" + //
			"      gl_FragColor = color;\r\n" + //
			"    }";
}
