package com.etheller.warsmash.units.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * Inspired by PitzerMike's obj.h, without a lot of immediate focus on Java
 * conventions. I will probably get it converted over to Java conventions once I
 * have a working replica of his obj.h code.
 *
 * @author Eric
 *
 */
public final class War3ObjectDataChangeset {
	public static final int VAR_TYPE_INT = 0;
	public static final int VAR_TYPE_REAL = 1;
	public static final int VAR_TYPE_UNREAL = 2;
	public static final int VAR_TYPE_STRING = 3;
	public static final int VAR_TYPE_BOOLEAN = 4;
	public static final int MAX_STR_LEN = 1024;
	private static final Set<War3ID> UNIT_ID_SET;
	private static final Set<War3ID> ABILITY_ID_SET;
	static {
		final HashSet<War3ID> unitHashSet = new HashSet<>();
		unitHashSet.add(War3ID.fromString("ubpx"));
		unitHashSet.add(War3ID.fromString("ubpy"));
		unitHashSet.add(War3ID.fromString("ides"));
		unitHashSet.add(War3ID.fromString("uhot"));
		unitHashSet.add(War3ID.fromString("unam"));
		unitHashSet.add(War3ID.fromString("ureq"));
		unitHashSet.add(War3ID.fromString("urqa"));
		unitHashSet.add(War3ID.fromString("utip"));
		unitHashSet.add(War3ID.fromString("utub"));
		UNIT_ID_SET = unitHashSet;
		final HashSet<War3ID> abilHashSet = new HashSet<>();
		abilHashSet.add(War3ID.fromString("irc2"));
		abilHashSet.add(War3ID.fromString("irc3"));
		abilHashSet.add(War3ID.fromString("bsk1"));
		abilHashSet.add(War3ID.fromString("bsk2"));
		abilHashSet.add(War3ID.fromString("bsk3"));
		abilHashSet.add(War3ID.fromString("coau"));
		abilHashSet.add(War3ID.fromString("coa1"));
		abilHashSet.add(War3ID.fromString("coa2"));
		abilHashSet.add(War3ID.fromString("cyc1"));
		abilHashSet.add(War3ID.fromString("dcp1"));
		abilHashSet.add(War3ID.fromString("dcp2"));
		abilHashSet.add(War3ID.fromString("dvm1"));
		abilHashSet.add(War3ID.fromString("dvm2"));
		abilHashSet.add(War3ID.fromString("dvm3"));
		abilHashSet.add(War3ID.fromString("dvm4"));
		abilHashSet.add(War3ID.fromString("dvm5"));
		abilHashSet.add(War3ID.fromString("exh1"));
		abilHashSet.add(War3ID.fromString("exhu"));
		abilHashSet.add(War3ID.fromString("fak1"));
		abilHashSet.add(War3ID.fromString("fak2"));
		abilHashSet.add(War3ID.fromString("fak3"));
		abilHashSet.add(War3ID.fromString("hwdu"));
		abilHashSet.add(War3ID.fromString("inv1"));
		abilHashSet.add(War3ID.fromString("inv2"));
		abilHashSet.add(War3ID.fromString("inv3"));
		abilHashSet.add(War3ID.fromString("inv4"));
		abilHashSet.add(War3ID.fromString("inv5"));
		abilHashSet.add(War3ID.fromString("liq1"));
		abilHashSet.add(War3ID.fromString("liq2"));
		abilHashSet.add(War3ID.fromString("liq3"));
		abilHashSet.add(War3ID.fromString("liq4"));
		abilHashSet.add(War3ID.fromString("mim1"));
		abilHashSet.add(War3ID.fromString("mfl1"));
		abilHashSet.add(War3ID.fromString("mfl2"));
		abilHashSet.add(War3ID.fromString("mfl3"));
		abilHashSet.add(War3ID.fromString("mfl4"));
		abilHashSet.add(War3ID.fromString("mfl5"));
		abilHashSet.add(War3ID.fromString("tpi1"));
		abilHashSet.add(War3ID.fromString("tpi2"));
		abilHashSet.add(War3ID.fromString("spl1"));
		abilHashSet.add(War3ID.fromString("spl2"));
		abilHashSet.add(War3ID.fromString("irl1"));
		abilHashSet.add(War3ID.fromString("irl2"));
		abilHashSet.add(War3ID.fromString("irl3"));
		abilHashSet.add(War3ID.fromString("irl4"));
		abilHashSet.add(War3ID.fromString("irl5"));
		abilHashSet.add(War3ID.fromString("idc1"));
		abilHashSet.add(War3ID.fromString("idc2"));
		abilHashSet.add(War3ID.fromString("idc3"));
		abilHashSet.add(War3ID.fromString("imo1"));
		abilHashSet.add(War3ID.fromString("imo2"));
		abilHashSet.add(War3ID.fromString("imo3"));
		abilHashSet.add(War3ID.fromString("imou"));
		abilHashSet.add(War3ID.fromString("ict1"));
		abilHashSet.add(War3ID.fromString("ict2"));
		abilHashSet.add(War3ID.fromString("isr1"));
		abilHashSet.add(War3ID.fromString("isr2"));
		abilHashSet.add(War3ID.fromString("ipv1"));
		abilHashSet.add(War3ID.fromString("ipv2"));
		abilHashSet.add(War3ID.fromString("ipv3"));
		abilHashSet.add(War3ID.fromString("mec1"));
		abilHashSet.add(War3ID.fromString("spb1"));
		abilHashSet.add(War3ID.fromString("spb2"));
		abilHashSet.add(War3ID.fromString("spb3"));
		abilHashSet.add(War3ID.fromString("spb4"));
		abilHashSet.add(War3ID.fromString("spb5"));
		abilHashSet.add(War3ID.fromString("gra1"));
		abilHashSet.add(War3ID.fromString("gra2"));
		abilHashSet.add(War3ID.fromString("gra3"));
		abilHashSet.add(War3ID.fromString("gra4"));
		abilHashSet.add(War3ID.fromString("gra5"));
		abilHashSet.add(War3ID.fromString("ipmu"));
		abilHashSet.add(War3ID.fromString("flk1"));
		abilHashSet.add(War3ID.fromString("flk2"));
		abilHashSet.add(War3ID.fromString("flk3"));
		abilHashSet.add(War3ID.fromString("flk4"));
		abilHashSet.add(War3ID.fromString("flk5"));
		abilHashSet.add(War3ID.fromString("fbk1"));
		abilHashSet.add(War3ID.fromString("fbk2"));
		abilHashSet.add(War3ID.fromString("fbk3"));
		abilHashSet.add(War3ID.fromString("fbk4"));
		abilHashSet.add(War3ID.fromString("nca1"));
		abilHashSet.add(War3ID.fromString("pxf1"));
		abilHashSet.add(War3ID.fromString("pxf2"));
		abilHashSet.add(War3ID.fromString("mls1"));
		abilHashSet.add(War3ID.fromString("sla1"));
		abilHashSet.add(War3ID.fromString("sla2"));
		ABILITY_ID_SET = abilHashSet;
	}

