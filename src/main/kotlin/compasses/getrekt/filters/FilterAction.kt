/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.filters

import compasses.getrekt.Event
import compasses.getrekt.Translations
import compasses.getrekt.storage.MuteStorage
import kotlinx.serialization.Serializable
import net.minecraft.server.players.UserBanListEntry
import java.util.*

/**
 * Sealed class representing a filter action.
 *
 * Subtypes implement specific action behaviors.
 */
@Serializable
sealed class FilterAction {
	/** For ordering - higher numbers mean more severe actions. **/
	abstract val severity: Int

	/**
	 * Callback to be run when this filter action happens.
	 *
	 * @param event Event that triggered this action
	 *
	 * @return Whether to allow the action to proceed - `true` to allow, `false` to cancel
	 */
	abstract operator fun invoke(event: Event): Boolean

	companion object {
		/** Set containing all possible action types. **/
		val actions = linkedSetOf(MuteAction, BanAction, KickAction, CancelAction, LogOnlyAction)
	}

	/** Banning filter action - ban the user when this action is triggered. **/
	object BanAction : FilterAction() {
		override val severity: Int = 4

		override fun invoke(event: Event): Boolean {
			val banList = event.player.server.playerList.bans

			banList.add(
				UserBanListEntry(
					event.player.gameProfile,

					null,                    // When the ban was created
					"Get Rekt: Filter",     // Source of the ban
					null,                   // When the ban expires
					"Banned by Get Rekt."  // The ban reason
				)
			)

			event.player.connection.disconnect(Translations.get("get-rekt.multiplayer.banned"))

			return false
		}
	}

	/** Kicking filter action - kick the user when this action is triggered. **/
	object KickAction : FilterAction() {
		override val severity: Int = 3

		override fun invoke(event: Event): Boolean {
			event.player.connection.disconnect(Translations.get("get-rekt.multiplayer.kicked"))

			return false
		}
	}

	/** Muting filter action - mute the user when this action is triggered. **/
	object MuteAction : FilterAction() {
		override val severity: Int = 2

		override fun invoke(event: Event): Boolean {
			// todo: Make it so MuteAction can be configured with a mute duration e.g. 5 minutes
			MuteStorage.mute(event.player, Date(), null, "Get Rekt: Filter", "Muted by Get Rekt.")

			return false
		}
	}

	/** Canceling filter action - cancel the action the user took, but don't do anything else. **/
	object CancelAction : FilterAction() {
		override val severity: Int = 1

		override fun invoke(event: Event): Boolean {
			return false
		}
	}

	// todo: this needs more context in some situations e.g. where the sign was placed

	/** Logging filter action - log the action the user took, but don't prevent them from doing it. **/
	object LogOnlyAction : FilterAction() {
		override val severity: Int = -1

		override fun invoke(event: Event): Boolean {
			return true
		}
	}
}
