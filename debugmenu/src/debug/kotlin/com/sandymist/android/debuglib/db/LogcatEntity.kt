package com.sandymist.android.debuglib.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sandymist.android.debuglib.model.Logcat

const val LOGCAT_TABLE_NAME = "logcat"
const val LOGCAT_MAX_ENTRIES = 1000 // TODO: make configurable

val LOGCAT_LIMIT_ROWS = getRowLimitTrigger(
    LOGCAT_TABLE_NAME,
    "id",
    "createdAt",
    LOGCAT_MAX_ENTRIES,
)

@Entity(tableName = LOGCAT_TABLE_NAME)
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
