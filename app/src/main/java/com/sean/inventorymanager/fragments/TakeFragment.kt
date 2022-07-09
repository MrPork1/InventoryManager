package com.sean.inventorymanager.fragments

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sean.inventorymanager.*
import com.sean.inventorymanager.Notification
import com.sean.inventorymanager.data.Item
import com.sean.inventorymanager.data.ItemViewModel
import com.sean.inventorymanager.data.LastTook
import com.sean.inventorymanager.databinding.FragmentTakeBinding
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.math.min

class TakeFragment : Fragment() {

    private var _binding: FragmentTakeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mItemViewModel: ItemViewModel

    private val args by navArgs<TakeFragmentArgs>()

    private var itemLastTookDate: LocalDate? = null
    private var itemLastTookTime: LocalTime? = null

    private var canAdd: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTakeBinding.inflate(inflater, container, false)

        //createNotificationChannel()

        mItemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        binding.btnLastTookDate.setOnClickListener {
            openDateDialog()
        }

        binding.btnLastTookTime.setOnClickListener {
            openTimeDialog()
        }

        binding.dialogTakeItemItemAmount.setOnClickListener {
            val newDialog = TakeFragmentItemAmountDialog()
            newDialog.show(childFragmentManager, "sup")
        }

        val currentTime = LocalDateTime.now()
        binding.dialogTakeitemDateText.text = currentTime.toLocalDate().toString()
        itemLastTookDate = currentTime.toLocalDate()

        binding.btnTake.setOnClickListener {
            if (itemLastTookDate != null && itemLastTookTime != null) {
                val itemAmount = if (!binding.textViewTakeItemItemAmount.text.toString().isNullOrEmpty()) {
                    binding.textViewTakeItemItemAmount.text.toString().toFloat()
                } else {
                    0F
                }

                var lastTookAmountAdded = itemAmount

                if (args.currentItem.lastTook != null) {
                    lastTookAmountAdded += args.currentItem.lastTook!!.lastTookAmount
                }

                val lastTookTimeData = LocalDateTime.of(itemLastTookDate, itemLastTookTime)

                if (canAdd && inputCheck(itemAmount)) {
                    val newLastTook = LastTook(lastTookAmountAdded, lastTookTimeData)
                    val newItemAmount = args.currentItem.itemAmount - itemAmount
                    val tempItem = Item(args.currentItem.id, args.currentItem.itemName, newItemAmount,
                        args.currentItem.itemMeasurement, args.currentItem.itemCategory,
                        args.currentItem.itemLocation, args.currentItem.itemCreated,
                        args.currentItem.itemExpired, args.currentItem.itemNotes,
                        args.currentItem.itemFavorite, newLastTook,
                        args.currentItem.frequency)
                    //Add data to Database
                    mItemViewModel.updateItem(tempItem)
                    //scheduleNotification()
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_takeFragment_to_todayFragment)
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
            val result = bundle.getString("amountKey")
            binding.textViewTakeItemItemAmount.text = result
        }
    }

//    private fun scheduleNotification() {
//        val intent = Intent(requireContext(), Notification::class.java)
//        val title = "Time to eat!"
//        val message = args.currentItem.itemName
//        intent.putExtra(titleExtra, title)
//        intent.putExtra(messageExtra, message)
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            requireContext(),
//            notificationID,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val time = args.currentItem.lastTook.
//    }

//    private fun createNotificationChannel() {
//        val name = "Notif Channel"
//        val desc = "A description of the Channel"
//        val important = NotificationManager.IMPORTANCE_DEFAULT
//        val channel = NotificationChannel(channelID, name, important)
//        channel.description = desc
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }

    private fun inputCheck(amount: Float): Boolean {
        if (args.currentItem.lastTook != null) {
            if (amount > 0 && amount <= args.maxAmountToTake) {
                return true
            }
        } else {
            if (amount > 0 && amount <= args.currentItem.frequency.frequencyAmount) {
                return true
            }
        }
        return false
    }

    private fun openTimeDialog() {
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(requireContext(), { view, hourOfDay, minute ->
            if (view != null) {
                val parseDate = LocalDate.parse(binding.dialogTakeitemDateText.text)
                val currentTime = LocalDateTime.now()
                val convertedTime = LocalTime.of(hourOfDay, minute)
                if (parseDate == currentTime.toLocalDate()) {
                    if (convertedTime <= currentTime.toLocalTime()) {
                        itemLastTookTime = LocalTime.of(hourOfDay, minute)
                        binding.dialogTakeitemTimeText.text = itemLastTookTime.toString()
                        canAdd = true
                    } else {
                        Toast.makeText(requireContext(), "Time must be less than current time.", Toast.LENGTH_SHORT).show()
                        canAdd = false
                    }
                } else {
                    itemLastTookTime = LocalTime.of(hourOfDay, minute)
                    binding.dialogTakeitemTimeText.text = itemLastTookTime.toString()
                    canAdd = true
                }
            }
        }, hourOfDay, minute, false)

        timePicker.show()
    }

    private fun openDateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


//        val datePicker = DatePickerDialog(requireContext(), this, year, month, day).show()
        
        val datePicker = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->
            if (view != null) {
                itemLastTookDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                binding.dialogTakeitemDateText.text = itemLastTookDate.toString()
            }
        }, year, month, day)

        datePicker.datePicker.maxDate = c.timeInMillis

        datePicker.show()
    }

//    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
//        if (view != null) {
//            itemLastTookTime = LocalTime.of(hourOfDay, minute)
//            binding.dialogTakeitemTimeText.text = itemLastTookTime.toString()
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class TakeFragmentItemAmountDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.view_edittext, null)

            val result = view.findViewById<EditText>(R.id.view_item_amount)

            builder.setView(view)
                .setTitle("Amount to take")
                .setPositiveButton("Set") {_,_ ->
                    val resultHere = result.text.toString()
                    setFragmentResult("requestKey", bundleOf("amountKey" to resultHere))
                }
                .setNegativeButton("Cancel") {_,_ ->}
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null.")
    }
}