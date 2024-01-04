package com.example.alarm.di

import com.example.alarm.viewmodel.MyVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { MyVM() }

}
