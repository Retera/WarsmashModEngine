uniform sampler2D u_boneMap;
uniform float u_vectorSize;
uniform float u_rowSize;
mat4 fetchMatrix(float column, float row) {
  column *= u_vectorSize * 4.0;
  row *= u_rowSize;
  // Add in half texel to sample in the middle of the texel.
  // Otherwise, since the sample is directly on the boundry, small floating point errors can cause the sample to get the wrong pixel.
  // This is mostly noticable with NPOT textures, which the bone maps are.
  column += 0.5 * u_vectorSize;
  row += 0.5 * u_rowSize;
  return mat4(texture2D(u_boneMap, vec2(column, row)),
              texture2D(u_boneMap, vec2(column + u_vectorSize, row)),
              texture2D(u_boneMap, vec2(column + u_vectorSize * 2.0, row)),
              texture2D(u_boneMap, vec2(column + u_vectorSize * 3.0, row)));
}