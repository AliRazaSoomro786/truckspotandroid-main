package com.truckspot.fragment.ui.home

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.gson.Gson
import com.truckspot.R
import com.truckspot.R.color
import com.truckspot.R.color.colorPrimary
import com.truckspot.R.drawable
import com.truckspot.R.drawable.home_bg_deign
import com.truckspot.R.drawable.home_bg_design_selected
import com.truckspot.R.layout.*
import com.truckspot.R.string.shipping_number
import com.truckspot.R.string.trailer_number
import com.truckspot.databinding.FragmentHomeBinding
import com.truckspot.fragment.Dashboard
import com.truckspot.fragment.ui.viewmodels.DashboardViewModel
import com.truckspot.models.DRIVE_MODE.*
import com.truckspot.models.SPopupData
import com.truckspot.models.TrackingModelNew
import com.truckspot.models.UserLog
import com.truckspot.pt.devicemanager.AppModel
import com.truckspot.request.AddLogRequest
import com.truckspot.request.updateLogRequest
import com.truckspot.utils.*
import com.truckspot.utils.AlertCalculationUtils.decimalHours
import com.truckspot.utils.AlertCalculationUtils.getHours
import com.truckspot.utils.PrefConstants.DRIVING_PER_DAY
import com.truckspot.utils.PrefConstants.DUTY_PER_DAY
import com.truckspot.utils.PrefConstants.EIGHT_DAY_PERIOD
import com.truckspot.utils.PrefConstants.MAX_DRIVING_HOURS
import com.truckspot.utils.PrefConstants.TRUCK_MODE_DRIVING
import com.truckspot.utils.PrefConstants.TRUCK_MODE_OFF
import com.truckspot.utils.PrefConstants.TRUCK_MODE_ON
import com.truckspot.utils.PrefConstants.TRUCK_MODE_SLEEPING
import com.truckspot.utils.PrefConstants.allowedContinousDrivingLimit
import com.truckspot.utils.Utils.dialogInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.joda.time.DateTime
import java.math.BigDecimal
import java.math.BigDecimal.valueOf
import java.math.RoundingMode.FLOOR
import java.text.SimpleDateFormat
import java.util.*


/*
 - if speed 0 and driving mode enabled line should be on ON-MODE
- if vehicle is moving and driving mode is enabled then graph line should be on D MODE
- When speed is 0 then if you press D mode then shift to ON-MODE
*/

