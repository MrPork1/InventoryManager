package com.sean.inventorymanager.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sean.inventorymanager.R
import com.sean.inventorymanager.data.Frequency
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.data.ItemViewModel
import com.sean.inventorymanager.data.LastTook
import com.sean.inventorymanager.databinding.FragmentUpdateBinding
import com.sean.inventorymanager.utils.Constants
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.max
import kotlin.math.min

class UpdateFragment : Fragment(), DatePickerDialog.OnDateSetListener, View.OnClickListener{
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mItemViewModel: ItemViewModel

    var favorite: Boolean = false
    private lateinit var updateItemExpiredDate: LocalDate
    private lateinit var updateItemLastTookDate: LocalDate
    private lateinit var updateItemLastTookTime: LocalTime

    var buttonId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view =  binding.root
        // Inflate the layout for this fragment
        populateSpinners()

        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        binding.updateItemName.setText(args.currentItem.itemName)
        binding.updateItemAmount.setText(args.currentItem.itemAmount.toString())
        binding.updateCreatedDate.text = "Created ${Constants.dateFormatter.format(args.currentItem.itemCreated)}"
        binding.updateItemNotes.setText(args.currentItem.itemNotes)
        binding.updateExpiredDateTextView.text = args.currentItem.itemExpired.toString()

        binding.buttonUpdateUpdateItem.setOnClickListener {
            updateItemToDatabase()
        }

        binding.buttonUpdateExpiredDateDialog.setOnClickListener(this)

        updateItemExpiredDate = args.currentItem.itemExpired


        if (args.currentItem.frequency.frequencyAmount > 0F) {
            binding.switchFrequency.isChecked = true
            binding.viewFrequency.visibility = View.VISIBLE
            binding.frequencyAmount.setText(args.currentItem.frequency.frequencyAmount.toString())
            binding.editTextTimeSpan.setText(args.currentItem.frequency.duration.toString())
        }

//        binding.frequencyAmount.setOnClickListener {
//            val newDialog = NumberSpinnerDialogFragment()
//            val b = Bundle()
//            b.putInt("KEY1", 1)
//            newDialog.arguments = b
//            //newDialog.setVariables("Frequency amount", 1, 50)
//            newDialog.show(childFragmentManager, "hello there")
//        }

