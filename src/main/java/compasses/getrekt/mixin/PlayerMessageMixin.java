/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.mixin;

import compasses.getrekt.Main;
import compasses.getrekt.filters.Filter;
import compasses.getrekt.filters.FilterType;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class PlayerMessageMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(
			method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;tryHandleChat(Ljava/lang/String;Ljava/time/Instant;Lnet/minecraft/network/chat/LastSeenMessages$Update;)Ljava/util/Optional;"
			),
			cancellable = true
	)
	private void getrekt$beforeChatSent(ServerboundChatPacket packet, CallbackInfo ci) {
		Filter filter = Main.firstFilterOrNull(packet.message(), FilterType.TEXT);

		if (filter != null) {
			filter.action(player, null, packet.message());
			ci.cancel();
		}
	}
}
