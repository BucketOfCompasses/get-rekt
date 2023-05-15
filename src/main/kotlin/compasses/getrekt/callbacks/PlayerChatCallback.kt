/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.callbacks

import compasses.getrekt.Event.PlayerChat
import compasses.getrekt.Main
import compasses.getrekt.filters.FilterType
import net.minecraft.network.protocol.game.ServerboundChatPacket
import net.minecraft.server.level.ServerPlayer
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object PlayerChatCallback {
	operator fun invoke(player: ServerPlayer, packet: ServerboundChatPacket, ci: CallbackInfo) {
		val filter = Main.firstFilterOrNull(packet.message(), FilterType.TEXT)
			?: return

		val event = PlayerChat(player, packet.message())

		if (!filter.action(event)) {
			ci.cancel()
		}
	}
}
