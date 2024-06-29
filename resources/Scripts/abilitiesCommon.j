// ability customization API types
type abilitytype extends handle
type orderbutton extends handle
type orderbuttontype extends handle
type abilitybehavior extends handle
type behaviorexpr extends handle
type iconui extends handle

constant native ConvertOrderButtonType takes integer x returns orderbuttontype

globals
	constant orderbuttontype ORDERBUTTON_INSTANT_NO_TARGET                 = ConvertOrderButtonType(0)
	constant orderbuttontype ORDERBUTTON_UNIT_TARGET                       = ConvertOrderButtonType(1)
	constant orderbuttontype ORDERBUTTON_POINT_TARGET                      = ConvertOrderButtonType(2)
	constant orderbuttontype ORDERBUTTON_UNIT_OR_POINT_TARGET              = ConvertOrderButtonType(3)
	constant orderbuttontype ORDERBUTTON_INSTANT_NO_TARGET_NO_INTERRUPT    = ConvertOrderButtonType(4)
	constant orderbuttontype ORDERBUTTON_PASSIVE                           = ConvertOrderButtonType(5)
	constant orderbuttontype ORDERBUTTON_MENU                              = ConvertOrderButtonType(6)
endglobals


//=====================================================
// Behavior API                                        
//=====================================================
native GetUnitMoveFollowBehavior takes unit whichUnit, integer highlightOrderId, widget whichFollowTarget returns abilitybehavior
native GetUnitMovePointBehavior takes unit whichUnit, integer highlightOrderId, real targetX, real targetY returns abilitybehavior
native GetUnitMovePointBehaviorLoc takes unit whichUnit, integer highlightOrderId, location whichLocation returns abilitybehavior
native GetUnitAttackMovePointBehavior takes unit whichUnit, real targetX, real targetY returns abilitybehavior
native GetUnitAttackMovePointBehaviorLoc takes unit whichUnit, location whichLocation returns abilitybehavior
native GetUnitAttackWidgetBehavior takes unit whichUnit, integer highlightOrderId, integer whichUnitAttackIndex, widget whichAttackTarget returns abilitybehavior
native GetUnitAttackGroundBehavior takes unit whichUnit, integer highlightOrderId, integer whichUnitAttackIndex, real attackGroundX, real attackGroundY returns abilitybehavior
native GetUnitAttackGroundBehaviorLoc takes unit whichUnit, integer highlightOrderId, integer whichUnitAttackIndex, location attackGroundLoc returns abilitybehavior
// The code func passed below must be "takes nothing returns abilitybehavior", and
// the behavior it returns is what the unit will do next after each frame of the behavior we are defining
// so it should return itself until it is finished. "How does it return itself??" you ask? Just use the
// GetBehavingBehavior() native within its callback.
native CreateAbilityBehavior takes integer highlightOrderId, code func returns abilitybehavior
// GetBehavingUnit returns the unit who owns the behavior if you use it within the handler "code func"
// of a behavior that you define
native GetBehavingUnit takes nothing returns unit
native GetBehavingBehavior takes nothing returns abilitybehavior
// this function will read from the unit's list of shift click orders they are given,
// poll the next item from that queue (modifying it) and return the top next
// item to perform. So, in your handler function to "CreateAbilityBehavior" you should
// return this when you are certain the unit has completed the ability.
native UnitPollNextOrderBehavior takes unit whichUnit returns abilitybehavior
                            
//=====================================================
// BehaviorExpr API                                        
//=====================================================
// BehaviorExpr API, this is meant to feel similar to BoolExpr.

// The code func for "CreateBehaviorExpr" should be:
//   takes nothing returns abilitybehavior
native CreateBehaviorExpr takes code func returns behaviorexpr
// Within the BehaviorExpr's callback function, you can use:
// GetSpellAbilityUnit() to retrieve the unit ("caster")
// GetSpellAbility() to retrieve the ability
native GetSpellAbilityOrderId takes nothing returns integer
// GetSpellAbilityOrderId() to retrieve the order ID causing the behavior
// GetSpellAbilityId() to retrieve the alias (rawcode) of the ability
// GetSpellTarget<thing> to retrieve the target
native GetSpellTargetType takes nothing returns orderbuttontype
// GetSpellTargetType() to retrieve the target type of the command card button

