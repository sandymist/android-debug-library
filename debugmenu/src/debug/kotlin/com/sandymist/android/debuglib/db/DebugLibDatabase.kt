package com.sandymist.android.debuglib.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.FileNotFoundException

private const val DB_VERSION = 2
private const val DB_NAME = "debug_lib_database"

@Database(
    entities = [
        LogcatEntity::class,
        PreferencesEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = true,
)
abstract class DebugLibDatabase : RoomDatabase() {
    abstract fun logcatDao(): LogcatDao
    abstract fun preferencesDao(): PreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: DebugLibDatabase? = null

        fun getDatabase(context: Context): DebugLibDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DebugLibDatabase::class.java,
                    DB_NAME,
                )
                    .fallbackToDestructiveMigration() // TODO: remove before launch
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabaseSize(context: Context): Long {
            val dbFile = context.getDatabasePath(DB_NAME)

            if (dbFile.exists()) {
                return dbFile.length()
            } else {
                throw FileNotFoundException("Database file not found: ${dbFile.absolutePath}")
            }
        }
    }
}
