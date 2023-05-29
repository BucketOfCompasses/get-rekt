/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.commands

import com.ibm.icu.text.MeasureFormat
import com.ibm.icu.util.Measure
import com.ibm.icu.util.MeasureUnit
import com.mojang.authlib.GameProfile
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.toJavaInstant
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val DURATION_REGEX = "([1-9][0-9]*) (years|months|weeks|days|hours|minutes|year|month|week|day|hour|minute)"

class CommandActor(private val targets: Collection<GameProfile>, reasonDurationPair: String) {
	private val durationFormat = MeasureFormat.getInstance(Locale.ENGLISH, MeasureFormat.FormatWidth.WIDE)

	private val startInstant = Clock.System.now()

	private val duration : DateTimePeriod

	val startDate : Date

	val endDate : Date

	val reason : String

	init {
		val matches = DURATION_REGEX.toRegex().findAll(reasonDurationPair)
		var foundDuration : MatchResult? = null

		for (match in matches.toList().reversed()) {
			if (match.range.first == 0 || match.range.last == reasonDurationPair.length - 1) {
				foundDuration = match
				break
			}
		}

		if (foundDuration == null) {
			throw IllegalArgumentException("Invalid input for reasonDurationPair")
		}

		val durationLength = foundDuration.groupValues[1].toInt()

		duration = when(foundDuration.groupValues.last().lowercase()) {
			"minute", "minutes" -> DateTimePeriod(minutes = durationLength)
			"hour", "hours" -> DateTimePeriod(hours = durationLength)
			"day", "days" -> DateTimePeriod(days = durationLength)
			"week", "weeks" -> DateTimePeriod(days = durationLength * 7)
			"month", "months" -> DateTimePeriod(months = durationLength)
			"year", "years" -> DateTimePeriod(years = durationLength)
			else -> throw IllegalStateException("Unreachable code.")
		}

		reason = reasonDurationPair.removeRange(foundDuration.range).trim()

		startDate = Date.from(startInstant.toJavaInstant())
		endDate = Date.from((startInstant + duration.seconds.toDuration(DurationUnit.SECONDS)).toJavaInstant())
	}

	fun act(action: (GameProfile) -> Boolean) : Int {
		var result = 0
		for (target in targets) {
			if (action.invoke(target)) {
				result++
			}
		}
		return result
	}

	fun durationString(): String {
		val measures = mutableListOf<Measure>()
		if (duration.years != 0) measures.add(Measure(duration.years, MeasureUnit.YEAR))
		if (duration.months != 0) measures.add(Measure(duration.months, MeasureUnit.MONTH))
		val weeks = Math.floorDiv(duration.days, 7)
		val days = duration.days - (7 * weeks)
		if (weeks != 0) measures.add(Measure(weeks, MeasureUnit.WEEK))
		if (days != 0) measures.add(Measure(days, MeasureUnit.DAY))
		if (duration.hours != 0) measures.add(Measure(duration.hours, MeasureUnit.HOUR))
		if (duration.minutes != 0) measures.add(Measure(duration.minutes, MeasureUnit.MINUTE))

		return durationFormat.formatMeasures(*measures.toTypedArray())
	}
}
