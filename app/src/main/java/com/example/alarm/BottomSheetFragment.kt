package com.example.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alarm.databinding.FragmentBottomsheetBinding
import com.example.alarm.model.Alarm
import com.example.alarm.model.AlarmService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BottomSheetFragment(
    private val isAdd: Boolean,
    private val oldId: Long = 0
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomsheetBinding

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val alarmsService: AlarmService
        get() = Repositories.alarmRepository as AlarmService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.timePicker.setIs24HourView(true)
        if(!isAdd) binding.heading.text = "Изменить будильник"
        binding.confirmButton.setOnClickListener {
            if(isAdd) { addNewAlarm() }
            else { changeAlarm(oldId) }
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun addNewAlarm() {
        val alarm = Alarm(
            id = 0,
            timeHours = binding.timePicker.hour,
            timeMinutes = binding.timePicker.minute,
            name = binding.signalName.text.toString(),
            enabled = 1
        )
        uiScope.launch {
            alarmsService.addAlarm(alarm)
            dismiss()
        }
    }
    private fun changeAlarm(oldId: Long) {
        val alarmNew = Alarm(
            id = oldId,
            timeHours = binding.timePicker.hour,
            timeMinutes = binding.timePicker.minute,
            name = binding.signalName.text.toString(),
            enabled = 1
        )
        uiScope.launch {
            alarmsService.updateAlarm(alarmNew)
            dismiss()
        }
    }
}