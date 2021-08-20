package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

/**
 * Thanks to the Wurst guys for this list of ids, taken from this link:
 * https://github.com/wurstscript/WurstStdlib2/blob/master/wurst/_wurst/assets/Orders.wurst
 *
 * The original code ported to create this Java file is licensed under the
 * Apache License; you can read more at the link above.
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
	public static final int itemdrag00 = 852002;
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
	public static final int itemdrag05 = 852007;
	/**
	 * An order that will make the ordered hero use the item in a certain inventory
	 * slot. If it's an order with no target or object or point targeted depends on
	 * the type of item.
	 */
	public static final int itemuse00 = 852008;
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
	public static final int itemuse05 = 852013;
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
}
