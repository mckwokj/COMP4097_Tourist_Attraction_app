package edu.hkbu.comp.comp4097.comp4097project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(PlaceInfo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao

    companion object {
        private var instance: AppDatabase? = null

        suspend fun getInstance(context: Context): AppDatabase {
            if (instance != null) {
                return instance!!
            }
            instance = Room.databaseBuilder(context, AppDatabase::class.java, "place").build()
            initDB()

            return instance!!
        }

        suspend fun initDB() {
//                instance?.clearAllTables() //add this line when you are still debugging
//                SampleData.EVENT.forEach { instance?.eventDao()?.insert(it) }
        }
    }
}