native GetSpellAbilityOrderButton takes nothing returns orderbutton

                           
//=====================================================
// AbilityType API                                        
//=====================================================
// These allow us to define a new kind of ability. The other way we could have done this
// would be if each of these were a trigger event we could register on the ability type.
// Seemed like maybe that would reduce performance and add more unnecessary plumbing, so
// for now I did not do that, though. 
native CreateAbilityType takes nothing returns abilitytype
// NOTE: Within these callbacks, use GetSpellAbilityUnit() to refer to the unit who has the ability,
// and use GetSpellAbility() to refer to the 'ability' handle if you need it.
native SetAbilityTypeOnAddAction takes abilitytype whichAbilityType, code func returns nothing
native SetAbilityTypeOnRemoveAction takes abilitytype whichAbilityType, code func returns nothing
native SetAbilityTypeOnTickAction takes abilitytype whichAbilityType, code func returns nothing
native SetAbilityTypeOnDeathAction takes abilitytype whichAbilityType, code func returns nothing
native SetAbilityTypeOnCancelFromQueueAction takes abilitytype whichAbilityType, code func returns nothing
// This condition allows us to return false and block the ability from ever interrupting unit's
// orders when clicked, such as for Berserk or Wind Walk or whatever.
// --- use GetSpellAbilityOrderId() to check the order ID of this (in case the ability type has multiple command card icons)
native SetAbilityTypeOnCheckBeforeQueueQueueCondition takes abilitytype whichAbilityType, boolexpr handler returns nothing
// The target condition allows us to return false to prevent use of the ability on certain targets.
// Use the same "GetSpellAbility<thing>" natives that you would on a behavior for this.
// If you are going to return false, you should also call DisplayAbilityTargetingError("Unable to target sappers") or whatever.
native SetAbilityTypeCheckTargetCondition takes abilitytype whichAbilityType, boolexpr handler returns nothing
native SetAbilityTypeCheckUseCondition takes abilitytype whichAbilityType, boolexpr handler returns nothing

native SetAbilityTypeEnabledWhileUnderConstruction takes abilitytype whichAbilityType, boolean enabled returns nothing
native SetAbilityTypeEnabledWhileUpgrading takes abilitytype whichAbilityType, boolean enabled returns nothing

// SetCurrentAbilityTargetingError:
// Ok so as a note, this magic native probably won't always display the error.
// The game engine asks an ability if you can target something, and other times the UI asks.
// This message is only going to show onscreen if the UI asked, but not if the internal stuff asked...
//    and it would show probably only for local player whose UI asked...
// If you want a general purpose "call this on local player machine to set the golden error message text box contents"
// function, please make that separately. This one is meant to only be used within abilitytype "code func" callbacks.
// SECOND NOTE: if you pass me a localization key (for supporting different language installations) I can probably
// give you back the correct message automatically
native SetCurrentAbilityTargetingError takes string errorMessage returns nothing

native SetAbilityTypeBehavior takes abilitytype whichAbilityType, behaviorexpr handler returns nothing

native RegisterAbilityType takes integer rawcode, abilitytype whichAbilityType returns nothing

//=====================================================
// IconUI API                                        
//=====================================================
native GetUnitIconUI takes integer unitId returns iconui
// As a note, there may be an issue with Ability IconUI objects
// because they only have level 1 tooltip on them or whatever.
// Maybe we need to manipulate that with OrderButton, or
// maybe we need to upgrade this API in the future.
native GetAbilityOnIconUI takes integer abilityId returns iconui
native GetAbilityOffIconUI takes integer abilityId returns iconui
native GetAbilityLearnIconUI takes integer abilityId returns iconui
native GetItemIconUI takes integer itemId returns iconui

