package com.sean.inventorymanager.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.sean.inventorymanager.R
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.fragments.CalenderFragmentDirections
import com.sean.inventorymanager.fragments.listFragmentDirections
import java.time.LocalDateTime

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var itemList = emptyList<Item>()
    private var fragmentId: Int = 0

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.itemView.findViewById<TextView>(R.id.textview_itemname).text = currentItem.itemName

        if (currentItem.itemAmount <= 0) {
            holder.itemView.findViewById<TextView>(R.id.textview_itemAmount).setTextColor(holder.itemView.context.getColor(
                com.google.android.material.R.color.design_default_color_error))
        }

        var text = ""
        val currentDate = LocalDateTime.now()
        if (currentDate.toLocalDate() > currentItem.itemExpired) {
            text = holder.itemView.context.getString(R.string.list_adapter_format, currentItem.itemLocation, currentItem.itemCategory, currentItem.itemMeasurement)
            text += " " + holder.itemView.context.getString(R.string.list_adapter_format_expired, "Expired")
        } else {
            text = holder.itemView.context.getString(R.string.list_adapter_format, currentItem.itemLocation, currentItem.itemCategory, currentItem.itemMeasurement)
        }

        holder.itemView.findViewById<TextView>(R.id.textview_itemAmount).text = currentItem.itemAmount.toString()
        holder.itemView.findViewById<TextView>(R.id.textview_details).text = text

        if (currentItem.itemFavorite) {
            holder.itemView.findViewById<ImageView>(R.id.textview_itemFavorite).visibility = View.VISIBLE
        }

        holder.itemView.findViewById<ImageButton>(R.id.button_details).setOnClickListener {
            if (fragmentId == 0)
            {
                val action = listFragmentDirections.actionListFragmentToDetailsFragment(currentItem)
                holder.itemView.findNavController().navigate(action)
            } else if (fragmentId == 1) {
                val action = CalenderFragmentDirections.actionCalenderFragmentToDetailsFragment(currentItem)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    fun setData(item: List<Item>) {
        this.itemList = item
        notifyDataSetChanged()
    }

    fun setId(x: Int) {
        fragmentId = x
    }
}