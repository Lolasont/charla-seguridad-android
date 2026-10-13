package com.example.charlasinacap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.charlasinacap.data.InacapRepository

class AsaaViewModelFactory(
    private val repository: InacapRepository = InacapRepository.getInstance(com.example.charlasinacap.App.instance)
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return com.example.charlasinacap.viewmodel.AsaaViewModel(repository) as T
    }
}