	private int version;
	private ObjectMap original = new ObjectMap();
	private final ObjectMap custom = new ObjectMap();
	private char expected;
	private War3ID lastused;

	public char kind;
	public boolean detected;

	public War3ID nameField;

	public War3ObjectDataChangeset() {
		this.version = 2;
		this.kind = 'u';
		this.expected = 'u';
		this.detected = false;
		this.lastused = War3ID.fromString("u~~~");
	}

	public War3ObjectDataChangeset(final char expectedkind) {
		this.version = 2;
		this.kind = 'u';
		this.expected = expectedkind;
		this.detected = false;
		this.lastused = War3ID.fromString("u~~~");
	}

	public boolean detectKind(final War3ID chid) {
		if (UNIT_ID_SET.contains(chid)) {
			this.kind = 'u';
			return false;
		}
		else if (ABILITY_ID_SET.contains(chid)) {
			this.kind = 'a';
		}
		else {
			switch (chid.asStringValue().charAt(0)) {
			case 'f':
				this.kind = 'h';
				break;
			case 'i':
				this.kind = 't';
				break;
			case 'g':
				this.kind = 'q';
				break;
			case 'a':
			case 'u':
			case 'b':
			case 'd':
				this.kind = chid.asStringValue().charAt(0);
				break;
			default:
				this.kind = this.expected;
			}
		}
		return true;
	}

	public char getExpectedKind() {
		return this.expected;
	}

	public War3ID getNameField() {
		final War3ID field = War3ID.fromString("unam");
		char cmp = this.kind;
		if (!this.detected) {
			cmp = this.expected;
		}
		switch (cmp) {
		case 'h':
			this.nameField = field.set(0, 'f');
			break;
		case 't':
			this.nameField = field.set(0, 'u');
			break;
		case 'q':
			this.nameField = field.set(0, 'g');
			break;
		default:
			this.nameField = field.set(0, cmp);
			break;
		}
		return this.nameField;
	}

