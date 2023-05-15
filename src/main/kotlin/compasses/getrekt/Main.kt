/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt

import compasses.getrekt.filters.Filter
import compasses.getrekt.filters.FilterAction
import compasses.getrekt.filters.FilterType
import compasses.getrekt.filters.MatchType
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

object Main : ModInitializer {
	val LOGGER: Logger = LoggerFactory.getLogger("Get Rekt")

	override fun onInitialize() {
		LOGGER.info("Hello modded world!")
	}

	// todo: find fixed places for these & actually implement.

	fun mutePlayer(player: UUID) {
		//TODO("Not yet implemented")
	}

	fun isMuted(player: UUID) : Boolean {
		TODO("Not yet implemented")
	}

	@JvmStatic
	fun firstFilterOrNull(raw: String, filterType: FilterType): Filter? {
		// NOTE: Sort by severity, use the most severe first

		if (raw.contains("beans") && filterType == FilterType.TEXT) {
			return Filter("beans", MatchType.CONTAINS, FilterAction.CancelAction, FilterType.TEXT)
		}

		return null
	}
}
