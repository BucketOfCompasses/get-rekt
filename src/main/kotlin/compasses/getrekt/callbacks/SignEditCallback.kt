/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.callbacks

import compasses.getrekt.Event
import compasses.getrekt.Main
import compasses.getrekt.filters.FilterType
import compasses.getrekt.storage.MuteStorage
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.FilteredText
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object SignEditCallback {
	operator fun invoke(
		level: Level?,

		pos: BlockPos,
		state: BlockState,

		player: Player,

		oldSignData: Array<Component>,
		newSignData: List<FilteredText>,

		ci: CallbackInfo
	) {
		level ?: return

		val serverPlayer = player as? ServerPlayer
			?: return

		val oldText = oldSignData.joinToString("\n") { it.string }

		if (MuteStorage.isMuted(serverPlayer)) {
			if (oldText.isBlank()) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
			} else {
				level.sendBlockUpdated(pos, state, state, 3)
			}

			ci.cancel()
			return
		}

		// todo: check if FakePlayer, try get owning player, log action if filter matches
		// val fp : FakePlayer? = serverPlayer as? FakePlayer
		// val owner : ServerPlayer? = serverPlayer.server.playerList.players.firstOrNull {
		//  it.uuid == fp?.gameProfile?.id
		// }

		val newText = newSignData.joinToString("\n") { it.raw }

		val filter = Main.firstFilterOrNull(newText, FilterType.TEXT)
			?: return

		val event = Event.SignUpdate(serverPlayer, pos, oldText, newText)

		if (!filter.action(event)) {
			if (oldText.isBlank()) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
			} else {
				level.sendBlockUpdated(pos, state, state, 3)
			}

			ci.cancel()
		}
	}
}
