/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt

import compasses.getrekt.commands.MuteCommands
import compasses.getrekt.commands.TempBanCommand
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.player.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Main : ModInitializer {
	val LOGGER: Logger = LoggerFactory.getLogger("Get Rekt")

	override fun onInitialize() {
		// todo: feedback should probably be reworked for cases like @a
		CommandRegistrationCallback.EVENT.register { dispatcher, registrationContext, registrationSelection ->
			MuteCommands.initialize(dispatcher)
			TempBanCommand.initialize(dispatcher)
		}
	}
}

fun MutableComponent.moderationMessage(): Component {
	return this.withStyle(ChatFormatting.GREEN)
}

fun Player.bypassesModeration(): Boolean {
	// todo: remove before release.
	if (FabricLoader.getInstance().isDevelopmentEnvironment) {
		return false
	}
	return hasPermissions(Commands.LEVEL_ADMINS)
}
