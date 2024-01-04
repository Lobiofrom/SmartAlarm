package com.example.alarm

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.work.WorkManager
import com.example.alarm.alarm.Alarm
import com.example.alarm.gpsWorker.GpsWorker
import com.example.alarm.permissions.Permissions
import com.example.alarm.presentation.MainScreen
import com.example.alarm.ui.theme.AlarmTheme
import com.example.alarm.viewmodel.MyVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private lateinit var workManager: WorkManager

    private val viewModel by viewModel<MyVM>()

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "Permissions are not granted", Toast.LENGTH_LONG).show()
            }
        }
    private val alarm = Alarm(this)

    private val permissions = Permissions(this, launcher)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(applicationContext)

        permissions.checkPermissions()

        setContent {
            AlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val workResult = viewModel.workId?.let { id ->
                        workManager.getWorkInfoByIdLiveData(id).observeAsState().value
                    }
                    var latitude by remember {
                        mutableDoubleStateOf(0.0)
                    }
                    var longitude by remember {
                        mutableDoubleStateOf(0.0)
                    }
                    LaunchedEffect(key1 = workResult?.outputData) {
                        if (workResult?.outputData != null) {
                            latitude =
                                workResult.outputData.getDouble(GpsWorker.LATITUDE, 0.0)
                            longitude =
                                workResult.outputData.getDouble(GpsWorker.LONGITUDE, 0.0)
                            viewModel.updateLocation(latitude, longitude)
                        }
                    }

                    MainScreen(
                        alarm,
                        this,
                        workManager = workManager,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AlarmTheme {
//        val alarm = Alarm(LocalContext.current)
//        MainScreen(alarm, LocalContext.current)
//    }
//}