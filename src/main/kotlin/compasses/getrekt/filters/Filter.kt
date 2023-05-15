/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.filters

import compasses.getrekt.Event
import kotlinx.serialization.Serializable

@Serializable
data class Filter(
	val match: String,
	val matchType: MatchType,
	val filterAction: FilterAction,
	val filterType: FilterType
) {
	fun action(event: Event): Boolean =
		filterAction(event)
}
