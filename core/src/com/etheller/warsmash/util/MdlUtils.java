package com.etheller.warsmash.util;

/**
 * Constants for the tokens were used to prevent typos in token literals. It
 * would be very easy for me to type "Interval" in one place and "Intreval" in
 * another by mistake. With this paradigm, that mistake causes a compile error,
 * since TOKEN_INTREVAL does not exist.
 */
public class MdlUtils {
	public static final String TOKEN_VERSION = "Version";

	public static final String TOKEN_MODEL = "Model";

	public static final String TOKEN_SEQUENCES = "Sequences";

	public static final String TOKEN_GLOBAL_SEQUENCES = "GlobalSequences";

	public static final String TOKEN_INTERVAL = "Interval";
	public static final String TOKEN_NONLOOPING = "NonLooping";
	public static final String TOKEN_MOVESPEED = "MoveSpeed";
	public static final String TOKEN_RARITY = "Rarity";

	public static final String TOKEN_FORMAT_VERSION = "FormatVersion";
	public static final String TOKEN_BLEND_TIME = "BlendTime";
	public static final String TOKEN_DURATION = "Duration";

	public static final String TOKEN_IMAGE = "Image";
	public static final String TOKEN_WRAP_WIDTH = "WrapWidth";
	public static final String TOKEN_WRAP_HEIGHT = "WrapHeight";
	public static final String TOKEN_BITMAP = "Bitmap";

	public static final String TOKEN_TVERTEX_ANIM_SPACE = "TVertexAnim ";
	public static final String TOKEN_TVERTEX_ANIM = "TVertexAnim";

	public static final String TOKEN_DONT_INTERP = "DontInterp";
	public static final String TOKEN_LINEAR = "Linear";
	public static final String TOKEN_HERMITE = "Hermite";
	public static final String TOKEN_BEZIER = "Bezier";
	public static final String TOKEN_GLOBAL_SEQ_ID = "GlobalSeqId";

	public static final String TOKEN_PLANE = "Plane";
	public static final String TOKEN_BOX = "Box";
	public static final String TOKEN_SPHERE = "Sphere";
	public static final String TOKEN_CYLINDER = "Cylinder";

	public static final String TOKEN_GEOSETID = "GeosetId";
	public static final String TOKEN_MULTIPLE = "Multiple";
	public static final String TOKEN_GEOSETANIMID = "GeosetAnimId";
	public static final String TOKEN_NONE = "None";
	public static final String TOKEN_OBJECTID = "ObjectId";
	public static final String TOKEN_PARENT = "Parent";
	public static final String TOKEN_BILLBOARDED_LOCK_Z = "BillboardedLockZ";
	public static final String TOKEN_BILLBOARDED_LOCK_Y = "BillboardedLockY";
	public static final String TOKEN_BILLBOARDED_LOCK_X = "BillboardedLockX";
	public static final String TOKEN_BILLBOARDED = "Billboarded";
	public static final String TOKEN_CAMERA_ANCHORED = "CameraAnchored";
	public static final String TOKEN_DONT_INHERIT = "DontInherit";
	public static final String TOKEN_ROTATION = "Rotation";
	public static final String TOKEN_TRANSLATION = "Translation";
	public static final String TOKEN_SCALING = "Scaling";
	public static final String TOKEN_STATIC = "static";
	public static final String TOKEN_ATTACHMENT_ID = "AttachmentID";
	public static final String TOKEN_PATH = "Path";
	public static final String TOKEN_VISIBILITY = "Visibility";
	public static final String TOKEN_POSITION = "Position";
	public static final String TOKEN_FIELDOFVIEW = "FieldOfView";
	public static final String TOKEN_FARCLIP = "FarClip";
	public static final String TOKEN_NEARCLIP = "NearClip";
	public static final String TOKEN_TARGET = "Target";
	public static final String TOKEN_VERTICES = "Vertices";
	public static final String TOKEN_BOUNDSRADIUS = "BoundsRadius";
	public static final String TOKEN_EVENT_TRACK = "EventTrack";
	public static final String TOKEN_MAXIMUM_EXTENT = "MaximumExtent";
	public static final String TOKEN_MINIMUM_EXTENT = "MinimumExtent";
	public static final String TOKEN_NORMALS = "Normals";
	public static final String TOKEN_TVERTICES = "TVertices";
	public static final String TOKEN_VERTEX_GROUP = "VertexGroup";
	public static final String TOKEN_FACES = "Faces";
	public static final String TOKEN_GROUPS = "Groups";
	public static final String TOKEN_ANIM = "Anim";
	public static final String TOKEN_MATERIAL_ID = "MaterialID";
	public static final String TOKEN_SELECTION_GROUP = "SelectionGroup";
	public static final String TOKEN_UNSELECTABLE = "Unselectable";
	public static final String TOKEN_TRIANGLES = "Triangles";
	public static final String TOKEN_MATRICES = "Matrices";
	public static final String TOKEN_DROP_SHADOW = "DropShadow";
	public static final String TOKEN_ALPHA = "Alpha";
	public static final String TOKEN_COLOR = "Color";
	public static final String TOKEN_STATIC_ALPHA = TOKEN_STATIC + " " + TOKEN_ALPHA;
	public static final String TOKEN_STATIC_COLOR = TOKEN_STATIC + " " + TOKEN_COLOR;
	public static final String TOKEN_FILTER_MODE = "FilterMode";
	public static final String TOKEN_UNSHADED = "Unshaded";
	public static final String TOKEN_SPHERE_ENV_MAP = "SphereEnvMap";
	public static final String TOKEN_TWO_SIDED = "TwoSided";
	public static final String TOKEN_UNFOGGED = "Unfogged";
	public static final String TOKEN_NO_DEPTH_TEST = "NoDepthTest";
	public static final String TOKEN_NO_DEPTH_SET = "NoDepthSet";
	public static final String TOKEN_TEXTURE_ID = "TextureID";
	public static final String TOKEN_STATIC_TEXTURE_ID = TOKEN_STATIC + " " + TOKEN_TEXTURE_ID;
	public static final String TOKEN_TVERTEX_ANIM_ID = "TVertexAnimId";
	public static final String TOKEN_COORD_ID = "CoordId";

