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

object FilterStorage {
	fun firstOrNull(raw: String, filterType: FilterType): Filter? {
		// NOTE: Sort by severity, use the most severe first

		if (raw.contains("beans") && filterType == FilterType.TEXT) {
			return Filter("beans", MatchType.CONTAINS, FilterAction.MuteAction, FilterType.TEXT)
		}

		return null
	}
}
