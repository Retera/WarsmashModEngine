package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.util.WarsmashConstants;

/**
 * Thanks to the Wurst guys for this list of ids, taken from this link:
 * https://github.com/wurstscript/WurstStdlib2/blob/master/wurst/_wurst/assets/Orders.wurst
 *
 * The original code ported to create this Java file is licensed under the
 * Apache License; you can read more at the link above.
 *
 */
/**
 * The list was updated with WaterKnights list of order ids from the following link:
 * https://www.hiveworkshop.com/threads/list-of-order-ids.350361/
 * https://docs.google.com/spreadsheets/d/10hNbIwFr4-y90pS5R9olWNT6lmD8-iZcBOafck58Xm4/edit#gid=0
 *
 * WaterKnight does not provide a license on the list, but the list was adapted to Java as part of including it
 *
 */
public class OrderIds {
	;/**
		 * This is an order with no target that opens up the build menu of a unit that
		 * can build structures.
		 */
	;
	public static final int buildmenu = 851994;
	/**
	 * 851976 (cancel): This is an order with no target that is like a click on a
	 * cancel button. We used to be able to catch cancel clicks with this id back
	 * then but this id doesn't seem to work any more.
	 */
	public static final int cancel = 851976;
	/**
	 * An item targeted order that move the target item to a certain inventory slot
	 * of the ordered hero.
	 */
	public static final int itemdrag00 = WarsmashConstants.USE_NINE_ITEM_INVENTORY ? 85200200 : 852002;
	/**
	 * An item targeted order that move the target item to a certain inventory slot
	 * of the ordered hero.
	 */
	public static final int itemdrag01 = 852003;
	/**
	 * An item targeted order that move the target item to a certain inventory slot
	 * of the ordered hero.
	 */
	public static final int itemdrag02 = 852004;
	/**
	 * An item targeted order that move the target item to a certain inventory slot
	 * of the ordered hero.
	 */
	public static final int itemdrag03 = 852005;
	/**
	 * An item targeted order that move the target item to a certain inventory slot
	 * of the ordered hero.
	 */
	public static final int itemdrag04 = 852006;
	/**
	 * An item targeted order that move the target item to a certain inventory slot
	 * of the ordered hero.
	 */
	public static final int itemdrag05 = WarsmashConstants.USE_NINE_ITEM_INVENTORY ? 85200700 : 852007;
	/**
	 * An order that will make the ordered hero use the item in a certain inventory
	 * slot. If it's an order with no target or object or point targeted depends on
	 * the type of item.
	 */
	public static final int itemuse00 = WarsmashConstants.USE_NINE_ITEM_INVENTORY ? 85200800 : 852008;
	/**
	 * An order that will make the ordered hero use the item in a certain inventory
	 * slot. If it's an order with no target or object or point targeted depends on
	 * the type of item.
	 */
	public static final int itemuse01 = 852009;
	/**
	 * An order that will make the ordered hero use the item in a certain inventory
	 * slot. If it's an order with no target or object or point targeted depends on
	 * the type of item.
	 */
	public static final int itemuse02 = 852010;
	/**
	 * An order that will make the ordered hero use the item in a certain inventory
	 * slot. If it's an order with no target or object or point targeted depends on
	 * the type of item.
	 */
	public static final int itemuse03 = 852011;
	/**
	 * An order that will make the ordered hero use the item in a certain inventory
	 * slot. If it's an order with no target or object or point targeted depends on
	 * the type of item.
	 */
	public static final int itemuse04 = 852012;
	/**
	 * An order that will make the ordered hero use the item in a certain inventory
	 * slot. If it's an order with no target or object or point targeted depends on
	 * the type of item.
	 */
	public static final int itemuse05 = WarsmashConstants.USE_NINE_ITEM_INVENTORY ? 85201300 : 852013;
	/**
	 * Order for AIaa ability, which blizzard made for tome of attack, but never
	 * used it. But it can actually change caster's base attack!
	 */
	public static final int tomeOfAttack = 852259;
	/** This is a point or object targeted order that is like a right click. */
	public static final int smart = 851971;
	/**
	 * This is an order with no target that opens the skill menu of heroes. If it is
	 * issued for a normal unit with triggers it will black out the command card for
	 * this unit, the command card will revert to normal after reselecting the unit.
	 */
	public static final int skillmenu = 852000;
	/**
	 * This order is issued to units that get stunned by a spell, for example War
	 * Stomp (AOws). This is probably a hold position + hold fire order. The ordered
	 * unit will be unable to move and attack.
	 */
	public static final int stunned = 851973;
	public static final int wandOfIllusion = 852274;
	public static final int absorb = 852529;
	public static final int acidbomb = 852662;
	public static final int acolyteharvest = 852185;
	public static final int ambush = 852131;
	public static final int ancestralspirit = 852490;
	public static final int ancestralspirittarget = 852491;
	public static final int animatedead = 852217;
	public static final int antimagicshell = 852186;
	public static final int attack = 851983;
	public static final int attackground = 851984;
	public static final int attackonce = 851985;
	public static final int attributemodskill = 852576;
	public static final int auraunholy = 852215;
	public static final int auravampiric = 852216;
	public static final int autodispel = 852132;
	public static final int autodispeloff = 852134;
	public static final int autodispelon = 852133;
	public static final int autoentangle = 852505;
	public static final int autoentangleinstant = 852506;
	public static final int autoharvestgold = 852021;
	public static final int autoharvestlumber = 852022;
	public static final int avatar = 852086;
	public static final int avengerform = 852531;
	public static final int awaken = 852466;
	public static final int banish = 852486;
	public static final int barkskin = 852135;
	public static final int barkskinoff = 852137;
	public static final int barkskinon = 852136;
	public static final int battleroar = 852599;
	public static final int battlestations = 852099;
	public static final int bearform = 852138;
	public static final int berserk = 852100;
	public static final int blackarrow = 852577;
	public static final int blackarrowoff = 852579;
	public static final int blackarrowon = 852578;
	public static final int blight = 852187;
	public static final int blink = 852525;
	public static final int blizzard = 852089;
	public static final int bloodlust = 852101;
	public static final int bloodlustoff = 852103;
	public static final int bloodluston = 852102;
	public static final int board = 852043;
	public static final int breathoffire = 852580;
	public static final int breathoffrost = 852560;
	public static final int build = 851994;
	public static final int burrow = 852533;
	public static final int cannibalize = 852188;
	public static final int carrionscarabs = 852551;
	public static final int carrionscarabsinstant = 852554;
	public static final int carrionscarabsoff = 852553;
	public static final int carrionscarabson = 852552;
	public static final int carrionswarm = 852218;
	public static final int chainlightning = 852119;
	public static final int channel = 852600;
	public static final int charm = 852581;
	public static final int chemicalrage = 852663;
	public static final int cloudoffog = 852473;
	public static final int clusterrockets = 852652;
	public static final int coldarrows = 852244;
	public static final int coldarrowstarg = 852243;
	public static final int controlmagic = 852474;
	public static final int corporealform = 852493;
	public static final int corrosivebreath = 852140;
	public static final int coupleinstant = 852508;
	public static final int coupletarget = 852507;
	public static final int creepanimatedead = 852246;
	public static final int creepdevour = 852247;
	public static final int creepheal = 852248;
	public static final int creephealoff = 852250;
	public static final int creephealon = 852249;
	public static final int creepthunderbolt = 852252;
	public static final int creepthunderclap = 852253;
	public static final int cripple = 852189;
	public static final int curse = 852190;
	public static final int curseoff = 852192;
	public static final int curseon = 852191;
	public static final int cyclone = 852144;
	public static final int darkconversion = 852228;
	public static final int darkportal = 852229;
	public static final int darkritual = 852219;
	public static final int darksummoning = 852220;
	public static final int deathanddecay = 852221;
	public static final int deathcoil = 852222;
	public static final int deathpact = 852223;
	public static final int decouple = 852509;
	public static final int defend = 852055;
	public static final int detectaoe = 852015;
	public static final int detonate = 852145;
	public static final int devour = 852104;
	public static final int devourmagic = 852536;
	public static final int disassociate = 852240;
	public static final int disenchant = 852495;
	public static final int dismount = 852470;
	public static final int dispel = 852057;
	public static final int divineshield = 852090;
	public static final int doom = 852583;
	public static final int drain = 852487;
	public static final int dreadlordinferno = 852224;
	public static final int dropitem = 852001;
	public static final int drunkenhaze = 852585;
	public static final int earthquake = 852121;
	public static final int eattree = 852146;
	public static final int elementalfury = 852586;
	public static final int ensnare = 852106;
	public static final int ensnareoff = 852108;
	public static final int ensnareon = 852107;
	public static final int entangle = 852147;
	public static final int entangleinstant = 852148;
	public static final int entanglingroots = 852171;
	public static final int etherealform = 852496;
	public static final int evileye = 852105;
	public static final int faeriefire = 852149;
	public static final int faeriefireoff = 852151;
	public static final int faeriefireon = 852150;
	public static final int fanofknives = 852526;
	public static final int farsight = 852122;
	public static final int fingerofdeath = 852230;
	public static final int firebolt = 852231;
	public static final int flamestrike = 852488;
	public static final int flamingarrows = 852174;
	public static final int flamingarrowstarg = 852173;
	public static final int flamingattack = 852540;
	public static final int flamingattacktarg = 852539;
	public static final int flare = 852060;
	public static final int forceboard = 852044;
	public static final int forceofnature = 852176;
	public static final int forkedlightning = 852587;
	public static final int freezingbreath = 852195;
	public static final int frenzy = 852561;
	public static final int frenzyoff = 852563;
	public static final int frenzyon = 852562;
	public static final int frostarmor = 852225;
	public static final int frostarmoroff = 852459;
	public static final int frostarmoron = 852458;
	public static final int frostnova = 852226;
	public static final int getitem = 851981;
	public static final int gold2lumber = 852233;
	public static final int grabtree = 852511;
	public static final int harvest = 852018;
	public static final int heal = 852063;
	public static final int healingspray = 852664;
	public static final int healingward = 852109;
	public static final int healingwave = 852501;
	public static final int healoff = 852065;
	public static final int healon = 852064;
	public static final int hex = 852502;
	public static final int holdposition = 851993;
	public static final int holybolt = 852092;
	public static final int howlofterror = 852588;
	public static final int humanbuild = 851995;
	public static final int immolation = 852177;
	public static final int impale = 852555;
	public static final int incineratearrow = 852670;
	public static final int incineratearrowoff = 852672;
	public static final int incineratearrowon = 852671;
	public static final int inferno = 852232;
	public static final int innerfire = 852066;
	public static final int innerfireoff = 852068;
	public static final int innerfireon = 852067;
	public static final int instant = 852200;
	public static final int invisibility = 852069;
	public static final int lavamonster = 852667;
	public static final int lightningshield = 852110;
	public static final int load = 852046;
	public static final int loadarcher = 852142;
	public static final int loadcorpse = 852050;
	public static final int loadcorpseon = 852051; //loadcorpse on (Amel) -- from WaterKnight
	public static final int loadcorpseoff = 852052; //loadcorpse off (Amel) -- from WaterKnight
	public static final int loadcorpseinstant = 852053;
	public static final int locustswarm = 852556;
	public static final int lumber2gold = 852234;
	public static final int magicdefense = 852478;
	public static final int magicleash = 852480;
	public static final int magicundefense = 852479;
	public static final int manaburn = 852179;
	public static final int manaflareoff = 852513;
	public static final int manaflareon = 852512;
	public static final int manashieldoff = 852590;
	public static final int manashieldon = 852589;
	public static final int massteleport = 852093;
	public static final int mechanicalcritter = 852564;
	public static final int metamorphosis = 852180;
	public static final int militia = 852072;
	public static final int militiaconvert = 852071;
	public static final int militiaoff = 852073;
	public static final int militiaunconvert = 852651;
	public static final int mindrot = 852565;
	public static final int mirrorimage = 852123;
	public static final int monsoon = 852591;
	public static final int mount = 852469;
	public static final int mounthippogryph = 852143;
	public static final int move = 851986;
	public static final int moveAI = 851988;
	public static final int nagabuild = 852467;
	public static final int neutraldetectaoe = 852023;
	public static final int neutralinteract = 852566;
	public static final int neutralspell = 852630;
	public static final int nightelfbuild = 851997;
	public static final int orcbuild = 851996;
	public static final int parasite = 852601;
	public static final int parasiteoff = 852603;
	public static final int parasiteon = 852602;
	public static final int patrol = 851990;
	public static final int phaseshift = 852514;
	public static final int phaseshiftinstant = 852517;
	public static final int phaseshiftoff = 852516;
	public static final int phaseshifton = 852515;
	public static final int phoenixfire = 852481;
	public static final int phoenixmorph = 852482;
	public static final int poisonarrows = 852255;
	public static final int poisonarrowstarg = 852254;
	public static final int polymorph = 852074;
	public static final int possession = 852196;
	public static final int preservation = 852568;
	public static final int purge = 852111;
	public static final int rainofchaos = 852237;
	public static final int rainoffire = 852238;
	public static final int raisedead = 852197;
	public static final int raisedeadoff = 852199;
	public static final int raisedeadon = 852198;
	public static final int ravenform = 852155;
	public static final int recharge = 852157;
	public static final int rechargeoff = 852159;
	public static final int rechargeon = 852158;
	public static final int rejuvination = 852160;
	public static final int renew = 852161;
	public static final int renewoff = 852163;
	public static final int renewon = 852162;
	public static final int repair = 852024;
	public static final int repairoff = 852026;
	public static final int repairon = 852025;
	public static final int replenish = 852542;
	public static final int replenishlife = 852545;
	public static final int replenishlifeoff = 852547;
	public static final int replenishlifeon = 852546;
	public static final int replenishmana = 852548;
	public static final int replenishmanaoff = 852550;
	public static final int replenishmanaon = 852549;
	public static final int replenishoff = 852544;
	public static final int replenishon = 852543;
	public static final int request_hero = 852239;
	public static final int requestsacrifice = 852201;
	public static final int restoration = 852202;
	public static final int restorationoff = 852204;
	public static final int restorationon = 852203;
	public static final int resumebuild = 851999;
	public static final int resumeharvesting = 852017;
	public static final int resurrection = 852094;
	public static final int returnresources = 852020;
	public static final int revenge = 852241;
	public static final int revive = 852039;
	public static final int roar = 852164;
	public static final int robogoblin = 852656;
	public static final int root = 852165;
	public static final int sacrifice = 852205;
	public static final int sanctuary = 852569;
	public static final int scout = 852181;
	public static final int selfdestruct = 852040;
	public static final int selfdestructoff = 852042;
	public static final int selfdestructon = 852041;
	public static final int sentinel = 852182;
	public static final int setrally = 851980;
	public static final int shadowsight = 852570;
	public static final int shadowstrike = 852527;
	public static final int shockwave = 852125;
	public static final int silence = 852592;
	public static final int sleep = 852227;
	public static final int slow = 852075;
	public static final int slowoff = 852077;
	public static final int slowon = 852076;
	public static final int soulburn = 852668;
	public static final int soulpreservation = 852242;
	public static final int spellshield = 852571;
	public static final int spellshieldaoe = 852572;
	public static final int spellsteal = 852483;
	public static final int spellstealoff = 852485;
	public static final int spellstealon = 852484;
	public static final int spies = 852235;
	public static final int spiritlink = 852499;
	public static final int spiritofvengeance = 852528;
	public static final int spirittroll = 852573;
	public static final int spiritwolf = 852126;
	public static final int stampede = 852593;
	public static final int standdown = 852113;
	public static final int starfall = 852183;
	public static final int stasistrap = 852114;
	public static final int steal = 852574;
	public static final int stomp = 852127;
	public static final int stoneform = 852206;
	public static final int stop = 851972;
	public static final int submerge = 852604;
	public static final int summonfactory = 852658;
	public static final int summongrizzly = 852594;
	public static final int summonphoenix = 852489;
	public static final int summonquillbeast = 852595;
	public static final int summonwareagle = 852596;
	public static final int tankdroppilot = 852079;
	public static final int tankloadpilot = 852080;
	public static final int tankpilot = 852081;
	public static final int taunt = 852520;
	public static final int thunderbolt = 852095;
	public static final int thunderclap = 852096;
	public static final int tornado = 852597;
	public static final int townbelloff = 852083;
	public static final int townbellon = 852082;
	public static final int tranquility = 852184;
	public static final int transmute = 852665;
	public static final int unavatar = 852087;
	public static final int unavengerform = 852532;
	public static final int unbearform = 852139;
	public static final int unburrow = 852534;
	public static final int uncoldarrows = 852245;
	public static final int uncorporealform = 852494;
	public static final int undeadbuild = 851998;
	public static final int undefend = 852056;
	public static final int undivineshield = 852091;
	public static final int unetherealform = 852497;
	public static final int unflamingarrows = 852175;
	public static final int unflamingattack = 852541;
	public static final int unholyfrenzy = 852209;
	public static final int unimmolation = 852178;
	public static final int unload = 852047;
	public static final int unloadall = 852048;
	public static final int unloadallcorpses = 852054;
	public static final int unloadallinstant = 852049;
	public static final int unpoisonarrows = 852256;
	public static final int unravenform = 852156;
	public static final int unrobogoblin = 852657;
	public static final int unroot = 852166;
	public static final int unstableconcoction = 852500;
	public static final int unstoneform = 852207;
	public static final int unsubmerge = 852605;
	public static final int unsummon = 852210;
	public static final int unwindwalk = 852130;
	public static final int vengeance = 852521;
	public static final int vengeanceinstant = 852524;
	public static final int vengeanceoff = 852523;
	public static final int vengeanceon = 852522;
	public static final int volcano = 852669;
	public static final int voodoo = 852503;
	public static final int ward = 852504;
	public static final int waterelemental = 852097;
	public static final int wateryminion = 852598;
	public static final int web = 852211;
	public static final int weboff = 852213;
	public static final int webon = 852212;
	public static final int whirlwind = 852128;
	public static final int windwalk = 852129;
	public static final int wispharvest = 852214;
	