	public static final String TOKEN_OMNIDIRECTIONAL = "Omnidirectional";
	public static final String TOKEN_DIRECTIONAL = "Directional";
	public static final String TOKEN_AMBIENT = "Ambient";
	public static final String TOKEN_ATTENUATION_START = "AttenuationStart";
	public static final String TOKEN_STATIC_ATTENUATION_START = TOKEN_STATIC + " " + TOKEN_ATTENUATION_START;
	public static final String TOKEN_ATTENUATION_END = "AttenuationEnd";
	public static final String TOKEN_STATIC_ATTENUATION_END = TOKEN_STATIC + " " + TOKEN_ATTENUATION_END;
	public static final String TOKEN_INTENSITY = "Intensity";
	public static final String TOKEN_STATIC_INTENSITY = TOKEN_STATIC + " " + TOKEN_INTENSITY;
	public static final String TOKEN_AMB_INTENSITY = "AmbIntensity";
	public static final String TOKEN_STATIC_AMB_INTENSITY = TOKEN_STATIC + " " + TOKEN_AMB_INTENSITY;
	public static final String TOKEN_AMB_COLOR = "AmbColor";
	public static final String TOKEN_STATIC_AMB_COLOR = TOKEN_STATIC + " " + TOKEN_AMB_COLOR;

	public static final String TOKEN_CONSTANT_COLOR = "ConstantColor";
	public static final String TOKEN_SORT_PRIMS_NEAR_Z = "SortPrimsNearZ";
	public static final String TOKEN_SORT_PRIMS_FAR_Z = "SortPrimsFarZ";
	public static final String TOKEN_FULL_RESOLUTION = "FullResolution";
	public static final String TOKEN_PRIORITY_PLANE = "PriorityPlane";

	public static final String TOKEN_EMITTER_USES_MDL = "EmitterUsesMDL";
	public static final String TOKEN_EMITTER_USES_TGA = "EmitterUsesTGA";
	public static final String TOKEN_EMISSION_RATE = "EmissionRate";
	public static final String TOKEN_STATIC_EMISSION_RATE = TOKEN_STATIC + " " + TOKEN_EMISSION_RATE;
	public static final String TOKEN_GRAVITY = "Gravity";
	public static final String TOKEN_STATIC_GRAVITY = TOKEN_STATIC + " " + TOKEN_GRAVITY;
	public static final String TOKEN_LONGITUDE = "Longitude";
	public static final String TOKEN_STATIC_LONGITUDE = TOKEN_STATIC + " " + TOKEN_LONGITUDE;
	public static final String TOKEN_LATITUDE = "Latitude";
	public static final String TOKEN_STATIC_LATITUDE = TOKEN_STATIC + " " + TOKEN_LATITUDE;
	public static final String TOKEN_PARTICLE = "Particle";
	public static final String TOKEN_LIFE_SPAN = "LifeSpan";
	public static final String TOKEN_STATIC_LIFE_SPAN = TOKEN_STATIC + " " + TOKEN_LIFE_SPAN;
	public static final String TOKEN_INIT_VELOCITY = "InitVelocity";
	public static final String TOKEN_STATIC_INIT_VELOCITY = TOKEN_STATIC + " " + TOKEN_INIT_VELOCITY;

