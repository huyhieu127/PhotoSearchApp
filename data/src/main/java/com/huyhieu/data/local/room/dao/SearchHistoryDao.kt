package com.huyhieu.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huyhieu.data.local.room.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistoryEntity: SearchHistoryEntity): Long

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM search_history WHERE  LOWER(keyword) LIKE LOWER('%' || :keyword || '%') ORDER BY timestamp DESC LIMIT :take")
    suspend fun searchHistoryByQuery(keyword: String, take: Int): List<SearchHistoryEntity>

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :take")
    suspend fun getHistories(take: Int): List<SearchHistoryEntity>

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :take")
    fun getListHistories(take: Int): Flow<List<SearchHistoryEntity>>

}