//=====================================================
// OrderButton API                                        
//=====================================================
native CreateOrderButton takes orderbuttontype targetType, integer orderId, integer buttonPositionX, integer buttonPositionY returns orderbutton
native AbilityTypeAddOrderButton takes abilitytype whichAbilityType, orderbutton whichOrder returns nothing
native AbilityTypeRemoveOrderButton takes abilitytype whichAbilityType, orderbutton whichOrder returns nothing
native DestroyOrderButton takes orderbutton whichOrder returns nothing
native SetOrderButtonAutoCastOrderId takes orderbutton whichOrder, integer autoCastOrderId returns nothing
native SetOrderButtonUnAutoCastOrderId takes orderbutton whichOrder, integer autoCastOrderId returns nothing
// if you set container id, it would only show if you also added a menu-type icon
native SetOrderButtonContainerMenuOrderId takes orderbutton whichOrder, integer containerMenuOrderId returns nothing
native SetOrderButtonDisabled takes orderbutton whichOrder, boolean disabled returns nothing
native SetOrderButtonManaCost takes orderbutton whichOrder, integer costAmount returns nothing
native SetOrderButtonGoldCost takes orderbutton whichOrder, integer costAmount returns nothing
native SetOrderButtonLumberCost takes orderbutton whichOrder, integer costAmount returns nothing
native SetOrderButtonFoodCostDisplayOnly takes orderbutton whichOrder, integer costAmount returns nothing
native SetOrderButtonCharges takes orderbutton whichOrder, integer charges returns nothing
native SetOrderButtonAutoCastActive takes orderbutton whichOrder, boolean active returns nothing
native SetOrderButtonHidden takes orderbutton whichOrder, boolean hidden returns nothing
native SetOrderButtonIconPath takes orderbutton whichOrder, string iconPath returns nothing
native SetOrderButtonButtonPositionX takes orderbutton whichOrder, integer buttonPositionX returns nothing
native SetOrderButtonButtonPositionY takes orderbutton whichOrder, integer buttonPositionY returns nothing
native SetOrderButtonToolTip takes orderbutton whichOrder, string tip returns nothing
native SetOrderButtonUberTip takes orderbutton whichOrder, string uberTip returns nothing
// NOTE: On 1.31+, we should probably change SetHotKey to take one of the OSKEY constants
native SetOrderButtonHotKey takes orderbutton whichOrder, string hotkey returns nothing
// NOTE: below is intended for buildings, root, build tiny, etc and will show a freeze frame of the stand animation of the
// model at the given modelPath

// TODO choosing pathing map and target mouse model both were discontinued because it couldnt support
// "Is Can Build On" world editor nonsense. Do we want to bring them back later? Maybe only the model path?
//native SetOrderButtonTargetingMouseModel takes orderbutton whichOrder, string modelPath returns nothing
//native SetOrderButtonTargetPathingMap takes orderbutton whichOrder, string pathingMapFilePath returns nothing
native SetOrderButtonPreviewBuildUnitId takes orderbutton whichOrder, integer unitId returns nothing
native SetOrderButtonAOE takes orderbutton whichOrder, real radius returns nothing

native GetOrderButtonAutoCastOrderId takes orderbutton whichOrder returns integer
native GetOrderButtonUnAutoCastOrderId takes orderbutton whichOrder returns integer
native GetOrderButtonContainerMenuOrderId takes orderbutton whichOrder returns integer
native IsOrderButtonDisabled takes orderbutton whichOrder returns boolean
native GetOrderButtonManaCost takes orderbutton whichOrder returns integer
native GetOrderButtonGoldCost takes orderbutton whichOrder returns integer
native GetOrderButtonLumberCost takes orderbutton whichOrder returns integer
native GetOrderButtonFoodCostDisplayOnly takes orderbutton whichOrder returns integer
native GetOrderButtonCharges takes orderbutton whichOrder returns integer
native IsOrderButtonAutoCastActive takes orderbutton whichOrder returns boolean
native IsOrderButtonHidden takes orderbutton whichOrder returns boolean
native GetOrderButtonIconPath takes orderbutton whichOrder returns string
native GetOrderButtonButtonPositionX takes orderbutton whichOrder returns integer
native GetOrderButtonButtonPositionY takes orderbutton whichOrder returns integer
native GetOrderButtonToolTip takes orderbutton whichOrder returns string
native GetOrderButtonUberTip takes orderbutton whichOrder returns string
// NOTE: On 1.31+, we should probably change GetHotKey to return one of the OSKEY constants
native GetOrderButtonHotKey takes orderbutton whichOrder returns string

//native GetOrderButtonTargetingMouseModel takes orderbutton whichOrder returns string
//native GetOrderButtonTargetPathingMap takes orderbutton whichOrder returns string
native GetOrderButtonPreviewBuildUnitId takes orderbutton whichOrder returns integer
native GetOrderButtonAOE takes orderbutton whichOrder returns real


native GetSpellAbilityType takes nothing returns abilitytype

    
//=====================================================
// Ability API    
//=====================================================
// NOTE: GetAbilityType will return null for engine-level abilities, unless if those are some day
// all replaced with userspace implementations
native GetAbilityType takes ability whichAbility returns abilitytype
