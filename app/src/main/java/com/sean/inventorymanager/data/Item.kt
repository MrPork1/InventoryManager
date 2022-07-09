package com.sean.inventorymanager.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "item_table")
data class Item (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val itemName: String,
    val itemAmount: Float,
    val itemMeasurement: String,
    val itemCategory: String,
    val itemLocation: String,
    val itemCreated: LocalDate,
    val itemExpired: LocalDate,
    val itemNotes: String,
    val itemFavorite: Boolean,
    @Embedded
    var lastTook: LastTook?,
    @Embedded
    val frequency: Frequency
): Parcelable {
    override fun toString(): String {
        return "$itemName | $itemAmount"
    }
}

@Parcelize
data class Frequency(
    val frequencyAmount: Float,
    val duration: Float,
    val durationLength: String,
): Parcelable

@Parcelize
data class LastTook(
    val lastTookAmount: Float,
    val lastTookDateTime: LocalDateTime
): Parcelable

//@Parcelize
//@Entity(tableName = "lastTook_table")
//data class LastTook(
//    @PrimaryKey(autoGenerate = true)
//    val lastTookId: Int,
//    val itemId: Int,
//    val lastTookAmount: Double,
//    val lastTookDateTime: LocalDateTime
//): Parcelable
//
//data class ItemWithLastTook(
//    @Embedded
//    val item: Item,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "itemId"
//    )
//    val lastTook: LastTook?
//)