        binding.switchFrequency.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.viewFrequency.visibility = View.VISIBLE
            } else {
                binding.viewFrequency.visibility = View.GONE
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
        if (args.currentItem.itemFavorite) {
            menu.getItem(0).setIcon(R.drawable.ic_baseline_favorite)
            favorite = true
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_baseline_favorite_border)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_favorite) {
            if (favorite) {
                item.setIcon(R.drawable.ic_baseline_favorite_border)
                favorite = false
            } else {
                item.setIcon(R.drawable.ic_baseline_favorite)
                favorite = true
            }
        } else if (i == R.id.menu_trash){
            deleteItemFromDatabase()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == binding.buttonUpdateExpiredDateDialog.id) {
                buttonId = 0
                openDateDialog(binding.updateExpiredDateTextView.text.toString())
            }
        }
    }

    private fun openDateDialog(x: String) {
        val parseDate = LocalDate.parse(x)

        DatePickerDialog(requireContext(), this, parseDate.year, parseDate.monthValue-1, parseDate.dayOfMonth).show()
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (view != null) {
            if (buttonId == 0) {
                updateItemExpiredDate = LocalDate.of(year, month+1, dayOfMonth)
                binding.updateExpiredDateTextView.text = updateItemExpiredDate.toString()
            }
        }
    }

    private fun updateItemToDatabase() {
        val itemName = binding.updateItemName.text.toString()
        val itemAmount = binding.updateItemAmount.text.toString().toFloat()
        val itemM = binding.updateSpinnerMeasurement.selectedItem.toString()
        val itemC = binding.updateSpinnerCategory.selectedItem.toString()
        val itemL = binding.updateSpinnerLocation.selectedItem.toString()
        val itemCreated = args.currentItem.itemCreated
        val itemExpired = updateItemExpiredDate
        //val itemLastTook = LocalDateTime.of(updateItemLastTookDate, updateItemLastTookTime)
        val itemNotes = binding.updateItemNotes.text.toString()
        val itemFavorite = favorite

        val frequencyAmount: Float = if (!binding.frequencyAmount.text.isNullOrEmpty()) {
            binding.frequencyAmount.text.toString().toFloat()
        } else {
            0F
        }

        val frequencyDuration: Float = if (!binding.editTextTimeSpan.text.isNullOrEmpty()) {
            binding.editTextTimeSpan.text.toString().toFloat()
        } else {
            0F
        }

        val frequencyDurationLength = binding.spinnerTime.selectedItem.toString()

        if (inputCheck(itemName)) {
            //Create Item Object
            val updatedFrequency = Frequency(frequencyAmount,frequencyDuration,frequencyDurationLength)
            val updatedItem = Item(args.currentItem.id, itemName, itemAmount, itemM, itemC, itemL, itemCreated,
                itemExpired, itemNotes, itemFavorite, args.currentItem.lastTook, updatedFrequency)

            //Add data to Database
            mItemViewModel.updateItem(updatedItem)
            Toast.makeText(requireContext(),"Updated", Toast.LENGTH_LONG).show()
            val action = UpdateFragmentDirections.actionUpdateFragmentToDetailsFragment(updatedItem)
            findNavController().navigate(action)
            Log.i("item", updatedItem.toString())
        } else {
            Toast.makeText(requireContext(),"Item name can't be empty.", Toast.LENGTH_LONG).show()
        }
    }


    private fun inputCheck(one: String): Boolean {
        return !(TextUtils.isEmpty(one))
    }

    private fun deleteItemFromDatabase() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_,_ ->
            mItemViewModel.deleteItem(args.currentItem)
            Toast.makeText(requireContext(), "Removed ${args.currentItem.itemName}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle("Delete ${args.currentItem.itemName}?")
        builder.setMessage("Are you sure you want to delete ${args.currentItem.itemName}?")
        builder.create().show()
    }

    private fun populateSpinners() {
        val spinner = binding.updateSpinnerLocation
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.location_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            binding.updateSpinnerLocation.setSelection(adapter.getPosition(args.currentItem.itemLocation))
        }

        val spinner2 = binding.updateSpinnerCategory
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
            binding.updateSpinnerCategory.setSelection(adapter.getPosition(args.currentItem.itemCategory))
        }

        val spinner3 = binding.updateSpinnerMeasurement
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.measurements_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner3.adapter = adapter
            binding.updateSpinnerMeasurement.setSelection(adapter.getPosition(args.currentItem.itemMeasurement))
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.span_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerTime.adapter = adapter
            binding.spinnerTime.setSelection(adapter.getPosition(args.currentItem.frequency.durationLength))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//class NumberSpinnerDialogFragment: DialogFragment() {
//
//    companion object {
//        fun newInstance(isStarted: Boolean): NumberSpinnerDialogFragment {
//            val f = NumberSpinnerDialogFragment()
//            val args = Bundle()
//            args.putBoolean("isStarted", isStarted)
//            f.arguments = args
//            return f
//        }
//    }
//
//    private var title: String = "Number picker"
//    private var minValue: Int = 1
//    private var maxValue: Int = 50
//    private var value: Int = minValue
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let {
//            val arg = arguments
//            val keyValue = arg?.get("KEY1")
//            val builder = AlertDialog.Builder(it)
//            val inflater = requireActivity().layoutInflater
//            val view = inflater.inflate(R.layout.integer_spinner, null)
//
//            val numberPicker = view.findViewById<NumberPicker>(R.id.numberPicker)
//            val numberPicker2 = view.findViewById<NumberPicker>(R.id.numberPicker2)
//            numberPicker.minValue = 1
//            numberPicker.maxValue = 50
//            numberPicker.value = 1
//            numberPicker.wrapSelectorWheel = true
//            numberPicker2.minValue = 0
//            numberPicker2.maxValue = 50
//            numberPicker2.wrapSelectorWheel = true
//
//            builder.setView(view)
//                .setTitle(keyValue.toString())
//                .setPositiveButton("Ok") {_,_ -> }
//                .setNegativeButton("Cancel") {_,_ ->}
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null.")
//    }
//
//    fun setVariables(pTitle: String, pMinValue: Int, pMaxValue: Int) {
//        title = pTitle
//        minValue = pMinValue
//        maxValue = pMaxValue
//    }
//}