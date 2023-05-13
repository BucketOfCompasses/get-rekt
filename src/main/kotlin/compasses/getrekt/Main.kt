/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Main : ModInitializer {
	val LOGGER : Logger = LoggerFactory.getLogger("Get Rekt")

	override fun onInitialize() {
		LOGGER.info("Hello modded world!")
	}
}
