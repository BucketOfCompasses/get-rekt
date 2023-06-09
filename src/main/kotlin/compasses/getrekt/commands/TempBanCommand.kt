/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.commands

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import compasses.getrekt.*
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.server.players.UserBanListEntry
import java.lang.IllegalArgumentException

object TempBanCommand {
	private val ALREADY_BANNED_ERROR = SimpleCommandExceptionType(Component.translatable("commands.ban.failed"))

	fun initialize(dispatcher: CommandDispatcher<CommandSourceStack>) {
		dispatcher.register("tempban") {
			requires { it.hasPermission(Commands.LEVEL_ADMINS) }

			requiredArgument("targets", GameProfileArgument.gameProfile()) {

				requiredArgument("reason / duration", StringArgumentType.greedyString()) {
					runs {
						val targets: Collection<GameProfile> = GameProfileArgument.getGameProfiles(this, "targets")

						val reasonDurationPair = StringArgumentType.getString(this, "reason / duration")

						val commandActor = try {
							CommandActor(targets, reasonDurationPair, false)
						} catch (exception : IllegalArgumentException) {
								source.sendSystemMessage(Translations.get("get-rekt.command.failed_find_duration"))
								return@runs 0
						}

						val banList = source.server.playerList.bans

						val bans = commandActor.act { target ->
							if (!banList.isBanned(target)) {
								val entry = UserBanListEntry(target, commandActor.startDate, source.textName, commandActor.endDate, commandActor.reason)
								banList.add(entry)

								source.sendSuccess({ Component.translatable("commands.ban.success", ComponentUtils.getDisplayName(target), commandActor.reason) }, true)

								val player = source.server.playerList.getPlayer(target.id)

								val banMessage = if (commandActor.reason.isBlank()) Component.translatable("multiplayer.disconnect.banned") else Component.translatable("multiplayer.disconnect.banned.reason", commandActor.reason)
								banMessage.append(Translations.get("get-rekt.multiplayer.banned.duration", commandActor.durationString()))

								player?.connection?.disconnect(banMessage)

								return@act true
							}

							return@act false
						}

						if (bans == 0) {
							throw ALREADY_BANNED_ERROR.create()
						}

						return@runs bans
					}
				}
			}
		}
	}
}
