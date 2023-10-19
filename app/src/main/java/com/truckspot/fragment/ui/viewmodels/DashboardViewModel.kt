package com.truckspot.fragment.ui.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.truckspot.models.TrackingModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(var app: Application): ViewModel() {
   // var trackingModel: ObservableField<TrackingModel> = ObservableField(TrackingModel())
}