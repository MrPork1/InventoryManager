package com.sean.inventorymanager.adapter

import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.sean.inventorymanager.R
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.fragments.TodayFragmentDirections
import com.sean.inventorymanager.utils.Constants
import java.security.AccessController.getContext
import java.time.*

class CardAdapter: RecyclerView.Adapter<CardAdapter.MyViewHolder>() {

    private var itemList = emptyList<Item>()
    private var tag = "CardAdapter"
    private var calculatedString: String = ""

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_row2, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]

        val here = holder.itemView.context.resources.getStringArray(R.array.category_array)
        val imageInt = showCategoryImage(currentItem.itemCategory, *here)
        if (imageInt != -1) {
            holder.itemView.findViewById<ImageView>(R.id.cardView_itemImage).setImageResource(imageInt)
        }

        holder.itemView.findViewById<TextView>(R.id.cardView_itemName).text = currentItem.itemName

        if (currentItem.itemAmount <= 0) {
            holder.itemView.findViewById<TextView>(R.id.cardView_stockText).setTextColor(holder.itemView.context.getColor(
                com.google.android.material.R.color.design_default_color_error))
        }

        holder.itemView.findViewById<TextView>(R.id.cardView_stockText).text =
            holder.itemView.context.getString(R.string.card_adapter_stock_format, currentItem.itemAmount)

        val thing = calculationsHere(currentItem)
        if (thing > 0F) {
            //val cardView = holder.itemView.findViewById<TextView>(R.id.cardView_TakeText)
            val cardView = holder.itemView.findViewById<TextView>(R.id.cardView_TakeText2)
            //cardView.visibility = View.VISIBLE
            cardView.text = holder.itemView.context.getString(R.string.card_adapter_take_format, thing)

            holder.itemView.findViewById<CardView>(R.id.cardView).setOnClickListener {
                val action = TodayFragmentDirections.actionTodayFragmentToTakeFragment(currentItem, thing)
                holder.itemView.findNavController().navigate(action)
            }
        } else {
//            val buttons = holder.itemView.findViewById<LinearLayout>(R.id.cardView_TakeText)
//            buttons.visibility = View.GONE
            val cardView = holder.itemView.findViewById<TextView>(R.id.cardView_TakeText2)
            cardView.visibility = View.VISIBLE
            cardView.text = calculatedString
        }
    }

    private fun calculationsHere(x: Item): Float {
        //If last took is null, it means user can take full amount
        if (x.lastTook == null) {
            return x.frequency.frequencyAmount
        }

//        if (x.lastTook?.lastTookAmount!! >= x.frequency.frequencyAmount) {
//            return 0F
//        }
        Log.d("This", x.lastTook?.lastTookAmount.toString())

        if (x.lastTook?.lastTookAmount!! < x.frequency.frequencyAmount) {
            return x.frequency.frequencyAmount - x.lastTook!!.lastTookAmount
        }

        val currentTime = LocalDateTime.now()
        var dateNextTake: LocalDate? = null
        var timeNextTake: LocalTime? = null

        when (x.frequency.durationLength) {
            "hour" -> timeNextTake = LocalTime.from(x.lastTook?.lastTookDateTime).plusHours(x.frequency.duration.toLong())
            "day" -> dateNextTake = LocalDate.from(x.lastTook?.lastTookDateTime).plusDays(x.frequency.duration.toLong())
            "week" -> dateNextTake = LocalDate.from(x.lastTook?.lastTookDateTime).plusWeeks(x.frequency.duration.toLong())
            "month" -> dateNextTake = LocalDate.from(x.lastTook?.lastTookDateTime).plusMonths(x.frequency.duration.toLong())
            "year" -> dateNextTake = LocalDate.from(x.lastTook?.lastTookDateTime).plusYears(x.frequency.duration.toLong())
        }

        if (x.frequency.durationLength == "hour") {
            return if (currentTime.toLocalTime() >= timeNextTake) {
                //Can take.
                x.frequency.frequencyAmount
            } else {
                if (x.lastTook!!.lastTookAmount >= x.frequency.frequencyAmount) {
                    //Can take at the next time taken.
                        val newValue = Constants.timeFormatter.format(timeNextTake)
                    calculatedString = "Next: $newValue"
                    0F
                } else {
                    x.frequency.frequencyAmount - x.lastTook!!.lastTookAmount
                }
            }
        }

        if (x.frequency.durationLength != "hour") {
            if (currentTime.toLocalDate() > dateNextTake) {
                return x.frequency.frequencyAmount
            } else if (currentTime.toLocalDate() == dateNextTake) {
                //Today is the day! Now check hours.
                val newTimeLeft = x.lastTook?.lastTookDateTime?.toLocalTime()
                val newTimeLeftFormatted = Constants.timeFormatter.format(newTimeLeft)
                calculatedString = "Next: Today at $newTimeLeftFormatted"
                if (currentTime.toLocalTime() >= newTimeLeft) {
                    return x.frequency.frequencyAmount
                }
            } else {
                return if (x.lastTook!!.lastTookAmount >= x.frequency.frequencyAmount) {
                    //Can take at the next taken date and time.
                        val newDateNextTake = Constants.dateFormatter.format(dateNextTake)
                    val newTimeNextTake = Constants.timeFormatter.format(x.lastTook?.lastTookDateTime?.toLocalTime())
                    calculatedString = "Next: $newDateNextTake at $newTimeNextTake"
                    0F
                } else {
                    x.frequency.frequencyAmount - x.lastTook!!.lastTookAmount
                }
            }
        }
        return 0f //Can't take any. Return 0
    }

    private fun showCategoryImage(categoryString: String, vararg strings: String): Int {
        if (categoryString.isNotEmpty())
        {
            when (categoryString) {
                strings[0] -> return R.drawable.ic_baseline_medication
                strings[1] -> return R.drawable.ic_baseline_fastfood
                strings[2] -> return R.drawable.ic_baseline_cleaning_services
                strings[3] -> return R.drawable.ic_baseline_pets
            }
        }
        return -1
    }

    fun setData(item: List<Item>) {
        this.itemList = item
        notifyDataSetChanged()
    }
}