	/** Taken from WaterKnight's list
	 * 
	 * Starting with a few odd commands (some from reforged)
	 */
	public static final int AImove = 851988; //AImove (Amov) -- 
	public static final int ritualdagger = 852674; //item ritual dagger (AIdg, AIg2) -- ritualdagger
	public static final int unholyfrenzyaoe = 852675; //incite unholy frenzy (Auuf) -- unholyfrenzyaoe
	
	// Item Commands
	public static final int itemanimatedead = 852258; //item animate dead (AIan) -- 
	public static final int itemfigurinesummon = 852261; //figurine summon (Aifs, AIfs) -- 
	public static final int itemcommand = 852267; //item command (AIco) -- 
	public static final int itemdamageaoe = 852268; //item damage aoe (goblin landmine) (AIdm) -- 
	public static final int itemdefenseaoe = 852269; //item defense aoe (scroll of protection) (AIda) -- 
	public static final int itemareadetection = 852270; //area detection (crystal ball, arcane tower) (AIta) -- 
	public static final int itemdispelaoe = 852271; //item dispel aoe (AIdi) -- no target, point target, unit target
	public static final int itemheal = 852272; //item heal (AIhe) -- 
	public static final int itemhealaoe = 852273; //item heal aoe (AIha) -- 
	public static final int itemwandillusion = 852274; //wandillusion (AIil) -- 
	public static final int itemlightningpurge = 852275; //lightning purge (AIlp) -- 
	public static final int itemmanarestore = 852276; //item mana restore (AIma) -- 
	public static final int itemmanarestoreaoe = 852277; //item mana restore aoe (AImr) -- 
	public static final int itemplacemine = 852278; //item place mine (goblin landmine) (AIpm) -- 
	public static final int itemrecall = 852279; //item recall (AIrt) -- 
	public static final int itemrestore = 852281; //item restore (AIre) -- 
	public static final int itemrestoreaoe = 852282; //item restore aoe (AIra) -- 
	public static final int itemresurrection = 852283; //item resurrection (AIrs) -- 
	public static final int itemsoultrap = 852284; //item soul trap (AIso) -- 
	public static final int itemspeed = 852285; //item speed, item speed aoe (scroll of speed) (AIsp, AIsa) -- 
	public static final int itemtownportal = 852286; //item town portal (AItp, AIte) -- 
	public static final int iteminvisibility = 852287; //item invisibility (AIvi) -- 
	public static final int iteminvulnerabilitypotion = 852288; //invulnerability potion (AIvu) -- 
	public static final int itemretrain = 852471; //retrain (tome of retraining) (Aret) -- 
	public static final int itemspellbook = 852608; //spell book (Aspb) -- 
	public static final int itemregeneration = 852609; //item regeneration (rejuvination potions, ...) (AIrg) -- 
	public static final int itemgivegold = 852610; //give gold (AIgo) -- 
	public static final int itemgivelumber = 852611; //give lumber (AIlu) -- 
	public static final int itemrevealmap = 852612; //item reveal map (AIrv) -- 
	public static final int itemweb = 852613; //item web (AIwb) -- 
	public static final int itemmonsterlure = 852614; //item monster lure (AImo) -- 
	public static final int itemchaindispel = 852615; //chain dispel (AIdc) -- 
	public static final int itemcureaoe = 852616; //cure aoe (AIca) -- no target, point target, target
	public static final int itemflaregun = 852618; //flare gun (AIfa) -- 
	public static final int itemtinystructures = 852619; //item tiny structures (AIbl) -- 
	public static final int itemchangetimeofday = 852621; //item change time of day (moonstone) (AIct) -- 
	public static final int itemrandomitem = 852622; //item random item (AIri) -- 
	public static final int itempotionvampirism = 852623; //item potion vampirism (AIpv) -- 
	public static final int itemraisedead = 852624; //item raise dead (AIrd) -- 
	public static final int itemdustofappearance = 852625; //dust of appearance (AItb) -- 
	public static final int itemultravisionglyph = 852627; //ultra vision glyph (AIgl) -- 
	public static final int itemruneofrebirth = 852628; //rune of rebirth (AIrb) -- 
	//tomes
	public static final int itemagilitymod = 852257; //agility mod (AIam) -- 
	public static final int itemattackmod = 852259; //attack mod (AIaa) -- 
	public static final int itemxpgain = 852260; //experience mod (AIem) -- 
	public static final int itemintelligencemod = 852262; //intelligence mod (AIim) -- 
	public static final int itemlevelgain = 852263; //level mod (AIlm) -- 
	public static final int itemlifegain = 852264; //hp mod (AImi) -- 
	public static final int itemmpmod = 852265; //mp mod (AImn) -- AImn not in the current abilitydata .slk
	public static final int itemstrengthmod = 852266; //strength mod (AIsm) -- 
	
