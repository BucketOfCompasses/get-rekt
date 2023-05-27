/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.mixin;

import compasses.getrekt.storage.MuteStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MsgCommand.class)
public abstract class MessageCommandMixin {
	@Inject(
			method = "sendMessage(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/Collection;Lnet/minecraft/network/chat/PlayerChatMessage;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void getrekt$beforeMessageSend(CommandSourceStack source, Collection<ServerPlayer> targets, PlayerChatMessage message, CallbackInfo ci) {
		if (source.isPlayer() && MuteStorage.INSTANCE.isMuted(source.getPlayer())) {
			ci.cancel();
		}
	}
}
