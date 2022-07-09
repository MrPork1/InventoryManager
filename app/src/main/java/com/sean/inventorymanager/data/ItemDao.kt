package com.sean.inventorymanager.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDate

@Dao
interface ItemDao {

    @Insert
    suspend fun addItem(item: Item)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addLastTook(lastTook: LastTook)

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("DELETE FROM item_table")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM item_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Item>>

    @Query("SELECT * FROM item_table WHERE itemName LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<Item>>

    @Query("SELECT * FROM item_table WHERE itemExpired = :queryDate")
    fun searchDatabaseByExpiredDate(queryDate: LocalDate): LiveData<List<Item>>

//    @Transaction
//    @Query("SELECT * FROM item_table ORDER BY id ASC")
//    fun searchByItemLastTookNotNull(): LiveData<List<ItemWithLastTook>>

    @Query("SELECT * FROM item_table ORDER BY itemFavorite ASC")
    fun orderDatabaseByFavorite(): LiveData<List<Item>>
}