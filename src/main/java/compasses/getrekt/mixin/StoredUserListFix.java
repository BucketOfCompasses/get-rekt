/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.mixin;

import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

// https://bugs.mojang.com/browse/MC-158900
@Mixin(StoredUserList.class)
public abstract class StoredUserListFix<K, V extends StoredUserEntry<K>> {
	@Shadow
	@Final
	private Map<String, V> map;

	@Shadow
	protected abstract String getKeyForUser(K obj);

	@Shadow
	public abstract void remove(K user);

	/**
	 * @author Compasses (Get Rekt)
	 * @reason Code optimization & method intention fix.
	 */
	@Overwrite
	public boolean contains(K entry) { // Target method is protected... idk why we have to declare public here.
		String key = getKeyForUser(entry);
		V value = map.get(key);

		if (value != null) {
			if (!value.hasExpired()) {
				return true;
			}

			remove(entry);
		}

		return false;
	}

	/**
	 * @author Compasses (Get Rekt)
	 * @reason Code optimization & other method intention fix.
	 */
	@Overwrite
	private void removeExpired() {

	}
}
