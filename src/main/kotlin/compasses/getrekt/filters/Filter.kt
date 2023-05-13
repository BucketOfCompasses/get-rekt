/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.filters

import net.minecraft.server.level.ServerPlayer

data class Filter(
		val match: String,
		val matchType: MatchType,
		val action: FilterAction,
		val filterType: FilterType
) {
	fun action(player: ServerPlayer, oldText: String?, newText: String) {
		action.action(player, oldText, newText)
	}
}
