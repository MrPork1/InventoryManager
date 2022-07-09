package com.sean.inventorymanager.fragments

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sean.inventorymanager.R
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.data.ItemViewModel
import com.sean.inventorymanager.databinding.FragmentDetailsBinding
import com.sean.inventorymanager.utils.Constants
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailsFragmentArgs>()

    private lateinit var mItemViewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view =  binding.root

        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        //val dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")

        binding.detailsItemName.text = args.currentItem.itemName

        if (args.currentItem.itemAmount <= 0) {
            binding.detailsItemAmount.setTextColor(requireContext().getColor(com.google.android.material.R.color.design_default_color_error))
        }
        binding.detailsItemAmount.text = args.currentItem.itemAmount.toString()
        binding.detailsItemCategoryActual.text = args.currentItem.itemCategory
        binding.detailsItemLocationActual.text = args.currentItem.itemLocation
        binding.detailsItemLocationMeasurementActual.text = args.currentItem.itemMeasurement
        binding.detailsItemCreatedDateActual.text = Constants.dateFormatter.format(args.currentItem.itemCreated)
        binding.detailsItemExpiredDateActual.text = Constants.dateFormatter.format(args.currentItem.itemExpired)

        if (args.currentItem.lastTook != null) {
            binding.detailsItemLastTookTimeActual.text =
                Constants.timeFormatter.format(args.currentItem.lastTook?.lastTookDateTime?.toLocalTime())
            binding.detailsItemLastTookDateActual.text =
                Constants.dateFormatter.format(args.currentItem.lastTook?.lastTookDateTime?.toLocalDate()).toString()
        }

        if (args.currentItem.frequency.frequencyAmount > 0F) {
            binding.detailsDetailsFrequencyAmountActual.text = args.currentItem.frequency.frequencyAmount.toString()
            val totalDuration = args.currentItem.frequency.duration.toString() + " " +
                    args.currentItem.frequency.durationLength + "s"
            binding.detailsDetailsFrequencyDurationActual.text = totalDuration
        }


        if (args.currentItem.itemFavorite) {
            binding.detailsItemFavorite.visibility = View.VISIBLE
        }

        showCategoryImage()

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_edit) {
            val action = DetailsFragmentDirections.actionDetailsFragmentToUpdateFragment(args.currentItem)
            findNavController().navigate(action)
        } else if (i == R.id.menu_transfer) {
            openTransferDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showCategoryImage() {
        val categoryString = args.currentItem.itemCategory
        if (categoryString.isNotEmpty())
        {
            val array = resources.getStringArray(R.array.category_array)
            binding.detailsItemCategoryImage.visibility = View.VISIBLE
            when (categoryString) {
                array[0] -> binding.detailsItemCategoryImage.setImageResource(R.drawable.ic_baseline_medication)
                array[1] -> binding.detailsItemCategoryImage.setImageResource(R.drawable.ic_baseline_fastfood)
                array[2] -> binding.detailsItemCategoryImage.setImageResource(R.drawable.ic_baseline_cleaning_services)
                array[3] -> binding.detailsItemCategoryImage.setImageResource(R.drawable.ic_baseline_pets)
                else -> {
                    binding.detailsItemCategoryImage.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun openTransferDialog() {
        val newDialog = TransferDialogFragment()
        newDialog.show(childFragmentManager, "hello there")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class TransferDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val mItemViewModel: ItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_transfer, null)

            val adapter = ArrayAdapter<Item>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
            val spinner = view.findViewById<Spinner>(R.id.transfer_spinnerLocation)
            mItemViewModel.readAllData.observe(this, Observer {
                    item ->
                item?.forEach {
                    adapter.add(it)
                }
            })
            spinner.adapter = adapter

            builder.setView(view)
                .setPositiveButton("Save") {_,_ ->}
                .setNegativeButton("Cancel") {_,_ ->}
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null.")
    }
}