	// Passives with cooldowns:
	public static final int _exhume = 852537; //exhume (Aexh) -- 
	
	// Passive Order IDs
	public static final int _poisonattack = 852016; //poison attack (Apoi) -- 
	public static final int _gyrocopterbombs = 852061; //gyrocopter bombs (Agyb) -- 
	public static final int _detectgyrocopter = 852062; //detect (gyrocopter) (Agyv) -- 
	public static final int _detectmagicsentinel = 852070; //detect (magic sentinel) (Adts) -- 
	public static final int _stormhammers = 852078; //storm hammers (Asth) -- 
	public static final int _brillianceaura = 852084; //brilliance aura (AHab) -- 
	public static final int _devotionaura = 852085; //devotion aura (AHad) -- 
	public static final int _bash = 852088; //bash (AHbh) -- 
	public static final int _kotoaura = 852098; //koto aura (Aakb) -- 
	public static final int _pillage = 852112; //pillage (Asal) -- 
	public static final int _venomspears = 852115; //venom spears (Aven) -- 
	public static final int _pulverize = 852116; //pulverize (Awar) -- 
	public static final int _commandaura = 852117; //command aura (AOac) -- 
	public static final int _enduranceaura = 852118; //endurance aura (AOae) -- 
	public static final int _criticalstrike = 852120; //critical strike (AOcr) -- 
	public static final int _reincarnation = 852124; //reincarnation (AOre) -- 
	public static final int coupleinternal = 852141; //couple (archer), couple (hippogryph) (Acoa, Acoh) -- 
	public static final int _impalingbolt = 852152; //impaling bolt (vorpal blades) (Aimp) -- 
	public static final int _itemmagicimmunity = 852153; //item magic immunity (Amim) -- 
	public static final int _moonglaive = 852154; //moon glaive (Amgl) -- 
	public static final int _slowpoison = 852167; //slow poison (Aspo) -- 
	public static final int _thornsaura = 852169; //thorns aura (AEah) -- 
	public static final int _trueshotaura = 852170; //trueshot aura (AEar) -- 
	public static final int _evasion = 852172; //evasion (AEev) -- 
	public static final int _plagueaura = 852193; //plague aura (Aapl) -- 
	public static final int _plaguetoss = 852194; //plague toss (Apts) -- 
	public static final int _detectshade = 852208; //detect (shade) (Atru) -- 
	public static final int _permanentimmolation = 852236; //permanent immolation (ANpi) -- 
	public static final int _creepreincarnation = 852251; //creepreincarnation (ACrn) --
	public static final int _itemcloakofflames = 852289; //item cloak of flames (immolation) (AIcf) --  
	public static final int _slowaura = 852461; //slow aura (Aasl) -- 
	public static final int _rocketattack = 852472; //rocket attack (barrage) (Aroc) -- 
	public static final int _feedback = 852475; //feedback (Afbk) -- 
	public static final int _flakcannon = 852476; //flak cannon (Aflk) -- 
	public static final int _fragshards = 852477; //frag shards (Afsh) -- 
	public static final int _ballsoffire = 852492; //balls of fire (burning oil) (Abof) -- 
	public static final int _liquidfire = 852498; //liquid fire (Aliq) -- 
	public static final int _ethereal = 852510; //ethereal (Aetl) -- 
	public static final int _resistantskin = 852518; //resistant skin (Arsk) -- 
	public static final int _hardenedskin = 852519; //hardened skin (Assk) -- 
	public static final int _blightregenaura = 852530; //blight regen aura (Aabr) -- 
	public static final int _exhume2 = 852538; // (Aexh?) -- seems to be exhume related
	public static final int _spikedcarapace = 852559; //spiked carapace (AUts) -- 
	public static final int _permanentimmolationgraphic = 852567; //permanent immolation graphic (Apig) -- 
	public static final int _thorns = 852575; //thorns (ANth) -- 
	public static final int _cleavingattack = 852582; //cleaving attack (ANca) -- 
	public static final int _drunkenbrawler = 852584; //drunken brawler (ANdb) -- 
	public static final int _flyingcarpet = 852606; //flyingcarpet (AIfc) -- 
	public static final int _itemdefendelunesgrace = 852607; //item defend, elune's grace (AIdd) -- 
	public static final int _demolish = 852653; //demolish (ANde) -- 
	public static final int _engineeringupgrade = 852654; //engineering upgrade (ANeg) -- 
	public static final int _factory = 852655; //factory (ANfy) -- 
	public static final int _incinerate = 852666; //incinerate (ANic) -- 
	public static final int _sunderingblades = 852676; //sundering blades (Aaab) -- 
	public static final int _variableattributemod = 852678; //variable attribute mod (circlet of nobility) (AIvm) -- 
	public static final int _orbofvenom = 852661; //orb of venom (poison attack) (Apo2) -- 

