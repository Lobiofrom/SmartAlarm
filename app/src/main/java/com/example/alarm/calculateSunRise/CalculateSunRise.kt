package com.example.alarm.calculateSunRise

import org.shredzone.commons.suncalc.SunTimes

class CalculateSunRise(
    latitude: Double,
    longitude: Double
) {
    val times: SunTimes = SunTimes.compute()
        .at(latitude, longitude)
        .execute()
}