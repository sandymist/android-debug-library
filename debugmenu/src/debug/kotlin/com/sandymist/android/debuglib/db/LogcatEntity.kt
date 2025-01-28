package com.sandymist.android.debuglib.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sandymist.android.debuglib.model.Logcat

@Entity(tableName = "logcat")
data class LogcatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val message: String,
    val createdAt: Long,
) {
    fun toLogcat(): Logcat {
        return Logcat(message, createdAt)
    }

    companion object {
        fun fromLogcat(logcat: Logcat): LogcatEntity {
            return LogcatEntity(message = logcat.message, createdAt = logcat.createdAt)
        }
    }
}
