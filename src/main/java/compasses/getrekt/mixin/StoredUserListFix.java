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
public abstract class StoredUserListFix<User, Entry extends StoredUserEntry<User>> {
	@Shadow
	@Final
	private Map<String, Entry> map;

	@Shadow
	protected abstract String getKeyForUser(User user);

	@Shadow
	public abstract void remove(User user);

	/**
	 * @author Compasses (Get Rekt)
	 * @reason Code optimization & method intention fix.
	 */
	@Overwrite
	public boolean contains(User user) { // Target method is protected... idk why we have to declare public here.
		Entry entry = map.get(getKeyForUser(user));

		if (entry != null) {
			if (!entry.hasExpired()) {
				return true;
			}

			remove(user);
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
