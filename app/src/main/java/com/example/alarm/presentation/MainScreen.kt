package com.example.alarm.presentation

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.example.alarm.alarm.Alarm
import com.example.alarm.calculateSunRise.CalculateSunRise
import com.example.alarm.gpsWorker.GpsWorker
import com.example.alarm.notifications.Notifications
import com.example.alarm.ringtonePicker.RingtonePickerContract
import com.example.alarm.viewmodel.MyVM
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    alarm: Alarm,
    context: Context,
    viewModel: MyVM,
    workManager: WorkManager
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState(
        is24Hour = true
    )
    val snackState = remember { SnackbarHostState() }

    val formatter = remember { SimpleDateFormat("yyyy:MM:dd:hh:mm", Locale.getDefault()) }

    var selectedMelodyUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val getContent = rememberLauncherForActivityResult(RingtonePickerContract()) {
        selectedMelodyUri = it
    }
    var sunRise by remember {
        mutableStateOf<CalculateSunRise?>(null)
    }
    if (
        viewModel.locationLat != null &&
        viewModel.locationLat != 0.0 &&
        viewModel.locationLong != null &&
        viewModel.locationLong != 0.0
    ) {
        if (selectedMelodyUri == null) {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Выберите мелодию", Toast.LENGTH_SHORT).show()
                getContent.launch()
            }
        } else {
            LaunchedEffect(Unit) {
                sunRise = CalculateSunRise(viewModel.locationLat!!, viewModel.locationLong!!)
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_MONTH, 1)
                sunRise!!.times.rise?.hour?.let { cal.set(Calendar.HOUR_OF_DAY, it) }
                sunRise!!.times.rise?.minute?.let { cal.set(Calendar.MINUTE, it) }
                cal.isLenient = false

                alarm.createAlarm(cal.timeInMillis, state.hour, state.minute, selectedMelodyUri)
                Log.d("timeInMillis", "timeInMillis====${formatter.format(cal.timeInMillis)}")

                Notifications.createNotification(
                    context,
                    state.hour,
                    state.minute,
                    sunRise?.times?.rise?.hour,
                    sunRise?.times?.rise?.minute
                )
                Log.d("createAlarm", "createAlarm")
            }
        }
    }

    Box(propagateMinConstraints = false) {
        Text(
            text = "Через какое время после восхода сработает будильник?",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 26.dp),
            textAlign = TextAlign.Center
        )
        Button(modifier = Modifier
            .align(Alignment.Center)
            .padding(top = 160.dp),
            onClick = {
                showTimePicker = true
                getContent.launch()
            }) {
            Text("Установить")
        }
        SnackbarHost(hostState = snackState)
    }

    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                GpsWorker.createWorkManager(viewModel, workManager)
                showTimePicker = false
            },
        ) {
            TimePicker(state = state)
        }
    }
}
