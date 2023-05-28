/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.io.IOException
import java.lang.IllegalStateException
import java.nio.file.Files

object Translations {
	private lateinit var fallbacks : MutableMap<String, String>

	fun get(translationKey: String, vararg params: Any): MutableComponent {
		return Component.translatableWithFallback(translationKey, getFallback(translationKey), *params)
	}

	private fun getFallback(translationKey: String) : String {
		if (!::fallbacks.isInitialized) {
			val mod = FabricLoader.getInstance().getModContainer("get-rekt").orElseThrow()
			val translationsPath = mod.findPath("assets/get-rekt/lang/en_us.json").orElseThrow()

			try {
				val reader = Files.newBufferedReader(translationsPath)
				val gson = GsonBuilder().create()
				val obj = gson.fromJson(reader, JsonObject::class.java)
				fallbacks = mutableMapOf()
				obj.entrySet().forEach {
					fallbacks[it.key] = it.value.asString
				}
			} catch (exception : IOException) {
				throw IllegalStateException("Could not read translation file.")
			}
		}

		if (translationKey in fallbacks) {
			return fallbacks[translationKey]!!
		}

		throw IllegalArgumentException("Unknown translations key.")
	}
}
