package com.nyrds.pixeldungeon.mechanics.ablities;

import java.util.Set;

/**
 * Created by mike on 11.02.2017.
 * This file is part of Remixed Pixel Dungeon.
 */
public interface Abilities {
	Set<Class<?>> immunities();
	Set<Class<?>> resistances();

	int hasteBonus();

	int dewBonus();
}
