package com.fullcreative.citypulse.domain.usecases

import com.fullcreative.citypulse.domain.usecase.GenerateCityEventUseCase
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class GenerateCityEventUseCaseTest {

    private lateinit var useCase: GenerateCityEventUseCase

    @Before
    fun setup() {
        useCase = GenerateCityEventUseCase()
    }

    @Test
    fun `generated event should have valid city name`() {
        val validCities = listOf("New York", "Los Angeles", "Scranton", 
                               "Philadelphia", "Nashville", "Saint Louis", "Miami")
        
        val event = useCase()
        
        assertTrue(validCities.contains(event.name))
    }

    @Test
    fun `generated event should have valid color`() {
        val validColors = listOf("Yellow", "White", "Green", 
                               "Blue", "Red", "Black")
        
        val event = useCase()
        
        assertTrue(validColors.contains(event.color))
    }

    @Test
    fun `timestamp should be recent`() {
        val before = System.currentTimeMillis()
        val event = useCase()
        val after = System.currentTimeMillis()
        
        assertTrue(event.timestamp in before..after)
    }
}