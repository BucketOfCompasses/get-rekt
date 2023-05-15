package compasses.getrekt.storage

import compasses.getrekt.TranslationFallbacks
import compasses.getrekt.moderationMessage
import net.minecraft.commands.Commands
import net.minecraft.world.entity.player.Player
import java.util.UUID

object MuteStorage {
	private val players : MutableSet<UUID> = mutableSetOf()

	fun isMuted(player: Player): Boolean {
		return players.contains(player.uuid)
	}

	fun mute(player: Player) : Boolean {
		if (player.hasPermissions(Commands.LEVEL_ADMINS)) {
			return false
		}
		val added = players.add(player.uuid)
		if (added) {
			player.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.action.muted").moderationMessage())
		}
		return added
	}

	fun unmute(player: Player) {
		if (players.remove(player.uuid)) {
			player.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.action.unmuted").moderationMessage())
		}
	}
}
