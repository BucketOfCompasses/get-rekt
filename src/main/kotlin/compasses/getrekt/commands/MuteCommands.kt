/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.commands

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import compasses.getrekt.*
import compasses.getrekt.storage.MuteStorage
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.GameProfileArgument
import java.lang.IllegalArgumentException

object MuteCommands {
	fun initialize(dispatcher: CommandDispatcher<CommandSourceStack>) {
		dispatcher.register("mute") {
			requires { it.hasPermission(Commands.LEVEL_ADMINS) }

			requiredArgument("targets", GameProfileArgument.gameProfile()) {
				runs {
					val targets: Collection<GameProfile> = GameProfileArgument.getGameProfiles(this, "targets")

					val reasonDurationPair = StringArgumentType.getString(this, "reason / duration")

					val commandActor = try {
						CommandActor(targets, reasonDurationPair, true)
					} catch (exception: IllegalArgumentException) {
						source.sendSystemMessage(Translations.get("get-rekt.command.failed_find_duration"))
						return@runs 0
					}

					val mutes = commandActor.act { target ->
						val player = source.level.getPlayerByUUID(target.id)

						if (player == null) {
							source.sendSystemMessage(Translations.get("get-rekt.command.unknown_player", target.name ?: target.id))
						} else {
							if (MuteStorage.mute(player, commandActor.startDate, commandActor.endDate, source.textName, commandActor.reason)) {
								source.sendSuccess({ Translations.get("get-rekt.command.muted", player.name.string) }, true)

								return@act true
							} else {
								if (player.bypassesModeration()) {
									source.sendSystemMessage(Translations.get("get-rekt.command.not_muteable", player.name.string))
								} else {
									source.sendSystemMessage(Translations.get("get-rekt.command.already_muted", player.name.string))
								}
							}
						}

						return@act false
					}

					return@runs mutes
				}
			}
		}

		dispatcher.register("unmute") {
			requires { it.hasPermission(Commands.LEVEL_ADMINS) }

			requiredArgument("targets", GameProfileArgument.gameProfile()) {

				runs {
					val targets: Collection<GameProfile> = GameProfileArgument.getGameProfiles(this, "targets")

					var success = false

					for (target in targets) {
						val player = source.level.getPlayerByUUID(target.id)

						if (player == null) {
							source.sendSystemMessage(Translations.get("get-rekt.command.unknown_player", target.name
									?: target.id))
						} else {
							if (MuteStorage.unmute(player)) {
								source.sendSuccess({ Translations.get("get-rekt.command.unmuted", player.name.string) }, true)
								success = true
							} else {
								if (player.bypassesModeration()) {
									source.sendSystemMessage(Translations.get("get-rekt.command.not_muteable", player.name.string))
								} else {
									source.sendSystemMessage(Translations.get("get-rekt.command.already_not_muted", player.name.string))
								}
							}
						}
					}

					return@runs if (success) Command.SINGLE_SUCCESS else 0
				}
			}
		}
	}
}
