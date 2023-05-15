package compasses.getrekt.storage

import compasses.getrekt.TranslationFallbacks
import compasses.getrekt.bypassesModeration
import compasses.getrekt.moderationMessage
import net.minecraft.world.entity.player.Player
import java.util.UUID

object MuteStorage {
	private val players : MutableSet<UUID> = mutableSetOf()

	fun isMuted(player: Player): Boolean {
		return players.contains(player.uuid)
	}

	fun mute(player: Player) : Boolean {
		if (player.bypassesModeration()) {
			return false
		}
		if (players.add(player.uuid)) {
			player.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.action.muted").moderationMessage())
			return true
		}
		return false
	}

	fun unmute(player: Player) : Boolean {
		if (player.bypassesModeration()) {
			return false
		}
		if (players.remove(player.uuid)) {
			player.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.action.unmuted").moderationMessage())
			return true
		}
		return false
	}
}
