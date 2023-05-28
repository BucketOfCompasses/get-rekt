/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.player.Player

internal fun MutableComponent.moderationMessage(): Component {
	return this.withStyle(ChatFormatting.GREEN)
}

internal fun Player.bypassesModeration(): Boolean {
	// todo: remove before release.
	if (FabricLoader.getInstance().isDevelopmentEnvironment) {
		return false
	}
	return hasPermissions(Commands.LEVEL_ADMINS)
}

internal fun <Source> CommandDispatcher<Source>.register(
		name: String,
		action: LiteralArgumentBuilder<Source>.() -> Unit
) {
	val argument = LiteralArgumentBuilder.literal<Source>(name)
	action.invoke(argument)
	register(argument)
}

internal fun <Source, Type2> ArgumentBuilder<Source, *>.requiredArgument(
		name: String,
		type: ArgumentType<Type2>,
		action: ArgumentBuilder<Source, *>.() -> Unit
) {
	val argument = RequiredArgumentBuilder.argument<Source, Type2>(name, type)
	action.invoke(argument)
	then(argument)
}


internal fun <Source, Builder : ArgumentBuilder<Source, Builder>> ArgumentBuilder<Source, Builder>.runs(
		action: CommandContext<Source>.() -> Int
) {
	executes {
		action.invoke(it)
	}
}
