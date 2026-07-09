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

/**
 * A `key` is required (rather than relying on Compose's default class-based key) because
 * generic ViewModels such as ListViewModel<T> erase to the same runtime class regardless of
 * T. Without a distinct key, screens sharing one ViewModelStoreOwner (e.g. tabs switched via
 * a plain `when`, not separate nav destinations) would collide on a single cached instance.
 */
@Composable
inline fun <reified VM : ViewModel> appViewModel(
    container: AppContainer,
    key: String,
    noinline create: (AppContainer) -> VM,
): VM = viewModel(factory = ViewModelFactory(create, container), key = key)
