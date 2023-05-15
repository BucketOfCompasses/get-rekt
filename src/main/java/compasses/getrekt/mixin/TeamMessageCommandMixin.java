/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.mixin;

import compasses.getrekt.storage.MuteStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TeamMsgCommand.class)
public class TeamMessageCommandMixin {
	@Inject(
			method = "sendMessage(Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/scores/PlayerTeam;Ljava/util/List;Lnet/minecraft/network/chat/PlayerChatMessage;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void getrekt$beforeMessageSend(CommandSourceStack source, Entity sender, PlayerTeam team, List<ServerPlayer> teamMembers, PlayerChatMessage chatMessage, CallbackInfo ci) {
		if (source.isPlayer() && MuteStorage.INSTANCE.isMuted(source.getPlayer())) {
			ci.cancel();
		}
	}
}
