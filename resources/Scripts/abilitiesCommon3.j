
struct Projectile extends projectile

endstruct

struct AbilitySpellBase extends ability
    integer manaCost

	static method create takes integer alias returns AbilitySpellBase
		AbilitySpellBase this = .allocate(alias)
		call OnAbility
		return this
	endmethod
	
	// interface method
	method populate takes gameobject worldEditorAbility, integer level returns nothing
		
	endmethod
endstruct

interface Buff extends buff
    method onAdd takes unit target returns nothing
    method onRemove takes unit target returns nothing
    method onDeath takes unit target returns nothing
endinterface

// NOTE: almost a direct port of CBuffTimed from Java
struct BuffTimed extends Buff
    effect fx
    real duration
    integer expireTick
        
    method endmethod 
endstruct

struct BuffStun extends BuffTimed
	// maybe a create method

	method onAdd takes unit target returns nothing
		call SetUnitStunned(target, true)
	endmethod

	method onRemove takes unit target returns nothing
		call SetUnitStunned(target, false)
	endmethod
endstruct
