package com.sean.inventorymanager.data

import androidx.lifecycle.LiveData
import java.time.LocalDate

class ItemRepository(private val itemDao: ItemDao) {

    val readAllData: LiveData<List<Item>> = itemDao.readAllData()

    suspend fun addItem(item: Item) {
        itemDao.addItem(item)
    }

//    suspend fun addLastTook(lastTook: LastTook) {
//        itemDao.addLastTook(lastTook)
//    }

    suspend fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDao.deleteItem(item)
    }

    suspend fun deleteAllItems() {
        itemDao.deleteAllItems()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Item>> {
        return itemDao.searchDatabase(searchQuery)
    }

    fun searchDateByExpiredDate(expiredQuery: LocalDate): LiveData<List<Item>> {
        return itemDao.searchDatabaseByExpiredDate(expiredQuery)
    }

//    fun searchByItemLastTookNotNull(): LiveData<List<Item>> {
//        return itemDao.searchByItemLastTookNotNull()
//    }

//    fun returnLastTookBy(): LiveData<List<ItemWithLastTook>> {
//        return itemDao.searchByItemLastTookNotNull()
//    }

    fun orderDatabaseByFavorite(): LiveData<List<Item>> {
        return itemDao.orderDatabaseByFavorite()
    }
}