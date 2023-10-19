package com.truckspot.fragment.ui.logs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.truckspot.databinding.FragmentLogsBinding
import com.truckspot.fragment.ui.home.HomeViewModel
import com.truckspot.models.UserLog
import com.truckspot.utils.*
import com.truckspot.utils.AlertCalculationUtils.getHoursIntoMinutes
import com.truckspot.utils.PrefConstants.TRUCK_MODE_OFF
import com.truckspot.utils.PrefConstants.TRUCK_MODE_ON
import com.truckspot.utils.PrefConstants.TRUCK_MODE_SLEEPING
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogsFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()
    var days =0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _binding?.leftArrow?.setOnClickListener{
            if(days<=9) {
                binding.progressBar.isVisible = true
                homeViewModel.getLogs(1, 50, ++days)
                if (days == 0) {
                    _binding?.rightArrow?.visibility = View.GONE
                } else {
                    _binding?.rightArrow?.visibility = View.VISIBLE
                }
            }else {
                showValidationErrors("you cannot see more than 8 days old logsheet")
            }

        }
        _binding?.rightArrow?.setOnClickListener{
            if(days>0){
                binding.progressBar.isVisible = true
                homeViewModel.getLogs(1, 50, --days)

            }



        }
        homeViewModel.getLogs(1, 50, days)
        binding.progressBar.isVisible = true

        homeViewModel.getLogsLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success<*> -> {
                    if (it.data!!.status) {
                        val list = mutableListOf<ELDGraphData>()

                        val userLogs = it.data.results.userLogs
                        binding.dateTxt.text = it.data.results.queryDate
                        if(userLogs.isNotEmpty()) {
                            binding.eventLogRv.adapter = LogAdaptor(userLogs,childFragmentManager)
                            binding.eventLogRv.layoutManager =
                                LinearLayoutManager(requireContext())
                             val userLog = userLogs.last()

                            updateTimeBar(userLogs)

                            for (i in userLogs) {
                                val replacedString = i.time.replace(":", ".")
                                val time = (replacedString.substring(
                                    0,
                                    replacedString.length - 3
                                ) + "f").toFloat()
                                list.add(ELDGraphData(time, i.modename, 1))
                            }
                            if (it.data?.results?.currentTime?.isNotEmpty() == true) {
                                list.add(
                                    ELDGraphData(
                                        it.data.results.currentTime.toFloat(),
                                        userLog.modename,
                                        1
                                    )
                                )
                            }
                            binding.eldPlot.graph.invalidate()
                            binding.eldPlot.graph.plotGraph(list)
                        }
                    } else {
                        showValidationErrors(it.message.toString())
                    }
//                    tokenMana2505ger.saveToken(it.data!!.token)
                }
                is NetworkResult.Error<*> -> {
                    showValidationErrors(it.message.toString())
                }
                is NetworkResult.Loading<*> -> {
                    //  binding.progressBar.isVisible = true
                }
            }
        })

        return root
    }

    private val TAG = "LogsFragment"
    private fun updateTimeBar(userLogsList: List<UserLog>) {
        var totalHour = 0.0
        val userLogs = userLogsList.toMutableList()
        if (userLogs.isNotEmpty()) {
            Log.d(TAG, "updateTimeBar off mode time ---> ${userLogs[0].time}")
            if (userLogs[0].time == "00:00") userLogs.removeAt(0)
        }

        val offModeHours = AlertCalculationUtils.calculate(userLogs, TRUCK_MODE_OFF)
        Log.d(TAG, "lasOffMode total hours $offModeHours")
        binding.eldPlot.offDutyHours.text = getHoursIntoMinutes(offModeHours)
        totalHour += offModeHours

        val onModeHours = AlertCalculationUtils.calculate(userLogs, TRUCK_MODE_ON)
        Log.d(TAG, "lasOffMode total hours $onModeHours")
        binding.eldPlot.onDutyHours.text = getHoursIntoMinutes(onModeHours)
        totalHour += onModeHours

        val sleepModehours = AlertCalculationUtils.calculate(userLogs, TRUCK_MODE_SLEEPING)
        Log.d(TAG, "lasOffMode total hours $sleepModehours")
        binding.eldPlot.sbHours.text = getHoursIntoMinutes(sleepModehours)
        totalHour += sleepModehours


        val DrivingModeHours = AlertCalculationUtils.calculate(
            userLogs, PrefConstants.TRUCK_MODE_DRIVING
        )
        Log.d(TAG, "lasOffMode total hours $DrivingModeHours")
        binding.eldPlot.drivingHours.text = getHoursIntoMinutes(DrivingModeHours)
        totalHour += DrivingModeHours

        binding.eldPlot.totalHours.text = getHoursIntoMinutes(totalHour)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showValidationErrors(
        error: String, dialogInterface: Utils.dialogInterface? = null
    ) {

        Utils.dialog(requireContext(), message = error, callback = dialogInterface)
    }
}