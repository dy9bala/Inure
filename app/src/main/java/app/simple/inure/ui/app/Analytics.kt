package app.simple.inure.ui.app

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import app.simple.inure.R
import app.simple.inure.decorations.views.TypeFaceTextView
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.util.FileSizeHelper.getFileSize
import app.simple.inure.util.SDKHelper
import app.simple.inure.viewmodels.AppsAnalyticsData
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Analytics : ScopedFragment() {

    private lateinit var osVersion: TypeFaceTextView
    private lateinit var securityUpdate: TypeFaceTextView
    private lateinit var root: TypeFaceTextView
    private lateinit var busybox: TypeFaceTextView
    private lateinit var availableRam: TypeFaceTextView
    private lateinit var usedRam: TypeFaceTextView
    private lateinit var totalUserApps: TypeFaceTextView
    private lateinit var totalSystemApps: TypeFaceTextView

    private lateinit var ramIndicator: ProgressBar
    private lateinit var totalUserAppsIndicator: ProgressBar
    private lateinit var totalSystemAppsIndicator: ProgressBar

    private val model: AppsAnalyticsData by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_analytics, container, false)

        osVersion = view.findViewById(R.id.analytics_os_version)
        securityUpdate = view.findViewById(R.id.analytics_security_update)
        root = view.findViewById(R.id.analytics_root)
        busybox = view.findViewById(R.id.analytics_busybox)
        availableRam = view.findViewById(R.id.analytics_total_ram)
        usedRam = view.findViewById(R.id.analytics_total_used)
        totalUserApps = view.findViewById(R.id.analytics_total_user_apps)
        totalSystemApps = view.findViewById(R.id.analytics_total_system_apps)

        ramIndicator = view.findViewById(R.id.analytics_ram_progress_bar)
        totalUserAppsIndicator = view.findViewById(R.id.analytics_user_apps_progress_bar)
        totalSystemAppsIndicator = view.findViewById(R.id.analytics_system_apps_progress_bar)

        startPostponedEnterTransition()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDeviceAnalytics()
        setRamAnalytics()
        setAppsAnalytics()
    }

    private fun setAppsAnalytics() {
        model.getAppData().observe(requireActivity(), {
            launch {
                var totalApp: Int
                var userApp = 0
                var systemApp = 0

                withContext(Dispatchers.Default) {
                    for (i in it.indices) {
                        if ((it[i].flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                            systemApp += 1
                        }

                        if ((it[i].flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                            userApp += 1
                        }
                    }

                    totalApp = it.size
                }

                totalUserApps.text = StringBuilder().append(userApp).append("/").append(totalApp)
                totalSystemApps.text = StringBuilder().append(systemApp).append("/").append(totalApp)

                totalSystemAppsIndicator.max = totalApp
                totalSystemAppsIndicator.progress = systemApp
                totalUserAppsIndicator.max = totalApp
                totalUserAppsIndicator.progress = userApp
            }
        })
    }

    private fun setDeviceAnalytics() {
        launch {
            val osVersion: String
            val securityUpdate: String
            val root: String
            val busyBox: String

            withContext(Dispatchers.Default) {
                osVersion = SDKHelper.getSdkTitle(Build.VERSION.SDK_INT)
                securityUpdate = Build.VERSION.SECURITY_PATCH
                root = RootBeer(requireContext()).checkSuExists().toString()
                busyBox = RootBeer(requireContext()).checkForBusyBoxBinary().toString()
            }

            this@Analytics.osVersion.text = osVersion
            this@Analytics.securityUpdate.text = securityUpdate
            this@Analytics.root.text = root
            this@Analytics.busybox.text = busyBox
        }
    }

    private fun setRamAnalytics() {
        launch {
            val available: Long
            val used: Long

            withContext(Dispatchers.Default) {
                val mi = ActivityManager.MemoryInfo()
                val activityManager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                activityManager.getMemoryInfo(mi)

                available = mi.totalMem
                used = mi.totalMem - mi.availMem
            }

            ramIndicator.max = (available / 1000).toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ramIndicator.setProgress((used / 1000).toInt(), true)
            } else {
                ramIndicator.progress = (used / 1000).toInt()
            }

            availableRam.text = available.getFileSize()
            usedRam.text = used.getFileSize()
        }
    }

    override fun onResume() {
        super.onResume()
        setRamAnalytics()
    }

    companion object {
        fun newInstance(): Analytics {
            val args = Bundle()
            val fragment = Analytics()
            fragment.arguments = args
            return fragment
        }
    }
}