	// Misc
	public static final int _locustwander = 852557; //locust wander (Aloc) -- 
	public static final int _locustreturn = 852558; //locust return (Aloc) -- 
	
	
	// Odd internals
 	public static final int smartvariant = 851970; //smart variant () -- 
	/**"targeting a friendly or neutral unit, it's like smart, moving to the target, following, highlighting patrol, triggering effects like moon well heal
		targeting an enemy unit, it attacks the target
		targeting a tree, it harvests it or moves to it (latter unlike 851971), eat tree or war club does not work
		targeting a point, the unit moves to it while attacking enemies on the way
		targeting an item on the ground, it will be issued even without inventory but does nothing"**/
	public static final int _recycleguardposition = 851974; //? () -- is issued when guard position is recycled or when the unit is neutral hostile and it gets an expiration timer (non-exhaustive)
	public static final int _instant3 = 851975; //instant3 () -- 
	public static final int _deprecatedcancel = 851977; //some deprecated cancel? () -- can be issued to an altar targeting a hero reviving, but does not have an effect
	public static final int _cancelprogress = 851978; //cancel structure/upgrade of structure () -- can also be issued when the unit has other abilities that cause progress (unit training, research, sacrificing, reviving) but does not have an effect there
	public static final int _instant2 = 851987; //instant2 () -- is issued when the unit returned to its guard position, idle?
	public static final int _instant1 = 851991; //instant1 () -- 
	public static final int _instant4harvestlumber = 852019; //instant4, harvestlumber? (Awha?, Ahrl?) -- 
	public static final int _startreviveunit1altar = 852027; //start revive unit 1 (altar) (Arev) -- 
	public static final int _startreviveunit2altar = 852028; //start revive unit 2 (altar) (Arev) -- 
	public static final int _startreviveunit3altar = 852029; //start revive unit 3 (altar) (Arev) -- 
	public static final int _startreviveunit4altar = 852030; //start revive unit 4 (altar) (Arev) -- 
	public static final int _startreviveunit5altar = 852031; //start revive unit 5 (altar) (Arev) -- 
	public static final int _startreviveunit6altar = 852032; //start revive unit 6 (altar) (Arev) -- 
	public static final int _startreviveunit7altar = 852033; //start revive unit 7 (altar) (Arev) -- 
	public static final int _allmodblightplacementsacrificialskullpassivesdetectionalsoflyersubmerge = 852290; //all mod, blight placement (sacrificial skull), passives, detection (also flyer), submerge (AIxm, Ablp, APai, Adet, ANsu) --
	public static final int _awakenunit1tavern = 852462; //awaken unit 1 (tavern) (Aawa) -- must be issued targeting the hero to awaken
	public static final int _awakenunit2tavern = 852463; //awaken unit 2 (tavern) (Aawa) -- must be issued targeting the hero to awaken
	public static final int _awakenunit3tavern = 852464; //awaken unit 3 (tavern) (Aawa) -- must be issued targeting the hero to awaken
	public static final int _awakenunit4tavern = 852465; //awaken unit 4 (tavern) (Aawa) -- must be issued targeting the hero to awaken
	public static final int _cannibalizetarget = 852535; //cannibalize (Acan) -- unit target, can even be cast if the unit has full life (Acan)
	public static final int _resumeharvestlumber = 852659; //resumeharvestlumber? (Ahrl) -- 
	public static final int _returnresources = 852660; //return resources? (Ahar) -- no target
	public static final int _attacktargetpriority = 852677; //attack target priority (Aatp) -- Aatp: used for both activating and deactivating the ability
	public static final int _spell = 852629; //spell (AAsp) -- 

