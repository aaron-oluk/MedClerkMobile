package com.example.medclerkmobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import com.example.medclerkmobile.data.AppContainer

class ViewModelFactory(private val create: (AppContainer) -> ViewModel, private val container: AppContainer) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = create(container) as T
}

@Composable
inline fun <reified VM : ViewModel> appViewModel(
    container: AppContainer,
    noinline create: (AppContainer) -> VM,
): VM = viewModel(factory = ViewModelFactory(create, container))
