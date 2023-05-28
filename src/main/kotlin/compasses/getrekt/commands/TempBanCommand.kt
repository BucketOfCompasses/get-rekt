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
import java.time.temporal.ChronoUnit
import java.util.Date

object TempBanCommand {
	private const val DURATION_REGEX = "([1-9][0-9]*) (years|months|days|hours|minutes|year|month|day|hour|minute)"
	private val ALREADY_BANNED_ERROR = SimpleCommandExceptionType(Component.translatable("commands.ban.failed"))

	fun initialize(dispatcher: CommandDispatcher<CommandSourceStack>) {
		dispatcher.register("tempban") {
			requires { it.hasPermission(Commands.LEVEL_ADMINS) }

			requiredArgument("targets", GameProfileArgument.gameProfile()) {

				requiredArgument("reason / duration", StringArgumentType.greedyString()) {
					runs {
						val targets: Collection<GameProfile> = GameProfileArgument.getGameProfiles(this, "targets")

						val reasonDurationPair = StringArgumentType.getString(this, "reason / duration")
						val matches = DURATION_REGEX.toRegex().findAll(reasonDurationPair)
						var banDuration : MatchResult? = null



						for (match in matches.toList().reversed()) {
							if (match.range.first == 0 || match.range.last == reasonDurationPair.length - 1) {
								banDuration = match
								break
							}
						}

						if (banDuration == null) {
							source.sendSystemMessage(Translations.get("get-rekt.command.failed_find_duration"))
							return@runs 0
						}

						val banStartDate = Date()

						val duration = banDuration.groupValues[1].toLong()

						val unit = when (banDuration.groupValues.last().lowercase()) {
							"minute", "minutes" -> ChronoUnit.MINUTES
							"hour", "hours" -> ChronoUnit.HOURS
							"day", "days" -> ChronoUnit.DAYS
							"week", "weeks" -> ChronoUnit.WEEKS
							"month", "months" -> ChronoUnit.MONTHS
							"year", "years" -> ChronoUnit.YEARS
							else -> throw IllegalStateException("Unreachable code.")
						}

						val banEndDate = Date.from(banStartDate.toInstant().plus(duration, unit))

						val banReason = reasonDurationPair.removeRange(banDuration.range).trim()

						var bans = 0
						val banList = source.server.playerList.bans

						for (target in targets) {
							if (!banList.isBanned(target)) {
								val entry = UserBanListEntry(target, banStartDate, source.textName, banEndDate, banReason)
								banList.add(entry)

								bans++
								source.sendSuccess({ Component.translatable("commands.ban.success", ComponentUtils.getDisplayName(target), banReason) }, true)

								val player = source.server.playerList.getPlayer(target.id)
								player?.connection?.disconnect(Component.translatable("multiplayer.disconnect.banned"))
							}
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
