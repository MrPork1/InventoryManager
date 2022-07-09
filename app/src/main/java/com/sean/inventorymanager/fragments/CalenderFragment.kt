package com.sean.inventorymanager.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sean.inventorymanager.adapter.ListAdapter
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.data.ItemViewModel
import com.sean.inventorymanager.databinding.FragmentCalenderBinding
import java.time.LocalDate
import java.util.*

class CalenderFragment : Fragment() {
    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!


    private lateinit var mItemViewModel: ItemViewModel
    private val adapter = ListAdapter()

    val calender = Calendar.getInstance()

    private var itemList = emptyList<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        val view =  binding.root

        val recyclerView = binding.calenderViewRecyclerview
        adapter.setId(1)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        val newDate = LocalDate.of(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
        searchDatabase(newDate)

        binding.calenderView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            searchDatabase(LocalDate.of(year, month+1, dayOfMonth))
        }

        return view
    }

    private fun searchDatabase(query: LocalDate) {
        val searchQuery: LocalDate = query
        Log.i("date", searchQuery.toString())

        mItemViewModel.searchDatabaseByExpiredDate(searchQuery).observe(viewLifecycleOwner) { list ->
            list.let {
                adapter.setData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}