package com.huyhieu.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.huyhieu.data.local.room.dao.SearchHistoryDao
import com.huyhieu.data.local.room.entity.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun getSearchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var instance: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppRoomDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = AppRoomDatabase::class.java,
                name = "PHOTO_SEARCH_DB",
            )
                //.fallbackToDestructiveMigration(false)//Sẽ xóa dữ liệu cũ mỗi khi đổi Version
                .build()
        }
    }
}