	// And unknowns
	public static final int _unknown851979 = 851979; // () -- used by CUnit, maybe a signal
	public static final int _unknown851982 = 851982; // () -- used, not sure for what, seems to be a signal
	public static final int _unknown851989 = 851989; // () -- 
	public static final int _unknown851992 = 851992; // () -- 
	public static final int _unknown852014 = 852014; // () -- 
	public static final int _unknown852034 = 852034; // (Arev?) -- seems revive related
	public static final int _unknown852035 = 852035; // (Arev?) -- seems revive related
	public static final int _unknown852036 = 852036; // (Arev?) -- seems revive related
	public static final int _unknown852037 = 852037; // (Arev?) -- seems revive related
	public static final int _unknown852038 = 852038; // (Arev?) -- seems revive related
	public static final int _unknown852045 = 852045; // () -- 
	public static final int _unknown852058 = 852058; // () -- 
	public static final int _unknown852059 = 852059; // () -- 
	public static final int _unknown852168 = 852168; // () -- 
	public static final int _unknown852280 = 852280; // () --  
	public static final int _unknown852291 = 852291; // () -- 
	public static final int _unknown852292 = 852292; // () -- 
	public static final int _unknown852293 = 852293; // () -- 
	public static final int _unknown852294 = 852294; // () -- 
	public static final int _unknown852295 = 852295; // () -- 
	public static final int _unknown852296 = 852296; // () -- 
	public static final int _unknown852297 = 852297; // () -- 
	public static final int _unknown852298 = 852298; // () -- 
	public static final int _unknown852299 = 852299; // () -- 
	public static final int _unknown852300 = 852300; // () -- 
	public static final int _unknown852301 = 852301; // () -- 
	public static final int _unknown852302 = 852302; // () -- 
	public static final int _unknown852303 = 852303; // () -- 
	public static final int _unknown852304 = 852304; // () -- 
	public static final int _unknown852305 = 852305; // () -- 
	public static final int _unknown852306 = 852306; // () -- 
	public static final int _unknown852307 = 852307; // () -- 
	public static final int _unknown852308 = 852308; // () -- 
	public static final int _unknown852309 = 852309; // () -- 
	public static final int _unknown852310 = 852310; // () -- 
	public static final int _unknown852311 = 852311; // () -- 
	public static final int _unknown852312 = 852312; // () -- 
	public static final int _unknown852313 = 852313; // () -- 
	public static final int _unknown852314 = 852314; // () -- 
	public static final int _unknown852315 = 852315; // () -- 
	public static final int _unknown852316 = 852316; // () -- 
	public static final int _unknown852317 = 852317; // () -- 
	public static final int _unknown852318 = 852318; // () -- 
	public static final int _unknown852319 = 852319; // () -- 
	public static final int _unknown852320 = 852320; // () -- 
	public static final int _unknown852321 = 852321; // () -- 
	public static final int _unknown852322 = 852322; // () -- 
	public static final int _unknown852323 = 852323; // () -- 
	public static final int _unknown852324 = 852324; // () -- 
	public static final int _unknown852325 = 852325; // () -- 
	public static final int _unknown852326 = 852326; // () -- 
	public static final int _unknown852327 = 852327; // () -- 
	public static final int _unknown852328 = 852328; // () -- 
	public static final int _unknown852329 = 852329; // () -- 
	public static final int _unknown852330 = 852330; // () -- 
	public static final int _unknown852331 = 852331; // () -- 
	public static final int _unknown852332 = 852332; // () -- 
	public static final int _unknown852333 = 852333; // () -- 
	public static final int _unknown852334 = 852334; // () -- 
	public static final int _unknown852335 = 852335; // () -- 
	public static final int _unknown852336 = 852336; // () -- 
	public static final int _unknown852337 = 852337; // () -- 
	public static final int _unknown852338 = 852338; // () -- 
	public static final int _unknown852339 = 852339; // () -- 
	public static final int _unknown852340 = 852340; // () -- 
	public static final int _unknown852341 = 852341; // () -- 
	public static final int _unknown852342 = 852342; // () -- 
	public static final int _unknown852343 = 852343; // () -- 
	public static final int _unknown852344 = 852344; // () -- 
	public static final int _unknown852345 = 852345; // () -- 
	public static final int _unknown852346 = 852346; // () -- 
	public static final int _unknown852347 = 852347; // () -- 
	public static final int _unknown852348 = 852348; // () -- 
	public static final int _unknown852349 = 852349; // () -- 
	public static final int _unknown852350 = 852350; // () -- 
	public static final int _unknown852351 = 852351; // () -- 
	public static final int _unknown852352 = 852352; // () -- 
	public static final int _unknown852353 = 852353; // () -- 
	public static final int _unknown852354 = 852354; // () -- 
	public static final int _unknown852355 = 852355; // () -- 
	public static final int _unknown852356 = 852356; // () -- 
	public static final int _unknown852357 = 852357; // () -- 
	public static final int _unknown852358 = 852358; // () -- 
	public static final int _unknown852359 = 852359; // () -- 
	public static final int _unknown852360 = 852360; // () -- 
	public static final int _unknown852361 = 852361; // () -- 
	public static final int _unknown852362 = 852362; // () -- 
	public static final int _unknown852363 = 852363; // () -- 
	public static final int _unknown852364 = 852364; // () -- 
	public static final int _unknown852365 = 852365; // () -- 
	public static final int _unknown852366 = 852366; // () -- 
	public static final int _unknown852367 = 852367; // () -- 
	public static final int _unknown852368 = 852368; // () -- 
	public static final int _unknown852369 = 852369; // () -- 
	public static final int _unknown852370 = 852370; // () -- 
	public static final int _unknown852371 = 852371; // () -- 
	public static final int _unknown852372 = 852372; // () -- 
	public static final int _unknown852373 = 852373; // () -- 
	public static final int _unknown852374 = 852374; // () -- 
	public static final int _unknown852375 = 852375; // () -- 
	public static final int _unknown852376 = 852376; // () -- 
	public static final int _unknown852377 = 852377; // () -- 
	public static final int _unknown852378 = 852378; // () -- 
	public static final int _unknown852379 = 852379; // () -- 
	public static final int _unknown852380 = 852380; // () -- 
	public static final int _unknown852381 = 852381; // () -- 
	public static final int _unknown852382 = 852382; // () -- 
	public static final int _unknown852383 = 852383; // () -- 
	public static final int _unknown852384 = 852384; // () -- 
	public static final int _unknown852385 = 852385; // () -- 
	public static final int _unknown852386 = 852386; // () -- 
	public static final int _unknown852387 = 852387; // () -- 
	public static final int _unknown852388 = 852388; // () -- 
	public static final int _unknown852389 = 852389; // () -- 
	public static final int _unknown852390 = 852390; // () -- 
	public static final int _unknown852391 = 852391; // () -- 
	public static final int _unknown852392 = 852392; // () -- 
	public static final int _unknown852393 = 852393; // () -- 
	public static final int _unknown852394 = 852394; // () -- 
	public static final int _unknown852395 = 852395; // () -- 
	public static final int _unknown852396 = 852396; // () -- 
	public static final int _unknown852397 = 852397; // () -- 
	public static final int _unknown852398 = 852398; // () -- 
	public static final int _unknown852399 = 852399; // () -- 
	public static final int _unknown852400 = 852400; // () -- 
	public static final int _unknown852401 = 852401; // () -- 
	public static final int _unknown852402 = 852402; // () -- 
	public static final int _unknown852403 = 852403; // () -- 
	public static final int _unknown852404 = 852404; // () -- 
	public static final int _unknown852405 = 852405; // () -- 
	public static final int _unknown852406 = 852406; // () -- 
	public static final int _unknown852407 = 852407; // () -- 
	public static final int _unknown852408 = 852408; // () -- 
	public static final int _unknown852409 = 852409; // () -- 
	public static final int _unknown852410 = 852410; // () -- 
	public static final int _unknown852411 = 852411; // () -- 
	public static final int _unknown852412 = 852412; // () -- 
	public static final int _unknown852413 = 852413; // () -- 
	public static final int _unknown852414 = 852414; // () -- 
	public static final int _unknown852415 = 852415; // () -- 
	public static final int _unknown852416 = 852416; // () -- 
	public static final int _unknown852417 = 852417; // () -- 
	public static final int _unknown852418 = 852418; // () -- 
	public static final int _unknown852419 = 852419; // () -- 
	public static final int _unknown852420 = 852420; // () -- 
	public static final int _unknown852421 = 852421; // () -- 
	public static final int _unknown852422 = 852422; // () -- 
	public static final int _unknown852423 = 852423; // () -- 
	public static final int _unknown852424 = 852424; // () -- 
	public static final int _unknown852425 = 852425; // () -- 
	public static final int _unknown852426 = 852426; // () -- 
	public static final int _unknown852427 = 852427; // () -- 
	public static final int _unknown852428 = 852428; // () -- 
	public static final int _unknown852429 = 852429; // () -- 
	public static final int _unknown852430 = 852430; // () -- 
	public static final int _unknown852431 = 852431; // () -- 
	public static final int _unknown852432 = 852432; // () -- 
	public static final int _unknown852433 = 852433; // () -- 
	public static final int _unknown852434 = 852434; // () -- 
	public static final int _unknown852435 = 852435; // () -- 
	public static final int _unknown852436 = 852436; // () -- 
	public static final int _unknown852437 = 852437; // () -- 
	public static final int _unknown852438 = 852438; // () -- 
	public static final int _unknown852439 = 852439; // () -- 
	public static final int _unknown852440 = 852440; // () -- 
	public static final int _unknown852441 = 852441; // () -- 
	public static final int _unknown852442 = 852442; // () -- 
	public static final int _unknown852443 = 852443; // () -- 
	public static final int _unknown852444 = 852444; // () -- 
	public static final int _unknown852445 = 852445; // () -- 
	public static final int _unknown852446 = 852446; // () -- 
	public static final int _unknown852447 = 852447; // () -- 
	public static final int _unknown852448 = 852448; // () -- 
	public static final int _unknown852449 = 852449; // () -- 
	public static final int _unknown852450 = 852450; // () -- 
	public static final int _unknown852451 = 852451; // () -- 
	public static final int _unknown852452 = 852452; // () -- 
	public static final int _unknown852453 = 852453; // () -- 
	public static final int _unknown852454 = 852454; // () -- 
	public static final int _unknown852455 = 852455; // () -- 
	public static final int _unknown852456 = 852456; // () -- 
	public static final int _unknown852457 = 852457; // () -- 
	public static final int _unknown852460 = 852460; //?? (can be issued on targets) () -- can be issued on targets (unit/item/destructable), target must not be the unit itself, target unit must be visible, ordered unit must generally be able to move
	public static final int _unknown852468 = 852468; // () -- 
	public static final int _unknown852617 = 852617; // () -- 
	public static final int _unknown852620 = 852620; // () -- 
	public static final int _unknown852626 = 852626; // () -- 
	public static final int _unknown852631 = 852631; // () -- 
	public static final int _unknown852632 = 852632; // () -- 
	public static final int _unknown852633 = 852633; // () -- 
	public static final int _unknown852634 = 852634; // () -- 
	public static final int _unknown852635 = 852635; // () -- 
	public static final int _unknown852636 = 852636; // () -- 
	public static final int _unknown852637 = 852637; // () -- 
	public static final int _unknown852638 = 852638; // () -- 
	public static final int _unknown852639 = 852639; // () -- 
	public static final int _unknown852640 = 852640; // () -- 
	public static final int _unknown852641 = 852641; // () -- 
	public static final int _unknown852642 = 852642; // () -- 
	public static final int _unknown852643 = 852643; // () -- 
	public static final int _unknown852644 = 852644; // () -- 
	public static final int _unknown852645 = 852645; // () -- 
	public static final int _unknown852646 = 852646; // () -- 
	public static final int _unknown852647 = 852647; // () -- 
	public static final int _unknown852648 = 852648; // () -- 
	public static final int _unknown852649 = 852649; // () -- 
	public static final int _unknown852650 = 852650; // () -- 
	public static final int _unknown852673 = 852673; // () -- related to human build maybe

	/**
	 *  Made up for warsmash, could benefit from getting more accurate
	 */
	public static final int itemstatgain = 852214001;

	public static final int genericpaircommand = 852214008;
}
