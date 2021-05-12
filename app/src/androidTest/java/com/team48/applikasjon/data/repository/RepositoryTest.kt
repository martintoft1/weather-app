package com.team48.applikasjon.data.repository

import android.content.Context
import androidx.constraintlayout.utils.widget.MockView
import androidx.core.text.isDigitsOnly
import androidx.test.core.app.ApplicationProvider
import com.team48.applikasjon.data.api.ApiServiceImpl
import com.team48.applikasjon.data.models.VectorDataset
import com.team48.applikasjon.ui.main.MainActivity
import junit.framework.TestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass

class RepositoryTest : TestCase() {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var repository: Repository
    private val WEATHER_TYPES = 3


    private var apiServiceImpl: ApiServiceImpl = ApiServiceImpl()
    private lateinit var weatherList: List<VectorDataset>
    private lateinit var dataset: VectorDataset

    init {
        setup()
    }

    @BeforeClass
    fun setup() {

        repository = Repository(context)


        // Venter på at datasettet skal laste inn fra met
        while (repository.getWeather().size < WEATHER_TYPES) {
            runBlocking {
                delay(200)
            }
        }
        weatherList = apiServiceImpl.getWeather()

        // Velger tilfeldig dataset for testing
        val randomIndex = (0 until WEATHER_TYPES).random()
        dataset = weatherList[randomIndex]

    }

    fun testGetWeather() {

        assertEquals("Should contain 3 weathertypes", WEATHER_TYPES, weatherList.size)
        assertEquals("Wrong filetype", "pbf", dataset.imageType.toString())
        assertNotNull(dataset)

        val datasetId = dataset.url?.substringAfterLast("/")
        if (datasetId != null) {
            assertTrue("ID at end of url should only contain digits", datasetId.isDigitsOnly())
        }
    }

    fun testAddLocation() {
        fail("Test not yet implemented")
    }

    fun testDeleteLocation() {
        fail("Test not yet implemented")
    }

    fun testGetAllLocations() {
        fail("Test not yet implemented")
    }

    fun testGetCount() {
        fail("Test not yet implemented")
    }
}