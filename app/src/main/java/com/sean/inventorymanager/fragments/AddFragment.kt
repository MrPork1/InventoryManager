package com.sean.inventorymanager.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sean.inventorymanager.R
import com.sean.inventorymanager.data.Frequency
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.data.ItemViewModel
import com.sean.inventorymanager.data.LastTook
import com.sean.inventorymanager.databinding.FragmentAddBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class addFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!


    private lateinit var mItemViewModel: ItemViewModel

    var itemExpiredDate: LocalDate = LocalDate.of(2022,6,15)
    var itemLastTookDate: LocalDate = LocalDate.of(2022,6,15)
    var itemLastTookTime: LocalTime = LocalTime.of(1,30)

    var favorite: Boolean = false
    var buttonId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view =  binding.root

        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        populateSpinners()

        binding.buttonAddItem.setOnClickListener {
            insertDataToDatabase()
        }

//        binding.buttonLasttookShowDateDialog.setOnClickListener {
//            buttonId = 1
//            openDateDialog()
//        }

        binding.buttonExpiredDateDialog.setOnClickListener {
            buttonId = 0
            openDateDialog()
        }

//        binding.buttonLasttookShowTimeDialog.setOnClickListener {
//            openTimeDialog()
//        }

        binding.switchFrequency.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.viewFrequency.visibility = View.VISIBLE
            } else {
                binding.viewFrequency.visibility = View.GONE
            }
        }

        binding.locationParent.setOnClickListener {
            //Toast.makeText(requireContext(), "hello there", Toast.LENGTH_SHORT).show()
            val dialog = AddDialogFragments.newInstance("Choose location",
                "hello there message").show(childFragmentManager, AddDialogFragments.TAG)
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        childFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
            val result = bundle.getString("locationKey")
            binding.locationSelectedText.text = result
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDatabase() {
        val itemName = binding.itemName.text.toString()
        val itemAmount: Float = if (!binding.itemAmount.text.toString().isNullOrEmpty()) {
            binding.itemAmount.text.toString().toFloat()
        } else {
            0F
        }

        val itemM = binding.spinnerMeasurement.selectedItem.toString()
        val itemC = binding.spinnerCategory.selectedItem.toString()
        val itemL = binding.locationSelectedText.text.toString()
        val itemCreated = getCurrentDate()
        val itemExpired = itemExpiredDate
        val itemNotes = binding.itemNotes.text.toString()
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
            //Create Item Object and Frequency Object
            val frequency = Frequency(frequencyAmount,frequencyDuration, frequencyDurationLength)

            //val lastTook = LastTook(3.0, itemLastTook)
            val item = Item(0, itemName, itemAmount, itemM, itemC, itemL, itemCreated,
                itemExpired, itemNotes, itemFavorite, null, frequency)

            //Add data to Database
            mItemViewModel.addItem(item)
            Toast.makeText(requireContext(),"Added", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
            Log.i("item", item.toString())
        } else {
            Toast.makeText(requireContext(),"Item name can't be empty.", Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(one: String): Boolean {
        return !(TextUtils.isEmpty(one))
    }

    private fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

    private fun openDateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        DatePickerDialog(requireContext(), this, year, month, day).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (view != null) {
            if (buttonId == 0) {
                Log.i("this", "the expired date ran")
                itemExpiredDate = LocalDate.of(year, month + 1, dayOfMonth)
                binding.expiredDateTextView.text = itemExpiredDate.toString()
            }
        }
    }

    private fun populateSpinners() {
        val spinner = binding.spinnerLocation
         //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.location_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerCategory.adapter = adapter
        }

        val spinner3 = binding.spinnerMeasurement
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
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AddDialogFragments: DialogFragment() {

    companion object {
        const val TAG = "Hello there"
        private const val ARG_TITLE = "argTitle"
        private const val ARG_MESSAGE = "argMessage"

        fun newInstance(title: String, message: String) = AddDialogFragments().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
            }
        }
    }

    private var title: String? = null
    private var message: String? = null
    private var result: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val newList = resources.getStringArray(R.array.location_array)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
                .setTitle(title)
                .setPositiveButton("Ok") {_,_ ->
                    setFragmentResult("requestKey", bundleOf("locationKey" to result))
                }
                .setNegativeButton("Cancel") {_,_ ->
                }
                builder.setSingleChoiceItems(newList, -1, DialogInterface.OnClickListener { dialog, which ->
                    val preview = newList[which]
                    result = preview
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null.")
    }
}