@AndroidEntryPoint
class HomeFragment : Fragment(), OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    lateinit var prefRepository: PrefRepository

    var mActivity: FragmentActivity? = null
    private val TAG = "HomeFragment"
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()
    private val dashboardViewModel by activityViewModels<DashboardViewModel>()

    val timerManager = TimerManager()


    private var clockJob: Job? = null
    private var locationJob: Job? = null
    private lateinit var offDropdown: Spinner


    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private val apiScope = CoroutineScope(job + Dispatchers.IO)

    companion object {
        val isTesting = true;
    }

    var hrs_MODE_OFF = 0.0
    var hrs_MODE_ON = 0.0
    var hrs_MODE_D = 0.0
    var hrs_MODE_SB = 0.0

    var miles: BigDecimal? = null
    var mOdometer: String? = null
    var mEngineHours: String? = null
    var speed: Int = 0
    var location: String? = null

    var listTracking: ArrayList<TrackingModelNew> = arrayListOf()
    var isEmptyList = false
    lateinit var yoYo: YoYo.AnimationComposer

    val svcIf = IntentFilter()
    val tviIf = IntentFilter()
    val tmIf = IntentFilter()
    var tmRefresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "tmRefresh broadcast")
            updateTelemetryInfo()
        }
    }
    var svcRefresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "svcRefresh broadcast")
            updateTelemetryInfo()
        }
    }

    var viRefresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateVehicleInfo()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val offDropdown = view?.findViewById<Spinner>(R.id.offDropdown)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mActivity = activity
        yoYo = YoYo.with(Techniques.Tada)
        prefRepository = PrefRepository(mActivity!!)
        binding.tvOff.setOnClickListener(this)
        binding.tvSb.setOnClickListener(this)
        binding.tvD.setOnClickListener(this)
        binding.tvOn.setOnClickListener(this)
        binding.tvPerosnaluse.setOnClickListener(this)
         binding.tvYarduse.setOnClickListener(this)

        val list = mutableListOf<ELDGraphData>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        homeViewModel.getLogsLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success<*> -> {
                    if (it.data!!.status) {
                        list.clear()
                        val userLogs = it.data.results.userLogs
                        Log.d(TAG, "userlogs ${Gson().toJson(userLogs)}")
                        Log.d(TAG, "userlogs size ${userLogs.size}")
                        if (userLogs.isNotEmpty()) {
                            isEmptyList = false
                            val userLog = userLogs.last()
                            Log.d("checkingthelastlog","herelast:${userLog}")
                            prefRepository.setMode(userLog.modename)
                            Log.d(
                                TAG, "storing last mode from API ---> ${prefRepository.getMode()}"
                            )

                            updateTimeBar(userLogs)
                            userLogs.forEachIndexed { index, userLog ->
                                val replacedString = userLog.time.replace(":", ".")
                                val time = (replacedString.substring(
                                    0, replacedString.length - 3
                                ) + "f").toFloat()

                                Log.d("checkthetime","time:${time}")
                                list.add(ELDGraphData(time, userLog.modename, time.toLong()))
                                when (userLog.modename) {
                                    TRUCK_MODE_OFF -> {
                                        hrs_MODE_OFF += userLog.hours
                                    }

                                    TRUCK_MODE_ON -> {
                                        hrs_MODE_ON += userLog.hours
                                    }

                                    TRUCK_MODE_SLEEPING -> {
                                        hrs_MODE_SB += userLog.hours
                                    }

                                    TRUCK_MODE_DRIVING -> {
                                        hrs_MODE_D += userLog.hours
                                    }
                                }
                            }

                            if (homeViewModel.getLogsLiveData.value != null && homeViewModel.getLogsLiveData.value!!.data != null && homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.isNotEmpty()) {
                                val mode =
                                    homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.last().modename
//                                (activity as Dashboard).CurrentMode = mode
                                Log.d(TAG, "last mode ---> $mode")
                                when (mode) {
                                    TRUCK_MODE_OFF -> {
                                        homeViewModel.trackingMode.set(MODE_OFF)
                                        startTimer()
                                        updateUI(binding.tvOff)
                                        startLiveTimer("Time spent in OFF DUTY")
                                    }

                                    TRUCK_MODE_ON -> {
                                        homeViewModel.trackingMode.set(MODE_ON)
                                        startTimer()
                                        updateUI(binding.tvOn)
                                        startLiveTimer("Time spent in ON DUTY")
                                    }

                                    TRUCK_MODE_SLEEPING -> {
                                        homeViewModel.trackingMode.set(MODE_SB)
                                        startTimer()
                                        updateUI(binding.tvSb)
                                        startLiveTimer("Time spent in SLEEP")
                                    }

                                    TRUCK_MODE_DRIVING -> {
                                        homeViewModel.trackingMode.set(MODE_D)
                                        startTimer()
                                        updateUI(binding.tvD)
                                        startLiveTimer("Time spent in DRIVING")
                                    }
                                }
                            } else {
                                makeText(
                                    activity, "No Data Found for Today", Toast.LENGTH_SHORT
                                ).show()
                            }

                            binding.eldGraph.graph.invalidate()
                            binding.eldGraph.graph.plotGraph(list)

                        } else {
                            isEmptyList = true
                            setDefaultButton("inital")
                        }
                    }
                }

                is NetworkResult.Error<*> -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading<*> -> {
                    binding.progressBar.isVisible = true
                }
            }
        }

        homeViewModel.logResponseLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success<*> -> {
                    if (it.data!!.status) {
                        makeText(requireContext(), "Successfully Submitted", LENGTH_LONG).show()
                        homeViewModel.getLogs(1, 50, 0)
                    } else {
                        showValidationErrors(it.data.message)
                    }
                }

                is NetworkResult.Error<*> -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading<*> -> {
                    binding.progressBar.isVisible = true
                }
            }
        }

        binding.imgContinousDriving.setOnClickListener {
            // show continuous driving dialog
            val hoursSpent = getTimeSheetHours(TRUCK_MODE_DRIVING)
            var hoursLeft = allowedContinousDrivingLimit - hoursSpent

            if (hoursLeft < 0) hoursLeft = 0.0
            val violationHours: Double = (if (hoursSpent > MAX_DRIVING_HOURS) {
                hoursSpent - MAX_DRIVING_HOURS
            } else 0.0)

            val data = SPopupData.Data(
                drawable.user,
                "Continuous Driving",
                "Change your duty status to Driving to start with",
                MAX_DRIVING_HOURS,
                hoursSpent,
                hoursLeft,
                violationHours
            )

            showPopup(data)

        }

        binding.imgperday.setOnClickListener {
            val userLog = getUserLogs()
            if (userLog != null) {
                val hoursSpent = AlertCalculationUtils.calculate(userLog).second
                var hoursLeft = DRIVING_PER_DAY - hoursSpent

                if (hoursLeft < 0) hoursLeft = 0.0
                val violationHours: Double = (if (hoursSpent > DRIVING_PER_DAY) {
                    hoursSpent - DRIVING_PER_DAY
                } else 0.0)

                val data = SPopupData.Data(
                    drawable.delivery,
                    "Driving Per Day",
                    "Change your duty status to Driving to start with",
                    DRIVING_PER_DAY,
                    hoursSpent,
                    hoursLeft,
                    violationHours
                )

                showPopup(data)
            } else {
                makeText(activity, "No user record found", LENGTH_LONG).show()
            }


        }

        binding.imgBreak.setOnClickListener {
            val userLog = getUserLogs()
            if (userLog != null) {
                val hoursSpent = AlertCalculationUtils.calculate(userLog).second
                var hoursLeft = DUTY_PER_DAY - hoursSpent
                if (hoursLeft < 0) hoursLeft = 0.0
                val violationHours: Double = (if (hoursSpent > DUTY_PER_DAY) {
                    hoursSpent - DUTY_PER_DAY
                } else 0.0)
                // sleeping mode data here
                val data = SPopupData.Data(
                    drawable.smile,
                    "Duty Per Day",
                    "Change your duty status to either of the Duty status to start with",
                    DUTY_PER_DAY,
                    hoursSpent,
                    hoursLeft,
                    violationHours
                )
                showPopup(data)
            }

        }

        binding.imgWeekWise.setOnClickListener {

/*
            apiScope.launch {
                val response = async { return@async homeViewModel.getCustomLogs(0, 50, 4) }.await()
                Log.d(TAG, "7Days logs ${response.body().toString()}")
            }
*/

            val hoursSpent = getTimeSheetHours(TRUCK_MODE_DRIVING)
            val hoursLeft = EIGHT_DAY_PERIOD - hoursSpent

            val violationHours: Double = (if (hoursSpent > EIGHT_DAY_PERIOD) {
                hoursSpent - EIGHT_DAY_PERIOD
            } else 0.0)

            val data = SPopupData.Data(
                drawable.house,
                "8 Day Period",
                "Change your duty status to either of the Duty status to start with",
                EIGHT_DAY_PERIOD,
                hoursSpent,
                hoursLeft,
                violationHours
            )

            showPopup(data)
        }

        binding.editShipping.setOnClickListener {
            editShippingNumberPopup()
        }

        binding.editCoDriver.setOnClickListener {
            editCoDriverPopup()
        }

        binding.editTrailerNo.setOnClickListener {
            editTrailerNumberPopup()
        }

        updateUI()

        return root
    }

    override fun onResume() {
        super.onResume()
        getInstance(requireContext()).registerReceiver(tmRefresh, tmIf)
        getInstance(requireContext()).registerReceiver(svcRefresh, svcIf)
        getInstance(requireContext()).registerReceiver(viRefresh, tviIf)
        homeViewModel.getLogs(1, 50, 0)

        updateTelemetryInfo()
        startClock()
        locationJob = (activity as Dashboard).locationFlow
            .onEach { location -> handleLocationUpdate(location) }
            .launchIn(lifecycleScope)
    }

    private fun handleLocationUpdate(latest: Location) {
        Log.d(TAG, "latest object received")
        val currentMode = prefRepository.getMode()
        if (latest.speed.toInt() > 2 && currentMode == "on") {
            Log.d(TAG, "Truck is running & in on mode")
            if (binding != null) updateUI(binding.tvD)
            updateModeChange(hrs_MODE_D, "d")
            startLiveTimer("Time spent in DRIVING")
        } else if (latest.speed.toInt() == 0 && currentMode == "d") {
            Log.d(TAG, "driving mode found & speed is zero")
            updateUI(binding.tvOn)
            updateModeChange(hrs_MODE_ON, "on")
            startLiveTimer("Time spent in ON DUTY")
        }

        if (currentMode == TRUCK_MODE_DRIVING && latest.speed.toInt() > 2) {
            if (binding.speedMeter.visibility == GONE)
                binding.speedMeter.visibility = VISIBLE
            binding.speed.text = latest.speed.toString().plus("kph")
        }

        Log.d(TAG, "location callback current mode is --> $currentMode")
        Log.d(TAG, "location callback truck speed ---> ${latest.speed.toInt()}")
    }

    override fun onStop() {
        super.onStop()
        getInstance(requireContext()).unregisterReceiver(tmRefresh)
        getInstance(requireContext()).unregisterReceiver(svcRefresh)
        getInstance(requireContext()).unregisterReceiver(viRefresh)
        timerManager.stopTimer()
        clockJob?.cancel()
        locationJob?.cancel()
        locationJob = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startClock() {
        clockJob = GlobalScope.launch {
            while (true) {
                delay(1000)
                val currentTime = getCurrentTime()
                withContext(Dispatchers.Main) {
                    binding.liveClock.text = currentTime
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateTime = DateTime(currentTime)
        val hour = dateTime.hourOfDay
        val minute = dateTime.minuteOfHour
        val second = dateTime.secondOfMinute

        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        return String.format("%02d:%02d:%02d %s", hour12, minute, second, amPm)
    }

    private fun getTimeSheetHours(mode: String): Double {
        if (homeViewModel.getLogsLiveData.value != null
            && homeViewModel.getLogsLiveData.value!!.data != null
            && homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.isNotEmpty()) {
            val logs = homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs
            val lastLogs = lastMode(logs, mode) ?: return 0.0
            return getHours(lastLogs.timesheet)
        }

        return 0.0
    }

    private fun updateTimeBar(userLogsList: List<UserLog>) {
        var totalHour = 0.0
        val userLogs = userLogsList.toMutableList()
        Log.d("timebarlogs","${userLogs}")
        if (userLogs.isNotEmpty()) {
            Log.d(TAG, "updateTimeBar off mode time ---> ${userLogs[0].time}")
            if (userLogs[0].time == "00:00") userLogs.removeAt(0)
        }

        val offModeHours = AlertCalculationUtils.calculate(userLogs, TRUCK_MODE_OFF)
        val offModeHoursInt = offModeHours.toInt()
        val offModeMinutes = ((offModeHours - offModeHoursInt) * 60).toInt()
        binding.eldGraph.offDutyHours.text = formatTime(offModeHoursInt, offModeMinutes)

        val onModeHours = AlertCalculationUtils.calculate(userLogs, TRUCK_MODE_ON)
        val onModeHoursInt = onModeHours.toInt()
        val onModeMinutes = ((onModeHours - onModeHoursInt) * 60).toInt()
        binding.eldGraph.onDutyHours.text = formatTime(onModeHoursInt, onModeMinutes)

        val sleepModeHours = AlertCalculationUtils.calculate(userLogs, TRUCK_MODE_SLEEPING)
        val sleepModeHoursInt = sleepModeHours.toInt()
        val sleepModeMinutes = ((sleepModeHours - sleepModeHoursInt) * 60).toInt()
        binding.eldGraph.sbHours.text = formatTime(sleepModeHoursInt, sleepModeMinutes)

        val drivingModeHours = AlertCalculationUtils.calculate(userLogs, TRUCK_MODE_DRIVING)
        val drivingModeHoursInt = drivingModeHours.toInt()
        val drivingModeMinutes = ((drivingModeHours - drivingModeHoursInt) * 60).toInt()
        binding.eldGraph.drivingHours.text = formatTime(drivingModeHoursInt, drivingModeMinutes)
    }
   private fun formatTime(hours: Int, minutes: Int): String {
        return String.format("%02d:%02d", hours, minutes)
    }
    private fun lastMode(userLogs: List<UserLog>, mode: String): UserLog? {
        var lastLogs: UserLog? = null
        for (logs in userLogs) if (logs.modename == mode) lastLogs = logs

        return lastLogs
    }

    var isNeedToconnect = true

    override fun onClick(v: View) =
        if (mEngineHours.isNullOrEmpty() && isNeedToconnect && !isTesting) {
            Utils.dialog(requireContext(),
                message = "Please connect to a device first",
                negativeText = "Cancel",
                callback = object : dialogInterface {
                    override fun positiveClick() {
                        (activity as Dashboard).binding.appBarDashboard.fab.performClick()
                    }

                    override fun negativeClick() {}
                })
        } else {
            when (v.id) {
                R.id.tvD -> drivingMode()
                R.id.tvOff -> offMode()
                R.id.tvOn -> onMode()
                R.id.tvSb -> sbMode()
                else -> {}
            }

        }

    private fun sbMode() {
        if (!isClickable()) {
            Log.d(TAG, "Vehicle is running unable to click")
            return
        }

        if (MODE_SB != homeViewModel.trackingMode.get()!! || isEmptyList) {
            updateUI(binding.tvSb)
            updateModeChange(hrs_MODE_SB, TRUCK_MODE_SLEEPING)
        }

        startLiveTimer("Time spent in SLEEP")
    }
    private fun drivingMode() {
        if (MODE_D != homeViewModel.trackingMode.get()!! || isEmptyList) {
            val speed = (activity as Dashboard).gSpeed
            if (speed.toInt() == 0 || prefRepository.getMode() == TRUCK_MODE_OFF) {
                Log.d(TAG, "speed is zero or truck mode is off")
                return
            }

            updateUI(binding.tvD)
            updateModeChange(hrs_MODE_D, TRUCK_MODE_DRIVING)
            startLiveTimer("Time spent in DRIVING")
        }
    }

    private fun onMode() {
        if (binding.tvConected.text.toString() != "Connected") {
            Log.d(TAG, "vehicle is not connected")
            return
        }

        if (!isClickable() && prefRepository.getMode() == TRUCK_MODE_DRIVING) {
            Log.d(TAG, "Vehicle is running unable to click")
            return
        }

        if (MODE_ON != homeViewModel.trackingMode.get()!! || isEmptyList) {
            updateUI(binding.tvOn)
            updateModeChange(hrs_MODE_ON, TRUCK_MODE_ON)
        }


        startLiveTimer("Time spent in ON DUTY")
    }

    private fun offMode() {
        if (!isClickable()) {
            Log.d(TAG, "Vehicle is running unable to click")
            return
        }

        if (MODE_OFF != homeViewModel.trackingMode.get()!! || isEmptyList) {
            updateUI(binding.tvOff)
            updateModeChange(hrs_MODE_OFF, TRUCK_MODE_OFF)
        }

        startLiveTimer("Time spent in OFF DUTY")
    }

    private fun startLiveTimer(label: String) {
        timerManager.stopTimer()
        binding.timerLabelTv.text = label
        timerManager.setTimerListener(object : TimerListener {
            override fun onTimeUpdate(formattedTime: String) {
                binding.timerTv.text = formattedTime
            }
        })
        timerManager.startTimer()
    }

    private fun isClickable(): Boolean {
        val speed = (activity as Dashboard).gSpeed.toInt()
        return speed <= 2
    }

    private fun updateModeChange(hoursLast: Double, mode: String) {
        if (mode != TRUCK_MODE_DRIVING) {
            binding.speedMeter.visibility = GONE
        } else {
            binding.speedMeter.visibility = VISIBLE
        }
        Log.d(TAG, "updating mode --> $mode")
        prefRepository.setMode(mode)

        Log.d("checkingshipping","shippinghere : ${prefRepository.getShippingNumber()}")

        if (isTesting) {
            mOdometer = "23232"
            mEngineHours = "2122323"
        }

        val logRequest = mOdometer?.let {
            mEngineHours?.let { it1 ->
                Log.d("checktelemetery", "here-it-is: ${it1}")
                var shippingNumber = 0
                if (prefRepository.getShippingNumber().isNotEmpty())
                    shippingNumber = prefRepository.getShippingNumber().toInt()

                var trailerNumber = 0
                if (prefRepository.getTrailerNumber().isNotEmpty())
                    trailerNumber = prefRepository.getTrailerNumber().toInt()

                AddLogRequest(
                    mode, it, it1, 2, location, 1, shippingNumber,
                    trailerNumber, "1C6RREHT5NN451094"
                )
            }
        }

        if (logRequest != null) {
            Log.d(TAG, "logRequest is not null")
            if (homeViewModel.getLogsLiveData.value != null
                && homeViewModel.getLogsLiveData.value!!.data != null
                && homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.isNotEmpty()
            ) {
                val toDayDate = DateFormat.format("dd-MM-yyy", Date()).toString()
                val updateLogRequest = updateLogRequest(


                    homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.last().id,
                    hoursLast,
                    toDayDate,
                    homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.last().modename,
                    "loc not available",
                    homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.last().odometerreading,
                    homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.last().eng_hours,
                    homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.last().time,
                     0



                    )
                Log.d(TAG, "ready to update logs $hoursLast")
//                homeViewModel.updateLog(updateLogRequest)
                homeViewModel.logUser(logRequest) // adding user logs record here & state information as well
            } else {
                homeViewModel.logUser(logRequest)
            }
        } else {
            showValidationErrors("Please Connect to Device First")
        }
    }


    private fun updateUI(viewSelect: View) {
        binding.tvOff.background = getDrawable(resources, home_bg_deign, null)
        binding.tvOff.setTextColor(getColor(resources, colorPrimary, null))

        binding.tvSb.background = getDrawable(resources, home_bg_deign, null)
        binding.tvSb.setTextColor(getColor(resources, colorPrimary, null))

        binding.tvD.background = getDrawable(resources, home_bg_deign, null)
        binding.tvD.setTextColor(getColor(resources, colorPrimary, null))

        binding.tvOn.background = getDrawable(resources, home_bg_deign, null)
        binding.tvOn.setTextColor(getColor(resources, colorPrimary, null))

        binding.tvPerosnaluse.background = getDrawable(resources, home_bg_deign, null)
        binding.tvPerosnaluse.setTextColor(getColor(resources, colorPrimary, null))

        binding.tvYarduse.background = getDrawable(resources, home_bg_deign, null)
        binding.tvYarduse.setTextColor(getColor(resources, colorPrimary, null))

        viewSelect.background = getDrawable(resources, home_bg_design_selected, null)
        if (viewSelect is TextView)
            viewSelect.setTextColor(
                getColor(resources, color.white, null)
            )


    }

    private fun showValidationErrors(error: String, dialogInterface: dialogInterface? = null) {

        Utils.dialog(requireContext(), message = error, callback = dialogInterface)
    }

    var timeInMiliSeconds: Long = 518400 * 1000
    var mCountDownTimer: CountDownTimer? = null

    fun alertAnimation(imageView: View) {
        yoYo.duration(700).repeat(-1).playOn(imageView)
    }
    private fun updateTelemetryInfo() {
        val dashboard = activity as Dashboard
        if (AppModel.getInstance().mLastEvent != null) {
            try {
                val te = AppModel.getInstance().mLastEvent
                speed = te.mGeoloc.speed


                val km = BigDecimal(te.mOdometer)
                val miless = km.multiply(valueOf(0.621371))
                miles = miless.setScale(2, FLOOR)

                dashboard.binding.appBarDashboard.fab.text = "DisConnect"
                binding.tvConected.text = "Connected"
                 location = "`${te.mGeoloc.latitude},${te.mGeoloc.longitude}`"
                mOdometer = te.mOdometer

                mEngineHours = te.mEngineHours
                val speeed = dashboard.gSpeed > 2
                val mode = prefRepository.getMode()
                if (mode == "inital") {
                    return
                }

                binding.odometer.text = mOdometer.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                //    Log.e(TrackerViewTabGpsFragment.TAG, e.fillInStackTrace().toString())
            }
        } else {
            binding.ManufactureName.text = ""
            dashboard.binding.appBarDashboard.fab.text = "CONNECT"
            binding.tvConected.text = "Not Connected"

        }
    }

    fun updateVehicleInfo() {
        if (AppModel.getInstance().mVehicleInfo != null) {
            val vi = AppModel.getInstance().mVehicleInfo
            binding.ManufactureName.text = "Vin-" + vi.VIN
            binding.vinNumber.text = vi.VIN
            try {
                makeText(activity,"Vin number is ${vi.VIN}",LENGTH_LONG).show()
            }catch (e : Exception){

            }
        }
    }

    private fun getUserLogs(): List<UserLog>? {
        if (homeViewModel.getLogsLiveData.value != null
            && homeViewModel.getLogsLiveData.value!!.data != null
            && homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs.isNotEmpty()) {
            return try {
                val userLogs = homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs
                userLogs
            } catch (e: Exception) {
                null
            }
        } else return null
    }

    private fun startTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer?.cancel()
        }

        mCountDownTimer = object : CountDownTimer(timeInMiliSeconds, 30000) {
            override fun onTick(millisUntilFinished: Long) {
                if (getUserLogs() != null) {
                    try {
                        val userLogs = homeViewModel.getLogsLiveData.value!!.data!!.results.userLogs
                        val violation = AlertCalculationUtils.calculate(userLogs)
                        val onDutyHours = violation.first
                        val onDriveHours = violation.second
                         // DUTY PER DAY ----> 18 Minutes
                        // DRIVING PER DAY -----> 12 Minutes
                        if (onDutyHours <= DUTY_PER_DAY && onDriveHours > DRIVING_PER_DAY) {
                            Log.d(TAG, "11 hours violation happened")
                            alertAnimation(binding.imgperday)
                        }

                        if (onDutyHours > DUTY_PER_DAY || onDriveHours > DRIVING_PER_DAY) {
                            Log.d(TAG, "driver crossing on day on duty limit")
                            Log.d(TAG, "onDuty hours : $onDutyHours : $onDriveHours")
                            alertAnimation(binding.imgBreak)
                        }

                        val conDriving = AlertCalculationUtils.continuousDriving(userLogs)
                        Log.d(TAG, "continuous driving ----> $conDriving")

                        if (conDriving >= allowedContinousDrivingLimit) {
                            Log.d(TAG, "continuous driving alert driver crossing 8 hours limit")
                            alertAnimation(binding.imgContinousDriving)
                        }


                        Log.d(TAG, "Calculation total on duty hours $onDutyHours")
                        Log.d(TAG, "Calculation total drive  hours $onDriveHours")
                        updateTimeBar(userLogs)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFinish() {
                Log.d(TAG, "time ended")
            }
        }
        (mCountDownTimer as CountDownTimer).start()
    }

    private fun setDefaultButton(currentMode: String) {
        if (currentMode == "inital") {
            isNeedToconnect = false
            binding.tvOff.performClick()
            return
        }

        val speed = (activity as Dashboard).gSpeed.toInt() > 2

        if (currentMode != TRUCK_MODE_OFF && speed) {
            Log.d(TAG, "default truck is running & current mode is off")
            isNeedToconnect = false
            binding.tvD.performClick()
        }

        if (currentMode == TRUCK_MODE_OFF) {
            isNeedToconnect = false
            binding.tvOff.performClick()
        } else if (currentMode == TRUCK_MODE_DRIVING) {
            isNeedToconnect = false
            binding.tvD.performClick()
        } else if (currentMode == TRUCK_MODE_ON) {
            isNeedToconnect = false
            binding.tvOn.performClick()
        } else if (currentMode == "personal") {
            isNeedToconnect = false
            binding.tvPerosnaluse.performClick()
        } else if (currentMode == "yard") {
            isNeedToconnect = false
            binding.tvYarduse.performClick()
        } else if (currentMode == TRUCK_MODE_SLEEPING) {
            isNeedToconnect = false
            binding.tvSb.performClick()
        }
    }

    private fun updateUI() {
        val numberText = prefRepository.getShippingNumber()
        binding.shippingNumber.text = getString(shipping_number).plus(numberText)

        val trailerText = prefRepository.getTrailerNumber()
        binding.trailerNumber.text = getString(trailer_number).plus(trailerText)
    }


    private fun showPopup(sPopupData: SPopupData.Data) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialogView: View = layoutInflater.inflate(alert_driving, null)

        val drawable_icon = dialogView.findViewById<ImageView>(R.id.drawable_icon)
        drawable_icon.setImageResource(sPopupData.drawable)

        val alert_tittle = dialogView.findViewById<TextView>(R.id.alert_tittle)
        alert_tittle.text = sPopupData.tittle

        val alert_message = dialogView.findViewById<TextView>(R.id.alert_message)
        alert_message.text = sPopupData.message

        val tv_maxhours: TextView = dialogView.findViewById(R.id.tv_maxhours)
        val tv_spenthours: TextView = dialogView.findViewById(R.id.tv_spenthours)
        val tv_hoursleft: TextView = dialogView.findViewById(R.id.tv_hoursleft)
        val tv_violationhours: TextView = dialogView.findViewById(R.id.tv_violationhours)

        tv_maxhours.text = decimalHours(sPopupData.maxHours).toString()
        tv_spenthours.text = decimalHours(sPopupData.hoursSpent).toString()
        tv_hoursleft.text = decimalHours(sPopupData.hoursLeft).toString()
        tv_violationhours.text = decimalHours(sPopupData.violationHour).toString()

        builder.setView(dialogView).create().show()

    }

    private fun editShippingNumberPopup() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialogView: View = layoutInflater.inflate(edit_shiping_number, null)

        val dialog = builder.create()

        dialog.setView(dialogView)
        val shippingNumber: EditText = dialogView.findViewById(R.id.texture_shipping_number)

        shippingNumber.setText(prefRepository.getShippingNumber())

        dialogView.findViewById<Button>(R.id.cancel_shipping).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.update_shipping).setOnClickListener {
            val number = shippingNumber.text.toString()

            if (number.isEmpty()) {
                makeText(activity, "Shipping number required", LENGTH_LONG).show()
                return@setOnClickListener
            }
             dialog.dismiss()

            prefRepository.setShippingNumber(number)

            updateUI()

        }
        dialog.show()
    }

    private fun editTrailerNumberPopup() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialogView: View = layoutInflater.inflate(edit_trailer_number, null)

        val trailerNumber: EditText = dialogView.findViewById(R.id.trailer_number)
        trailerNumber.setText(prefRepository.getTrailerNumber())

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.action_cancel_trailer).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.action_update_trailer).setOnClickListener {
            val number = trailerNumber.text.toString()
            if (number.isEmpty()) {
                makeText(activity, "Trailer number required", LENGTH_LONG).show()
                return@setOnClickListener
            }

            dialog.dismiss()
            prefRepository.setTrailerNumber(number)
            updateUI()

        }


        dialog.setView(dialogView)
        dialog.show()

    }

    private fun editCoDriverPopup() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialogView: View = layoutInflater.inflate(edit_codriver, null)

        val dialog = builder.create()
        dialog.setView(dialogView)

        val items = arrayOf("No Co-Driver")

        val coDriverSpinner: AppCompatSpinner = dialogView.findViewById(R.id.spinner_co_driver)


        val adapter = ArrayAdapter(
            requireActivity().applicationContext, android.R.layout.simple_spinner_item, items
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        coDriverSpinner.adapter = adapter

        // Set a listener to handle item selection
        coDriverSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItem = items[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing when nothing is selected
            }
        }

        dialogView.findViewById<Button>(R.id.cancel_codriver).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.action_update_codriver).setOnClickListener {
            dialog.dismiss()
        }



        dialog.show()

    }

}