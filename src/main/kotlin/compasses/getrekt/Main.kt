/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.Command
import compasses.getrekt.filters.Filter
import compasses.getrekt.filters.FilterAction
import compasses.getrekt.filters.FilterType
import compasses.getrekt.filters.MatchType
import compasses.getrekt.storage.MuteStorage
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.ChatFormatting
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Main : ModInitializer {
	val LOGGER: Logger = LoggerFactory.getLogger("Get Rekt")

	override fun onInitialize() {
		LOGGER.info("Hello modded world!")

		CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
			dispatcher.register(
					Commands.literal("mute")
							.requires { it.hasPermission(Commands.LEVEL_ADMINS) }
							.then(Commands.argument("targets", GameProfileArgument.gameProfile())
									.executes { context ->
										val targets : Collection<GameProfile> = GameProfileArgument.getGameProfiles(context, "targets")

										var success = false

										for (target in targets) {
											val player = context.source.level.getPlayerByUUID(target.id)

											if (player == null) {
												context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.unknown_player", target.name ?: target.id))
											} else {
												if (MuteStorage.mute(player)) {
													context.source.sendSuccess(TranslationFallbacks.withFallback("get-rekt.command.muted", player.name.string), true)
													success = true
												} else {
													if (MuteStorage.isMuted(player)) {
														context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.already_muted", player.name.string))
													} else {
														context.source.sendSystemMessage(TranslationFallbacks.withFallback("get-rekt.command.not_muteable", player.name.string))
													}
												}
											}
										}

										return@executes if (success) Command.SINGLE_SUCCESS else 0
									})
			)
		}
	}

	// todo: find fixed places for these & actually implement.

	@JvmStatic
	fun firstFilterOrNull(raw: String, filterType: FilterType): Filter? {
		// NOTE: Sort by severity, use the most severe first

		if (raw.contains("beans") && filterType == FilterType.TEXT) {
			return Filter("beans", MatchType.CONTAINS, FilterAction.MuteAction, FilterType.TEXT)
		}

		return null
	}
}

fun MutableComponent.moderationMessage(): Component {
	return this.withStyle(ChatFormatting.GREEN)
}