	public boolean extended() {
		char cmp = this.kind;
		if (!this.detected) {
			cmp = this.expected;
		}
		switch (cmp) {
		case 'u':
		case 'h':
		case 'b':
		case 't':
			return false;
		}
		return true;
	}

	public void renameids(final ObjectMap map, final boolean isOriginal) {
		final War3ID nameId = getNameField();
		final List<War3ID> idsToRemoveFromMap = new ArrayList<>();
		final Map<War3ID, ObjectDataChangeEntry> idsToObjectsForAddingToMap = new HashMap<>();
		for (final Iterator<Map.Entry<War3ID, ObjectDataChangeEntry>> iterator = map.iterator(); iterator.hasNext();) {
			final Map.Entry<War3ID, ObjectDataChangeEntry> entry = iterator.next();
			final ObjectDataChangeEntry current = entry.getValue();
			final List<Change> nameEntry = current.getChanges().get(nameId);
			if ((nameEntry != null) && !nameEntry.isEmpty()) {
				final Change firstNameChange = nameEntry.get(0);
				int pos = firstNameChange.getStrval().lastIndexOf("::");
				if ((pos != -1) && (firstNameChange.getStrval().length() > (pos + 2))) {
					String rest = firstNameChange.getStrval().substring(pos + 2);
					if (rest.length() == 4) {
						final War3ID newId = War3ID.fromString(rest);
						final ObjectDataChangeEntry existingObjectWithMatchingId = map.get(newId);
						if (isOriginal) {// obj.cpp: update id and name
							current.setOldId(newId);
						}
						else {
							current.setNewId(newId);
						}
						firstNameChange.setStrval(firstNameChange.getStrval().substring(0, pos));
						if (existingObjectWithMatchingId != null) {
							// obj.cpp: carry over all changes
							final Iterator<Map.Entry<War3ID, List<Change>>> changeIterator = current.getChanges()
									.iterator();
							while (changeIterator.hasNext()) {
								final Map.Entry<War3ID, List<Change>> changeIteratorNext = changeIterator.next();
								final War3ID copiedChangeId = changeIteratorNext.getKey();
								List<Change> changeListForFieldToOverwrite = existingObjectWithMatchingId.getChanges()
										.get(copiedChangeId);
								if (changeListForFieldToOverwrite == null) {
									changeListForFieldToOverwrite = new ArrayList<>();
								}
								for (final Change changeToCopy : changeIteratorNext.getValue()) {
									final Iterator<Change> replaceIterator = changeListForFieldToOverwrite.iterator();
									boolean didOverwrite = false;
									while (replaceIterator.hasNext()) {
										final Change changeToOverwrite = replaceIterator.next();
										if (changeToOverwrite.getLevel() != changeToCopy.getLevel()) {
											// obj.cpp: we can only replace
											// changes with the same
											// level/variation
											continue;
										}
										if (copiedChangeId.equals(nameId)) {
											// obj.cpp: carry over further
											// references
											pos = changeToOverwrite.getStrval().lastIndexOf("::");
											if ((pos != -1) && (changeToOverwrite.getStrval().length() > (pos + 2))) {
												rest = changeToOverwrite.getStrval().substring(pos + 2);
												if ((rest.length() == 4) || "REMOVE".equals(rest)) {
													changeToCopy.setStrval(changeToCopy.getStrval() + "::" + rest);
													// so if this is a peasant, whose name was "Peasant::hfoo"
													// and when we copied his data onto the footman, we found
													// that the footman was named "Footman::hkni", then at that
													// point we set the peasant's name to be "Peasant::hkni"
													// because we are about to copy it onto the footman.
													// And, we already set it to just "Peasant", so
													// appending the "::" and the 'rest' variable is enough.
													// Then, on a further loop iteration, in theory
													// we will copoy the footman who is named Peasant
													// onto the knight.
													//
													// TODO but what if we already copied the footman onto the knight?
													// did PitzerMike consider this in obj.cpp?
												}
											}
										}
										changeToOverwrite.copyFrom(changeToCopy);
										didOverwrite = true;
										break;
									}
									if (!didOverwrite) {
										changeListForFieldToOverwrite.add(changeToCopy);
										if (changeListForFieldToOverwrite.size() == 1) {
											existingObjectWithMatchingId.getChanges().add(copiedChangeId,
													changeListForFieldToOverwrite);
										}
									}
								}
							}
						}
						else { // obj.cpp: an object with that id didn't exist
							idsToRemoveFromMap.add(entry.getKey());
							idsToObjectsForAddingToMap.put(newId, current.clone());
						}
					}
					else if ("REMOVE".equals(rest)) { // obj.cpp: want to remove the object
						idsToRemoveFromMap.add(entry.getKey());
					} // obj.cpp: in all other cases keep it untouched
				}
			}

		}
		for (final War3ID id : idsToRemoveFromMap) {
			map.remove(id);
		}
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : idsToObjectsForAddingToMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
	}

