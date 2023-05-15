/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.mixin.callbacks

import compasses.getrekt.Event.PlayerChat
import compasses.getrekt.Main
import compasses.getrekt.filters.FilterType
import net.minecraft.network.protocol.game.ServerboundChatPacket
import net.minecraft.server.level.ServerPlayer

object PlayerChatCallback {
	operator fun invoke(player: ServerPlayer, packet: ServerboundChatPacket) : Boolean {
		val filter = Main.firstFilterOrNull(packet.message(), FilterType.TEXT)
			?: return false

		val event = PlayerChat(player, packet.message())

		return !filter.action(event)
	}
}
