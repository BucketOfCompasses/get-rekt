/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.filters

import compasses.getrekt.Main
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.UserBanListEntry

enum class FilterAction(val func: (ServerPlayer, String?, String) -> Unit) {
	MUTE({ player, oldText, newText ->
		Main.mutePlayer(player.uuid)

		// todo: send webhook message
	}),
	KICK({ player, oldText, newText ->
		player.connection.disconnect(Component.translatable("getrekt.multiplayer.kicked"))

		// todo: send webhook message
	}),
	BAN({ player, oldText, newText ->
		val banList = player.server.playerList.bans

		banList.add(UserBanListEntry(player.gameProfile, null, "Get Rekt: Filter", null,  "Banned by Get Rekt."))

		player.connection.disconnect(Component.translatable("getrekt.multiplayer.banned"))

		// todo: send webhook message
	});

	fun action(player: ServerPlayer, oldText: String?, newText: String) {
		func(player, oldText, newText)
	}
}