	public void renameIds() {
		renameids(this.original, true);
		renameids(this.custom, false);
	}

	// ' ' - '/'
	// ':' - '@'
	// '[' - '`'
	// '{' - '~'
	public char nextchar(final char cur) {
		switch (cur) {
		case '&': // skip ' because often jass parsers don't handle escaped rawcodes like '\''
			return '(';
		case '/': // skip digits
			return ':';
		case '@': // skip capital letters
			return '['; // skip \ for the sam reason like ' ('\\')
		case '[':
			return ']';
		case '_': // skip � and lower case letters (� can't be seen very well)
			return '{';
		case '~': // close circle and restart at !
			return '!';
		default:
			return (char) ((short) cur + 1);
		}
	}

	// we use only special characters to avoid collisions with existing objects
	// the first character must remain unchanged though because it can have a
	// special meaning
	public War3ID getunusedid(final War3ID substitutefor) {
		this.lastused = this.lastused.set(0, substitutefor.charAt(0));
		this.lastused = this.lastused.set(3, nextchar(substitutefor.charAt(3)));
		if (this.lastused.charAt(3) == '!') {
			this.lastused = this.lastused.set(2, nextchar(substitutefor.charAt(2)));
			if (this.lastused.charAt(2) == '!') {
				this.lastused = this.lastused.set(1, nextchar(substitutefor.charAt(1)));
			}
		}
		return this.lastused;
	}

	public void mergetable(final ObjectMap target, final ObjectMap targetCustom, final ObjectMap source,
			final CollisionHandling collisionHandling) {
		final Iterator<Map.Entry<War3ID, ObjectDataChangeEntry>> sourceObjectIterator = source.iterator();
		while (sourceObjectIterator.hasNext()) {
			final Map.Entry<War3ID, ObjectDataChangeEntry> sourceObject = sourceObjectIterator.next();
			if (target.containsKey(sourceObject.getKey())) {
				// obj.cpp: we have a collision
				War3ID oldId;
				War3ID replacementId;

				switch (collisionHandling) {
				case CREATE_NEW_ID:
					oldId = sourceObject.getKey();
					// obj.cpp: get new id until we finally have one that isn't used yet, or we're
					// out of ids
					replacementId = getunusedid(oldId);
					while (!((oldId.charAt(1) == '~') && (oldId.charAt(2) == '~') && (oldId.charAt(3) == '~'))
							&& targetCustom.containsKey(replacementId)) {
						oldId = replacementId;
						replacementId = getunusedid(oldId);
					}
					if (!((oldId.charAt(1) == '~') && (oldId.charAt(2) == '~') && (oldId.charAt(3) == '~'))) {
						sourceObject.getValue().setNewId(replacementId);
						targetCustom.put(replacementId, sourceObject.getValue().clone());
					}
					break;
				case REPLACE:
					// final ObjectDataChangeEntry deleteObject = target.get(sourceObject.getKey());
					target.put(sourceObject.getKey(), sourceObject.getValue().clone());
					break;
				default:// merge
					final ObjectDataChangeEntry targetObject = target.get(sourceObject.getKey());
					for (final Map.Entry<War3ID, List<Change>> sourceUnitField : sourceObject.getValue().getChanges()) {
						for (final Change sourceChange : sourceUnitField.getValue()) {
							List<Change> targetChanges = targetObject.getChanges().get(sourceUnitField.getKey());
							if (targetChanges == null) {
								targetChanges = new ArrayList<>();
							}
							Change bestTargetChange = null;
							for (final Change targetChange : targetChanges) {
								if (targetChange.getLevel() == sourceChange.getLevel()) {
									bestTargetChange = targetChange;
									break;
								}
							}
							if (bestTargetChange != null) {
								bestTargetChange.copyFrom(sourceChange);
							}
							else {
								targetChanges.add(sourceChange.clone());
								if (targetChanges.size() == 1) {
									targetObject.getChanges().add(sourceUnitField.getKey(), targetChanges);
								}
							}
						}
					}
					break;
				}
			}
			else {
				targetCustom.put(sourceObject.getKey(), sourceObject.getValue().clone());
			}
		}
	}

