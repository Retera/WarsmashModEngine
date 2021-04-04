package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.util.Descriptor;

public interface MdlxBlockDescriptor<E> extends Descriptor<E> {

	MdlxBlockDescriptor<MdlxAttachment> ATTACHMENT = MdlxAttachment::new;

	MdlxBlockDescriptor<MdlxBone> BONE = MdlxBone::new;

	MdlxBlockDescriptor<MdlxCamera> CAMERA = MdlxCamera::new;

	MdlxBlockDescriptor<MdlxCollisionShape> COLLISION_SHAPE = MdlxCollisionShape::new;

	MdlxBlockDescriptor<MdlxEventObject> EVENT_OBJECT = MdlxEventObject::new;

	MdlxBlockDescriptor<MdlxGeoset> GEOSET = MdlxGeoset::new;

	MdlxBlockDescriptor<MdlxGeosetAnimation> GEOSET_ANIMATION = MdlxGeosetAnimation::new;

	MdlxBlockDescriptor<MdlxHelper> HELPER = MdlxHelper::new;

	MdlxBlockDescriptor<MdlxLight> LIGHT = MdlxLight::new;

	MdlxBlockDescriptor<MdlxLayer> LAYER = MdlxLayer::new;

	MdlxBlockDescriptor<MdlxMaterial> MATERIAL = MdlxMaterial::new;

	MdlxBlockDescriptor<MdlxParticleEmitter> PARTICLE_EMITTER = MdlxParticleEmitter::new;

	MdlxBlockDescriptor<MdlxParticleEmitter2> PARTICLE_EMITTER2 = MdlxParticleEmitter2::new;

	MdlxBlockDescriptor<MdlxParticleEmitterPopcorn> PARTICLE_EMITTER_POPCORN = MdlxParticleEmitterPopcorn::new;

	MdlxBlockDescriptor<MdlxRibbonEmitter> RIBBON_EMITTER = MdlxRibbonEmitter::new;

	MdlxBlockDescriptor<MdlxSequence> SEQUENCE = MdlxSequence::new;

	MdlxBlockDescriptor<MdlxTexture> TEXTURE = MdlxTexture::new;

	MdlxBlockDescriptor<MdlxTextureAnimation> TEXTURE_ANIMATION = MdlxTextureAnimation::new;

	MdlxBlockDescriptor<MdlxFaceEffect> FACE_EFFECT = MdlxFaceEffect::new;
}
