package com.sean.inventorymanager.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sean.inventorymanager.R
import com.sean.inventorymanager.data.ItemViewModel
import com.sean.inventorymanager.databinding.FragmentDetailsBinding
import com.sean.inventorymanager.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mItemViewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view =  binding.root

        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        binding.settingsDeleteData.setOnClickListener {
            deleteDataFromDatabase()
        }

        return view
    }

    private fun deleteDataFromDatabase() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_,_ ->
            mItemViewModel.deleteAllItems()
            Toast.makeText(requireContext(), "Removed all data", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle("Delete all data?")
        builder.setMessage("Are you sure you want to delete all of the data?")
        //builder.setSingleChoiceItems(R.array.measurements_array, 0, null)
        builder.create().show()
    }
}