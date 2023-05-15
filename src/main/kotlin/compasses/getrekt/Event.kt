/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer

// todo: probably move
sealed class Event(open val player: ServerPlayer) {
	data class SignUpdate(
		override val player: ServerPlayer,
		val pos: BlockPos,
		val oldText: String,
		val newText: String
	) : Event(player)

	data class PlayerChat(
		override val player: ServerPlayer,
		val text: String
	) : Event(player)
}
