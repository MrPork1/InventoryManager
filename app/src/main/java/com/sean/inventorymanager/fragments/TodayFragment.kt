package com.sean.inventorymanager.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sean.inventorymanager.R
import com.sean.inventorymanager.adapter.CardAdapter
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.data.ItemViewModel
import com.sean.inventorymanager.databinding.FragmentTodayBinding
import com.sean.inventorymanager.databinding.FragmentUpdateBinding

class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    private lateinit var mItemViewModel: ItemViewModel

    private val adapter = CardAdapter()
    private var itemList = emptyList<Item>()
    private var secondList: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view =  binding.root


        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.visibility = View.GONE

        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        mItemViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            secondList.clear()
            itemList = it
            for (x in itemList) {
                if (x.frequency.frequencyAmount > 0F) {
                    secondList.add(x)
                }
            }

            secondList.sortByDescending { it.lastTook?.lastTookDateTime  }
            adapter.setData(secondList)
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}