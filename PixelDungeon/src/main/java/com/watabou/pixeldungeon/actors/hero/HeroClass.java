/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.pixeldungeon.actors.hero;

import android.support.annotation.NonNull;

import com.nyrds.android.util.TrackedRuntimeException;
import com.nyrds.pixeldungeon.items.artifacts.CandleOfMindVision;
import com.nyrds.pixeldungeon.items.common.armor.NecromancerArmor;
import com.nyrds.pixeldungeon.items.common.armor.NecromancerRobe;
import com.nyrds.pixeldungeon.items.food.RottenPumpkinPie;
import com.nyrds.pixeldungeon.items.guts.weapon.melee.Halberd;
import com.nyrds.pixeldungeon.mechanics.ablities.Abilities;
import com.nyrds.pixeldungeon.mechanics.ablities.Ordinary;
import com.nyrds.pixeldungeon.ml.BuildConfig;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.TomeOfMastery;
import com.watabou.pixeldungeon.items.armor.ClassArmor;
import com.watabou.pixeldungeon.items.armor.ClothArmor;
import com.watabou.pixeldungeon.items.armor.ElfArmor;
import com.watabou.pixeldungeon.items.armor.HuntressArmor;
import com.watabou.pixeldungeon.items.armor.MageArmor;
import com.watabou.pixeldungeon.items.armor.PlateArmor;
import com.watabou.pixeldungeon.items.armor.RogueArmor;
import com.watabou.pixeldungeon.items.armor.WarriorArmor;
import com.watabou.pixeldungeon.items.keys.SkeletonKey;
import com.watabou.pixeldungeon.items.potions.PotionOfHealing;
import com.watabou.pixeldungeon.items.potions.PotionOfMindVision;
import com.watabou.pixeldungeon.items.potions.PotionOfStrength;
import com.watabou.pixeldungeon.items.rings.RingOfAccuracy;
import com.watabou.pixeldungeon.items.rings.RingOfShadows;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfCurse;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.watabou.pixeldungeon.items.wands.WandOfBlink;
import com.watabou.pixeldungeon.items.wands.WandOfMagicMissile;
import com.watabou.pixeldungeon.items.weapon.melee.BattleAxe;
import com.watabou.pixeldungeon.items.weapon.melee.Glaive;
import com.watabou.pixeldungeon.items.weapon.melee.ShortSword;
import com.watabou.pixeldungeon.items.weapon.melee.Spear;
import com.watabou.pixeldungeon.items.weapon.melee.WoodenBow;
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang;
import com.watabou.pixeldungeon.items.weapon.missiles.CommonArrow;
import com.watabou.pixeldungeon.ui.QuickSlot;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;

public enum HeroClass {

	WARRIOR(Game.getVar(R.string.HeroClass_War),WarriorArmor.class, Ordinary.instance),
	MAGE(Game.getVar(R.string.HeroClass_Mag),MageArmor.class, Ordinary.instance),
	ROGUE(Game.getVar(R.string.HeroClass_Rog),RogueArmor.class, Ordinary.instance),
	HUNTRESS(Game.getVar(R.string.HeroClass_Hun),HuntressArmor.class, Ordinary.instance),
	ELF(Game.getVar(R.string.HeroClass_Elf),ElfArmor.class, Ordinary.instance),
	NECROMANCER(Game.getVar(R.string.HeroClass_Necromancer),NecromancerArmor.class, Ordinary.instance);

	private final Class<? extends ClassArmor> armorClass;

	private String title;
	private Abilities abilities;


	private static final String[] WAR_PERKS = Game
			.getVars(R.array.HeroClass_WarPerks);
	private static final String[] MAG_PERKS = Game
			.getVars(R.array.HeroClass_MagPerks);
	private static final String[] ROG_PERKS = Game
			.getVars(R.array.HeroClass_RogPerks);
	private static final String[] HUN_PERKS = Game
			.getVars(R.array.HeroClass_HunPerks);
	private static final String[] ELF_PERKS = Game
			.getVars(R.array.HeroClass_ElfPerks);
	private static final String[] NECROMANCER_PERKS = Game
			.getVars(R.array.HeroClass_NecromancerPerks);

	HeroClass(String title, Class<? extends ClassArmor> armorClass, Abilities abilities) {
		this.title = title;
		this.armorClass = armorClass;
		this.abilities = abilities;
	}

	public void initHero(Hero hero) {
		hero.heroClass = this;
		initCommon(hero);

		switch (this) {
		case WARRIOR:
			initWarrior(hero);
			break;

		case MAGE:
			initMage(hero);
			break;

		case ROGUE:
			initRogue(hero);
			break;

		case HUNTRESS:
			initHuntress(hero);
			break;

		case ELF:
			initElf(hero);
			break;

		case NECROMANCER:
			initNecromancer(hero);
			break;
		}

		hero.setGender(getGender());

		if (Badges.isUnlocked(masteryBadge()) && hero.getDifficulty() < 3) {
			{
				if( hero.heroClass != HeroClass.NECROMANCER) {
					new TomeOfMastery().collect(hero);
				}
			}
		}
		hero.updateAwareness();
	}

