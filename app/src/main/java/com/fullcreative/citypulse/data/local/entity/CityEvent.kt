
package com.fullcreative.citypulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import com.fullcreative.citypulse.domain.model.CityEvent as DomainCityEvent

@Entity(tableName = "city_events")
data class CityEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String,
    val timestamp: Long
) {
    fun toDomain(): DomainCityEvent = DomainCityEvent(
        id = id,
        name = name,
        color = color,
        timestamp = timestamp

    )
}