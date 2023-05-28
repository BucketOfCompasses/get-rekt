/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.commands

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import compasses.getrekt.TranslationFallbacks
import compasses.getrekt.bypassesModeration
import compasses.getrekt.storage.MuteStorage
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.GameProfileArgument

object MuteCommands {
	fun initialize(dispatcher: CommandDispatcher<CommandSourceStack>) {
		dispatcher.register(
				Commands.literal("mute")
						.requires { it.hasPermission(Commands.LEVEL_ADMINS) }
						.then(Commands.argument("targets", GameProfileArgument.gameProfile())
								.executes { context ->
									val targets: Collection<GameProfile> = GameProfileArgument.getGameProfiles(context, "targets")

									var success = false

									for (target in targets) {
										val player = context.source.level.getPlayerByUUID(target.id)

										if (player == null) {
											context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.unknown_player", target.name
													?: target.id))
										} else {
											if (MuteStorage.mute(player)) {
												context.source.sendSuccess({ TranslationFallbacks.withFallback("get-rekt.command.muted", player.name.string) }, true)
												success = true
											} else {
												if (player.bypassesModeration()) {
													context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.not_muteable", player.name.string))
												} else {
													context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.already_muted", player.name.string))
												}
											}
										}
									}

									return@executes if (success) Command.SINGLE_SUCCESS else 0
								})
		)

		dispatcher.register(
				Commands.literal("unmute")
						.requires { it.hasPermission(Commands.LEVEL_ADMINS) }
						.then(Commands.argument("targets", GameProfileArgument.gameProfile())
								.executes { context ->
									val targets: Collection<GameProfile> = GameProfileArgument.getGameProfiles(context, "targets")

									var success = false

									for (target in targets) {
										val player = context.source.level.getPlayerByUUID(target.id)

										if (player == null) {
											context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.unknown_player", target.name
													?: target.id))
										} else {
											if (MuteStorage.unmute(player)) {
												context.source.sendSuccess({ TranslationFallbacks.withFallback("get-rekt.command.unmuted", player.name.string) }, true)
												success = true
											} else {
												if (player.bypassesModeration()) {
													context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.not_muteable", player.name.string))
												} else {
													context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.already_not_muted", player.name.string))
												}
											}
										}
									}

									return@executes if (success) Command.SINGLE_SUCCESS else 0
								})
		)
	}
}
