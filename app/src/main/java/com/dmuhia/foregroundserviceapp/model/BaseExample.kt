package com.dmuhia.foregroundserviceapp.model

import kotlin.time.measureTimedValue

fun main(){
    val (value, time) = measureTimedValue {
        predictFloodingInNairobi(0.35,7000.0)

    }
    println("Time taken:: ${time} for $value")
}

private fun predictFloodingInNairobi(amountOfRain: Double, areaInSquareMeters: Double): Boolean {
    val rainfallThresholdInMillimeters = 50.0
    val areaThreshold =1000.0

    val rainfallInMillimeters = amountOfRain * 1000.0

    return rainfallInMillimeters > rainfallThresholdInMillimeters && areaInSquareMeters > areaThreshold
}