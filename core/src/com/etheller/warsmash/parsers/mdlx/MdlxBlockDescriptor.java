package com.etheller.warsmash.parsers.mdlx;

import com.etheller.warsmash.util.Descriptor;

public interface MdlxBlockDescriptor<E> extends Descriptor<E> {

	public static final MdlxBlockDescriptor<Attachment> ATTACHMENT = new MdlxBlockDescriptor<Attachment>() {
		@Override
		public Attachment create() {
			return new Attachment();
		}
	};

	public static final MdlxBlockDescriptor<Bone> BONE = new MdlxBlockDescriptor<Bone>() {
		@Override
		public Bone create() {
			return new Bone();
		}
	};

	public static final MdlxBlockDescriptor<Camera> CAMERA = new MdlxBlockDescriptor<Camera>() {
		@Override
		public Camera create() {
			return new Camera();
		}
	};

	public static final MdlxBlockDescriptor<CollisionShape> COLLISION_SHAPE = new MdlxBlockDescriptor<CollisionShape>() {
		@Override
		public CollisionShape create() {
			return new CollisionShape();
		}
	};

	public static final MdlxBlockDescriptor<EventObject> EVENT_OBJECT = new MdlxBlockDescriptor<EventObject>() {
		@Override
		public EventObject create() {
			return new EventObject();
		}
	};

	public static final MdlxBlockDescriptor<Geoset> GEOSET = new MdlxBlockDescriptor<Geoset>() {
		@Override
		public Geoset create() {
			return new Geoset();
		}
	};

	public static final MdlxBlockDescriptor<GeosetAnimation> GEOSET_ANIMATION = new MdlxBlockDescriptor<GeosetAnimation>() {
		@Override
		public GeosetAnimation create() {
			return new GeosetAnimation();
		}
	};

	public static final MdlxBlockDescriptor<Helper> HELPER = new MdlxBlockDescriptor<Helper>() {
		@Override
		public Helper create() {
			return new Helper();
		}
	};

	public static final MdlxBlockDescriptor<Light> LIGHT = new MdlxBlockDescriptor<Light>() {
		@Override
		public Light create() {
			return new Light();
		}
	};

	public static final MdlxBlockDescriptor<Layer> LAYER = new MdlxBlockDescriptor<Layer>() {
		@Override
		public Layer create() {
			return new Layer();
		}
	};

	public static final MdlxBlockDescriptor<Material> MATERIAL = new MdlxBlockDescriptor<Material>() {
		@Override
		public Material create() {
			return new Material();
		}
	};

	public static final MdlxBlockDescriptor<ParticleEmitter> PARTICLE_EMITTER = new MdlxBlockDescriptor<ParticleEmitter>() {
		@Override
		public ParticleEmitter create() {
			return new ParticleEmitter();
		}
	};

	public static final MdlxBlockDescriptor<ParticleEmitter2> PARTICLE_EMITTER2 = new MdlxBlockDescriptor<ParticleEmitter2>() {
		@Override
		public ParticleEmitter2 create() {
			return new ParticleEmitter2();
		}
	};

	public static final MdlxBlockDescriptor<RibbonEmitter> RIBBON_EMITTER = new MdlxBlockDescriptor<RibbonEmitter>() {
		@Override
		public RibbonEmitter create() {
			return new RibbonEmitter();
		}
	};

	public static final MdlxBlockDescriptor<Sequence> SEQUENCE = new MdlxBlockDescriptor<Sequence>() {
		@Override
		public Sequence create() {
			return new Sequence();
		}
	};

	public static final MdlxBlockDescriptor<Texture> TEXTURE = new MdlxBlockDescriptor<Texture>() {
		@Override
		public Texture create() {
			return new Texture();
		}
	};

	public static final MdlxBlockDescriptor<TextureAnimation> TEXTURE_ANIMATION = new MdlxBlockDescriptor<TextureAnimation>() {
		@Override
		public TextureAnimation create() {
			return new TextureAnimation();
		}
	};
}