	public static enum CollisionHandling {
		CREATE_NEW_ID, REPLACE, MERGE;
	}

	public void merge(final War3ObjectDataChangeset obj, final CollisionHandling collisionHandling) {
		mergetable(this.original, this.custom, obj.original, collisionHandling);
		mergetable(this.original, this.custom, obj.custom, collisionHandling);
	}

	public int getvartype(final String name) {
		if ("int".equals(name) || "bool".equals(name)) {
			return 0;
		}
		else if ("real".equals(name)) {
			return 1;
		}
		else if ("unreal".equals(name)) {
			return 2;
		}
		return 3; // string
	}

	public boolean loadtable(final LittleEndianDataInputStream stream, final ObjectMap map, final boolean isOriginal,
			final WTS wts, final boolean inlineWTS) throws IOException {
		final War3ID noid = new War3ID(0);
		final StringBuilder stringBuilder = new StringBuilder();
		int ptr;
		final int count = stream.readInt();
		for (int i = 0; i < count; i++) {
			final long nanoTime = System.nanoTime();
			War3ID origid;
			War3ID newid = null;
			origid = readWar3ID(stream);
			ObjectDataChangeEntry existingObject;
			if (isOriginal) {
				if (noid.equals(origid)) {
					throw new IOException("the input stream might be screwed");
				}
				existingObject = map.get(origid);
				if (existingObject == null) {
					existingObject = new ObjectDataChangeEntry(origid, noid);
				}
				existingObject.setNewId(readWar3ID(stream));
			}
			else {
				newid = readWar3ID(stream);
				if (noid.equals(origid) || noid.equals(newid)) {
					throw new IOException("the input stream might be screwed");
				}
				existingObject = map.get(newid);
				if (existingObject == null) {
					existingObject = new ObjectDataChangeEntry(origid, newid);
				}
			}
			if (this.version >= 3) {
				final int reforged133JunkCount = stream.readInt();
				for (int reforged133JunkIndex = 0; reforged133JunkIndex < reforged133JunkCount; reforged133JunkIndex++) {
					final int reforgedJunk = stream.readInt();
				}
			}
			final int ccount = stream.readInt();// Retera: I assume this is change count?
			if ((ccount == 0) && isOriginal) {
				// throw new IOException("we seem to have reached the end of the stream and get
				// zeroes");
				System.err.println("we seem to have reached the end of the stream and get zeroes");
			}
			if (isOriginal) {
				debugprint("StandardUnit \"" + origid + "\" " + ccount + " {");
			}
			else {
				debugprint("CustomUnit \"" + origid + ":" + newid + "\" " + ccount + " {");
			}
			for (int j = 0; j < ccount; j++) {
				final War3ID chid = readWar3ID(stream);
				if (noid.equals(chid)) {
					throw new IOException("the input stream might be screwed");
				}
				if (!this.detected) {
					this.detected = detectKind(chid);
				}

				final Change newlyReadChange = new Change();
				newlyReadChange.setId(chid);
				newlyReadChange.setVartype(stream.readInt());
				debugprint("\t\"" + chid + "\" {");
				debugprint("\t\tType " + newlyReadChange.getVartype() + ",");
				if (extended()) {
					newlyReadChange.setLevel(stream.readInt());
					newlyReadChange.setDataptr(stream.readInt());
					debugprint("\t\tLevel " + newlyReadChange.getLevel() + ",");
					debugprint("\t\tData " + newlyReadChange.getDataptr() + ",");
				}

				switch (newlyReadChange.getVartype()) {
				case 0:
					newlyReadChange.setLongval(stream.readInt());
					debugprint("\t\tValue " + newlyReadChange.getLongval() + ",");
					break;
				case 3:
					ptr = 0;
					stringBuilder.setLength(0);
					int charRead;
					while ((charRead = stream.read()) != 0) {
						stringBuilder.append((char) charRead);
					}
					newlyReadChange.setStrval(stringBuilder.toString());
					if (inlineWTS && (newlyReadChange.getStrval().length() > 8)
							&& "TRIGSTR_".equals(newlyReadChange.getStrval().substring(0, 8))) {
						final int key = getWTSValue(newlyReadChange);
						newlyReadChange.setStrval(wts.get(key));
						if ((newlyReadChange.getStrval() != null)
								&& (newlyReadChange.getStrval().length() > MAX_STR_LEN)) {
							newlyReadChange.setStrval(newlyReadChange.getStrval().substring(0, MAX_STR_LEN - 1));
						}
					}
					debugprint("\t\tValue \"" + newlyReadChange.getStrval() + "\",");
					break;
				case 4:
					newlyReadChange.setBoolval(stream.readInt() == 1);
					debugprint("\t\tValue " + newlyReadChange.isBoolval() + ",");
					break;
				default:
					newlyReadChange.setRealval(stream.readFloat());
					debugprint("\t\tValue " + newlyReadChange.getRealval() + ",");
					break;
				}
				final War3ID crap = readWar3ID(stream);
				debugprint("\t\tExtra \"" + crap + "\",");
				newlyReadChange.setJunkDNA(crap);
				List<Change> existingChanges = existingObject.getChanges().get(chid);
				if (existingChanges == null) {
					existingChanges = new ArrayList<>();
				}
				Change bestTargetChange = null;
				for (final Change targetChange : existingChanges) {
					if (targetChange.getLevel() == newlyReadChange.getLevel()) {
						bestTargetChange = targetChange;
						break;
					}
				}
				if (bestTargetChange != null) {
					bestTargetChange.copyFrom(newlyReadChange);
				}
				else {
					existingChanges.add(newlyReadChange.clone());
					if (existingChanges.size() == 1) {
						existingObject.getChanges().add(chid, existingChanges);
					}
				}
				if (!crap.equals(existingObject.getOldId()) && !crap.equals(existingObject.getNewId())
						&& !crap.equals(noid)) {
					for (int charIndex = 0; charIndex < 4; charIndex++) {
						if ((crap.charAt(charIndex) < 32) || (crap.charAt(charIndex) > 126)) {
							return false;
						}
					}
				}
				debugprint("\t}");
			}
			debugprint("}");
			if ((newid == null) && !isOriginal) {
				throw new IllegalStateException("custom unit has no ID!");
			}
			map.put(isOriginal ? origid : newid, existingObject);
			final long endNanoTime = System.nanoTime();
			final long deltaNanoTime = endNanoTime - nanoTime;
		}
		return true;
	}

