package com.sandymist.android.debuglib.db

import androidx.room.Entity
import com.sandymist.android.debuglib.model.PrefData

@Entity(
    tableName = "preferences",
    primaryKeys = ["type", "name"]
)
data class PreferencesEntity(
    val name: String,
    val value: String,
    val type: String, // can be "header" or null/""
    val createdAt: Long,
) {
    fun toPrefData(): PrefData {
        return PrefData(name, value, type, createdAt)
    }

    companion object {
        fun fromPrefData(prefData: PrefData): PreferencesEntity {
            return PreferencesEntity(
                name = prefData.name,
                value = prefData.value,
                type = prefData.type,
                createdAt = prefData.createdAt
            )
        }
    }
}