	public static final String TOKEN_LINE_EMITTER = "LineEmitter";
	public static final String TOKEN_MODEL_SPACE = "ModelSpace";
	public static final String TOKEN_XY_QUAD = "XYQuad";
	public static final String TOKEN_SPEED = "Speed";
	public static final String TOKEN_STATIC_SPEED = TOKEN_STATIC + " " + TOKEN_SPEED;
	public static final String TOKEN_VARIATION = "Variation";
	public static final String TOKEN_STATIC_VARIATION = TOKEN_STATIC + " " + TOKEN_VARIATION;
	public static final String TOKEN_SQUIRT = "Squirt";
	public static final String TOKEN_WIDTH = "Width";
	public static final String TOKEN_STATIC_WIDTH = TOKEN_STATIC + " " + TOKEN_WIDTH;
	public static final String TOKEN_LENGTH = "Length";
	public static final String TOKEN_STATIC_LENGTH = TOKEN_STATIC + " " + TOKEN_LENGTH;
	public static final String TOKEN_ROWS = "Rows";
	public static final String TOKEN_COLUMNS = "Columns";
	public static final String TOKEN_HEAD = "Head";
	public static final String TOKEN_TAIL = "Tail";
	public static final String TOKEN_BOTH = "Both";
	public static final String TOKEN_TAIL_LENGTH = "TailLength";
	public static final String TOKEN_TIME = "Time";
	public static final String TOKEN_SEGMENT_COLOR = "SegmentColor";
	public static final String TOKEN_PARTICLE_SCALING = "ParticleScaling";
	public static final String TOKEN_LIFE_SPAN_UV_ANIM = "LifeSpanUVAnim";
	public static final String TOKEN_DECAY_UV_ANIM = "DecayUVAnim";
	public static final String TOKEN_TAIL_UV_ANIM = "TailUVAnim";
	public static final String TOKEN_TAIL_DECAY_UV_ANIM = "TailDecayUVAnim";
	public static final String TOKEN_REPLACEABLE_ID = "ReplaceableId";
	public static final String TOKEN_BLEND = "Blend";// ParticleEmitter2.FilterMode.BLEND.getMdlText();
	public static final String TOKEN_ADDITIVE = "Additive";// ParticleEmitter2.FilterMode.ADDITIVE.getMdlText();
	public static final String TOKEN_MODULATE = "Modulate";// ParticleEmitter2.FilterMode.MODULATE.getMdlText();
	public static final String TOKEN_MODULATE2X = "Modulate2x";// ParticleEmitter2.FilterMode.MODULATE2X.getMdlText();
	public static final String TOKEN_ALPHAKEY = "AlphaKey";// ParticleEmitter2.FilterMode.ALPHAKEY.getMdlText();

	public static final String TOKEN_HEIGHT_ABOVE = "HeightAbove";
	public static final String TOKEN_STATIC_HEIGHT_ABOVE = TOKEN_STATIC + " " + TOKEN_HEIGHT_ABOVE;
	public static final String TOKEN_HEIGHT_BELOW = "HeightBelow";
	public static final String TOKEN_STATIC_HEIGHT_BELOW = TOKEN_STATIC + " " + TOKEN_HEIGHT_BELOW;
	public static final String TOKEN_TEXTURE_SLOT = "TextureSlot";
	public static final String TOKEN_STATIC_TEXTURE_SLOT = TOKEN_STATIC + " " + TOKEN_TEXTURE_SLOT;

	public static final String TOKEN_TEXTURES = "Textures";
	public static final String TOKEN_MATERIALS = "Materials";
	public static final String TOKEN_TEXTURE_ANIMS = "TextureAnims";
	public static final String TOKEN_PIVOT_POINTS = "PivotPoints";

	public static final String TOKEN_ATTACHMENT = "Attachment";
	public static final String TOKEN_BONE = "Bone";
	public static final String TOKEN_CAMERA = "Camera";
	public static final String TOKEN_COLLISION_SHAPE = "CollisionShape";
	public static final String TOKEN_EVENT_OBJECT = "EventObject";
	public static final String TOKEN_GEOSET = "Geoset";
	public static final String TOKEN_GEOSETANIM = "GeosetAnim";
	public static final String TOKEN_HELPER = "Helper";
	public static final String TOKEN_LAYER = "Layer";
	public static final String TOKEN_LIGHT = "Light";
	public static final String TOKEN_MATERIAL = "Material";
	public static final String TOKEN_PARTICLE_EMITTER = "ParticleEmitter";
	public static final String TOKEN_PARTICLE_EMITTER2 = "ParticleEmitter2";
	public static final String TOKEN_RIBBON_EMITTER = "RibbonEmitter";

}
