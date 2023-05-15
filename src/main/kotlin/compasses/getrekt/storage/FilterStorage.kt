/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.storage

import compasses.getrekt.filters.Filter
import compasses.getrekt.filters.FilterAction
import compasses.getrekt.filters.FilterType
import compasses.getrekt.filters.MatchType
import net.codebox.homoglyph.HomoglyphBuilder

object FilterStorage {
	private val filters: List<Filter> = listOf(
			Filter("beans", MatchType.CONTAINS, FilterAction.MuteAction, FilterType.TEXT),
			Filter("ketchup", MatchType.CONTAINS, FilterAction.KickAction, FilterType.TEXT)
	)
	private val homoglyphs = HomoglyphBuilder.build()

	fun firstOrNull(raw: String, filterType: FilterType): Filter? {
		return filters.sortedByDescending { it.filterAction.severity }
				.filter { it.filterType == filterType }
				.firstOrNull {
					when (it.matchType) {
						MatchType.CONTAINS -> homoglyphs.search(raw, it.match).size > 0
						MatchType.EXACT -> raw.equals(it.match, ignoreCase = true)
						MatchType.REGEX_CONTAINS -> raw.contains(it.match.toRegex(RegexOption.IGNORE_CASE))
						MatchType.REGEX -> it.match.toRegex(RegexOption.IGNORE_CASE).matches(raw)
					}
				}
	}
}
