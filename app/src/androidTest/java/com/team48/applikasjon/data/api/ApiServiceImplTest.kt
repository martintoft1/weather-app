package com.team48.applikasjon.data.api

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.team48.applikasjon.data.models.VectorDataset
import junit.framework.TestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class ApiServiceImplTest : TestCase() {

    private var apiServiceImpl: ApiServiceImpl = ApiServiceImpl()
    private lateinit var weatherList: List<VectorDataset>
    private lateinit var dataset: VectorDataset
    private val WEATHER_TYPES = 3

    init {
        setup()
    }

    @BeforeClass
    fun setup() {
        // Venter på at datasettet skal laste inn fra met
        while (apiServiceImpl.getWeather().size < WEATHER_TYPES) {
            runBlocking {
                delay(200)
            }
        }
        weatherList = apiServiceImpl.getWeather()

        // Velger tilfeldig dataset for testing
        val randomIndex = (0 until WEATHER_TYPES).random()
        dataset = weatherList[randomIndex]
    }

    @Test
    fun testGetWeather() {

        assertEquals("Should contain 3 weathertypes", WEATHER_TYPES, weatherList.size)
        assertEquals("Wrong filetype", "pbf", dataset.imageType.toString())
        assertNotNull(dataset)

        val datasetId = dataset.url?.substringAfterLast("/")
        if (datasetId != null) {
            assertTrue("ID at end of url should only contain digits", datasetId.isDigitsOnly())
        }
    }
}