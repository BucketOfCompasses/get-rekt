/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.storage

import compasses.getrekt.Translations
import compasses.getrekt.bypassesModeration
import compasses.getrekt.moderationMessage
import net.minecraft.world.entity.player.Player
import java.util.Date
import java.util.UUID

object MuteStorage {
	// todo: rework like vanilla bans so it can have start & end date, banner name & reason information
	private val players : MutableSet<UUID> = mutableSetOf()

	fun isMuted(player: Player): Boolean {
		return players.contains(player.uuid)
	}

	fun mute(player: Player, startDate: Date, endDate: Date?, textName: String, reason: String) : Boolean {
		if (player.bypassesModeration()) {
			return false
		}
		if (players.add(player.uuid)) {
			player.sendSystemMessage(Translations.get("get-rekt.action.muted").moderationMessage())
			return true
		}
		return false
	}

	fun unmute(player: Player) : Boolean {
		if (player.bypassesModeration()) {
			return false
		}
		if (players.remove(player.uuid)) {
			player.sendSystemMessage(Translations.get("get-rekt.action.unmuted").moderationMessage())
			return true
		}
		return false
	}
}
