package compasses.getrekt

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

object TranslationFallbacks {
	fun withFallback(translationKey: String, vararg params: Any): MutableComponent {
		return Component.translatableWithFallback(translationKey, getFallback(translationKey), *params)
	}

	// todo: load these from the en_us.json or use one of kotlin's fancy thing for code gen
	private fun getFallback(translationKey: String) : String {
		return when(translationKey) {
			"get-rekt.action.muted" -> "You have been muted."
			"get-rekt.action.unmuted" -> "You have been unmuted."
			"get-rekt.command.muted" -> "%s has been muted."
			"get-rekt.command.unknown_player" -> "Command failed, unknown player: %s."
			"get-rekt.command.already_muted" -> "%s is already muted."
			"get-rekt.command.not_muteable" -> "%s is not able to be muted."
			else -> throw IllegalArgumentException("Unknown translation key.")
		}
	}
}
