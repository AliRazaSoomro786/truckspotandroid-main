package com.truckspot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truckspot.models.CertifyModelItem
import com.truckspot.repository.CertifyRepository
import com.truckspot.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class CertifyViewModel(private val certifyRepository: CertifyRepository) : ViewModel() {

    // Observe LiveData with CertifyModelItem type
    val cData: LiveData<NetworkResult<CertifyModelItem>>
        get() = certifyRepository.certified

    init {
        viewModelScope.launch(Dispatchers.IO) {
            certifyRepository.CertifiedData()
        }
    }
}