	private static void initDebug(Hero hero) {
		for(int i = 0;i<100;i++) {
			hero.collect(new ScrollOfMagicMapping());
			hero.collect(new ScrollOfUpgrade());
			hero.collect(new ScrollOfCurse());
			hero.collect(new PotionOfMindVision());
		}

		Item ring  = new RingOfAccuracy().identify();
		ring.cursed = true;
		hero.collect(ring);
		hero.collect(new RingOfAccuracy().identify());
		hero.collect(new CandleOfMindVision());
		hero.collect(new WandOfBlink().identify().upgrade(5));
		hero.collect(new TomeOfMastery());
		hero.collect(new Spear().identify().upgrade(100));
		hero.collect(new BattleAxe());

		hero.collect(new PlateArmor().identify().upgrade(9));
		hero.collect(new Glaive());
		hero.collect(new Halberd());

		hero.collect(new RottenPumpkinPie());

		hero.ht(1000);
		hero.hp(1000);
		hero.attackSkill = 1000;

		Badges.validateBossSlain(Badges.Badge.LICH_SLAIN);
		//hero.defenseSkill = 1000;
	}

	private static void initCommon(Hero hero) {
		(hero.belongings.armor = new ClothArmor()).identify();
		if(BuildConfig.DEBUG) initDebug(hero);
		QuickSlot.cleanStorage();
		Dungeon.gold(Dungeon.gold() + 200);
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
		case WARRIOR:
			return Badges.Badge.MASTERY_WARRIOR;
		case MAGE:
			return Badges.Badge.MASTERY_MAGE;
		case ROGUE:
			return Badges.Badge.MASTERY_ROGUE;
		case HUNTRESS:
			return Badges.Badge.MASTERY_HUNTRESS;
		case ELF:
			return Badges.Badge.MASTERY_ELF;
		case NECROMANCER:
			return Badges.Badge.MASTERY_NECROMANCER;
		}
		return null;
	}

	private static void initWarrior(Hero hero) {
		hero.STR(hero.STR() + 1);

		(hero.belongings.weapon = new ShortSword()).identify();

		new PotionOfStrength().setKnown();
	}

	private static void initMage(Hero hero) {
		WandOfMagicMissile wand = new WandOfMagicMissile();
		hero.collect(wand.identify());

		QuickSlot.selectItem(wand, 0);

		new ScrollOfIdentify().setKnown();
	}

	private static void initRogue(Hero hero) {
		(hero.belongings.ring1 = new RingOfShadows()).upgrade().identify();

		hero.belongings.ring1.activate(hero);

		new ScrollOfMagicMapping().setKnown();
	}

	private static void initHuntress(Hero hero) {
		hero.ht(hero.ht() - 5);
		hero.hp(hero.ht());

		Boomerang boomerang = new Boomerang();
		hero.collect(boomerang.identify());

		QuickSlot.selectItem(boomerang, 0);
	}

	private void initElf(Hero hero) {
		hero.STR(hero.STR() - 1);

		hero.ht(hero.ht() - 5);
		hero.hp(hero.ht());

		(hero.belongings.weapon = new WoodenBow()).upgrade().identify();

		hero.collect(new CommonArrow(20));

		QuickSlot.selectItem(CommonArrow.class, 0);
	}

	private static void initNecromancer(Hero hero) {
		hero.ht(hero.ht() - 5);
		hero.hp(hero.ht());

		(hero.belongings.armor = new NecromancerRobe()).identify();

		SkeletonKey key = SkeletonKey.makeNewKey("7", 7);
		hero.collect(key);

		new PotionOfHealing().setKnown();
	}

	public String title() {
		return title;
	}

	@NonNull
	public String[] perks() {

		switch (this) {
		case WARRIOR:
			return WAR_PERKS;
		case MAGE:
			return MAG_PERKS;
		case ROGUE:
		default:
			return ROG_PERKS;
		case HUNTRESS:
			return HUN_PERKS;
		case ELF:
			return ELF_PERKS;
		case NECROMANCER:
			return NECROMANCER_PERKS;
		}
	}

	public int getGender() {
		switch (this) {
		case WARRIOR:
		case MAGE:
		case ROGUE:
		case ELF:
		case NECROMANCER:
			return Utils.MASCULINE;
		case HUNTRESS:
			return Utils.FEMININE;
		}
		return Utils.NEUTER;
	}

	private static final String CLASS = "class";

	public void storeInBundle(Bundle bundle) {
		bundle.put(CLASS, toString());
	}

	public static HeroClass restoreFromBundle(Bundle bundle) {
		String value = bundle.getString(CLASS);
		return value.length() > 0 ? valueOf(value) : ROGUE;
	}

	public ClassArmor classArmor() {
		try {
			return armorClass.newInstance();
		} catch (Exception e) {
			throw new TrackedRuntimeException(e);
		}
	}

	Abilities getAbilities() {
		return abilities;
	}
}
