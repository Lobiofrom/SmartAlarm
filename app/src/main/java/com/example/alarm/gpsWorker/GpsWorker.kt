package com.example.alarm.gpsWorker

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.alarm.viewmodel.MyVM
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GpsWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {

            val location = suspendCoroutine { continuation ->
                val fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                            CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    })
                    .addOnSuccessListener {
                        continuation.resume(it)
                    }
            }

            if (location == null) {
                Result.failure()
            } else {
                Result.success(
                    workDataOf(
                        LONGITUDE to location.longitude,
                        LATITUDE to location.latitude
                    )
                )
            }
        }
    }

    companion object {
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        fun createWorkManager(viewModel: MyVM, workManager: WorkManager) {
            val request = OneTimeWorkRequestBuilder<GpsWorker>()
                .build()
            viewModel.updateWork(request.id)
            workManager.enqueue(request)
        }
    }
}