	private War3ID readWar3ID(final LittleEndianDataInputStream stream) throws IOException {
		return new War3ID(Integer.reverseBytes(stream.readInt()));
	}

	private static int getWTSValue(final Change change) {
		String numberAsText = change.getStrval().substring(8);
		while ((numberAsText.length() > 0) && (numberAsText.charAt(0) == '0')) {
			numberAsText = numberAsText.substring(1);
		}
		if (numberAsText.length() == 0) {
			return 0;
		}
		while (!Character.isDigit(numberAsText.charAt(numberAsText.length() - 1))) {
			numberAsText = numberAsText.substring(0, numberAsText.length() - 1);
		}
		return Integer.parseInt(numberAsText);
	}

	public boolean load(final LittleEndianDataInputStream stream, final WTS wts, final boolean inlineWTS)
			throws IOException {
		this.detected = false;
		this.version = stream.readInt();
		if ((this.version != 1) && (this.version != 2) && (this.version != 3)) {
			return false;
		}
		ObjectMap backup = this.original.clone();
		if (!loadtable(stream, this.original, true, wts, inlineWTS)) {
			this.original = backup;
			return false;
		}
		backup = this.custom.clone();
		if (!loadtable(stream, this.custom, false, wts, inlineWTS)) {
			this.original = backup;
			return false;
		}
		return true;
	}

	public boolean load(final File file, final WTS wts, final boolean inlineWTS) throws IOException {
		try (LittleEndianDataInputStream inputStream = new LittleEndianDataInputStream(new FileInputStream(file))) {
			final boolean result = load(inputStream, wts, inlineWTS);
			return result;
		}
	}

