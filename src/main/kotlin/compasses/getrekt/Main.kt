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
