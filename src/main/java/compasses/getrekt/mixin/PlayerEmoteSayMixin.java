package compasses.getrekt.mixin;

import compasses.getrekt.storage.MuteStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerEmoteSayMixin {

	@Inject(
			method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/network/chat/ChatType$Bound;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void getrekt$beforeMessageBroadcast(PlayerChatMessage message, CommandSourceStack sender, ChatType.Bound boundChatType, CallbackInfo ci) {
		//noinspection DataFlowIssue
		if (sender.isPlayer() && MuteStorage.INSTANCE.isMuted(sender.getPlayer())) {
			ci.cancel();
		}
	}
}
