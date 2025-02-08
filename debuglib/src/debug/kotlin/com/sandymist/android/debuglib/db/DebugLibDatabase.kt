package com.sandymist.android.debuglib.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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

        private val callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(LOGCAT_LIMIT_ROWS)
            }
        }

        fun getDatabase(context: Context): DebugLibDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    DebugLibDatabase::class.java,
                    DB_NAME,
                )

                builder
                    .addCallback(callback)
                    .fallbackToDestructiveMigration() // TODO: make configurable
                    .build()
                val instance = builder.build()
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
