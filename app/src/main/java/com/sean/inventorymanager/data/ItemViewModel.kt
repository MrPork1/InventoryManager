package com.sean.inventorymanager.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.time.LocalDate

class ItemViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Item>>
    private val repository: ItemRepository

    init {
        val itemDao = ItemDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(itemDao)
        readAllData = repository.readAllData
    }

    fun addItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addItem(item)
        }
    }

//    fun addLastTook(lastTook: LastTook) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.addLastTook(lastTook)
//        }
//    }

    fun updateItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }

    fun deleteAllItems() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllItems()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Item>> {
        return repository.searchDatabase(searchQuery)
    }

    fun searchDatabaseByExpiredDate(expiredQuery: LocalDate): LiveData<List<Item>> {
        return repository.searchDateByExpiredDate(expiredQuery)
    }

//    fun searchByItemLastTookNotNull(): LiveData<List<ItemWithLastTook>> {
//        return repository.returnLastTookBy()
//    }

    fun orderDatabaseByFavorite(): LiveData<List<Item>> {
        return repository.orderDatabaseByFavorite()
    }
}