	public static void inlineWTSTable(final ObjectMap map, final WTS wts) {
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : map.entrySet()) {
			for (final Map.Entry<War3ID, List<Change>> changes : entry.getValue().getChanges()) {
				for (final Change change : changes.getValue()) {
					if ((change.getStrval().length() > 8) && "TRIGSTR_".equals(change.getStrval().substring(0, 8))) {
						final int key = getWTSValue(change);
						change.setStrval(wts.get(key));
						if (change.getStrval().length() > MAX_STR_LEN) {
							change.setStrval(change.getStrval().substring(0, MAX_STR_LEN - 1));
						}
					}
				}
			}
		}
	}

	public void inlineWTS(final WTS wts) {
		inlineWTSTable(this.original, wts);
		inlineWTSTable(this.custom, wts);
	}

	public void reset() {
		reset('u');
	}

	public void reset(final char expectedkind) {
		this.detected = false;
		this.kind = 'u';
		this.lastused = War3ID.fromString("u~~~");
		this.expected = expectedkind;
		this.original.clear();
		this.custom.clear();
	}

	public boolean saveTable(final LittleEndianDataOutputStream outputStream, final ObjectMap map,
			final boolean isOriginal) throws IOException {
		final CharsetEncoder encoder = Charset.forName("utf-8").newEncoder().onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE);
		final CharBuffer charBuffer = CharBuffer.allocate(1024);
		final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		final War3ID noid = new War3ID(0);
		int count;
		count = map.size();
		outputStream.writeInt(count);
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : map) {
			final ObjectDataChangeEntry cl = entry.getValue();
			int totalSize = 0;
			for (final Map.Entry<War3ID, List<Change>> changeEntry : cl.getChanges()) {
				totalSize += changeEntry.getValue().size();
			}
			if ((totalSize > 0) || !isOriginal) {
				ParseUtils.writeWar3ID(outputStream, cl.getOldId());
				ParseUtils.writeWar3ID(outputStream, cl.getNewId());
				if (this.version >= 3) {
					outputStream.writeInt(1);
					outputStream.writeInt(0);
				}
				count = totalSize;// cl.getChanges().size();
				outputStream.writeInt(count);
				for (final Map.Entry<War3ID, List<Change>> changes : entry.getValue().getChanges()) {
					for (final Change change : changes.getValue()) {
						ParseUtils.writeWar3ID(outputStream, change.getId());
						outputStream.writeInt(change.getVartype());
						if (extended()) {
							outputStream.writeInt(change.getLevel());
							outputStream.writeInt(change.getDataptr());
						}
						switch (change.getVartype()) {
						case 0:
							outputStream.writeInt(change.getLongval());
							break;
						case 3:
							charBuffer.clear();
							byteBuffer.clear();
							charBuffer.put(change.getStrval());
							charBuffer.flip();
							encoder.encode(charBuffer, byteBuffer, false);
							byteBuffer.flip();
							final byte[] stringBytes = new byte[byteBuffer.remaining() + 1];
							int i = 0;
							while (byteBuffer.hasRemaining()) {
								stringBytes[i++] = byteBuffer.get();
							}
							stringBytes[i] = 0;
							outputStream.write(stringBytes);
							break;
						case 4:
							outputStream.writeInt(change.isBoolval() ? 1 : 0);
							break;
						default:
							outputStream.writeFloat(change.getRealval());
							break;
						}
						// if (change.getJunkDNA() == null) {
						// saveWriteChars(outputStream, cl.getNewId().asStringValue().toCharArray());
						// } else {
						// saveWriteChars(outputStream,
						// change.getJunkDNA().asStringValue().toCharArray());
						// }
						// saveWriteChars(outputStream, cl.getNewId().asStringValue().toCharArray());
						ParseUtils.writeWar3ID(outputStream, noid);
					}
				}
			}
		}
		return true;
	}

	public boolean save(final LittleEndianDataOutputStream outputStream, final boolean generateWTS) throws IOException {
		if (generateWTS) {
			throw new UnsupportedOperationException("FAIL cannot generate WTS, needs more code");
		}
		this.version = 2;
		outputStream.writeInt(this.version);
		if (!saveTable(outputStream, this.original, true)) {
			throw new RuntimeException("Failed to save standard unit custom data");
		}
		if (!saveTable(outputStream, this.custom, false)) {
			throw new RuntimeException("Failed to save custom unit custom data");
		}
		return true;
	}

	public ObjectMap getOriginal() {
		return this.original;
	}

	public ObjectMap getCustom() {
		return this.custom;
	}

	private static void debugprint(final String s) {

	}
}
