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
