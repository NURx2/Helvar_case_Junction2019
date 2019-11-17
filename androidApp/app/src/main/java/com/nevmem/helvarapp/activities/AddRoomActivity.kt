package com.nevmem.helvarapp.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.nevmem.helvar.network.WifiAdapter
import com.nevmem.helvarapp.HelvarApp
import com.nevmem.helvarapp.R
import com.nevmem.helvarapp.data.Room
import com.nevmem.helvarapp.data.RoomRepository
import com.nevmem.helvarapp.permissions.ListenableActivity
import com.nevmem.helvarapp.utils.delayedOnUI
import com.nevmem.helvarapp.view.SavingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.add_room_activity.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.math.min

class AddRoomActivity : ListenableActivity() {
    companion object {
        const val maxSaveDelay = 5000L
        val roomProfileWifiAmount = 30
        val minimalProfilingCount = 5
    }

    @Inject
    lateinit var wifiAdapter: WifiAdapter

    @Inject
    lateinit var roomRepository: RoomRepository

    lateinit var listUpdater: Disposable

    private var marksReceived = 0
    private val map = HashMap<String, Int>()
    private var savingStarted = false
    private var saved = false

    private val marks = BehaviorSubject.create<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_room_activity)

        (applicationContext as HelvarApp).appComponent.inject(this)

        window.statusBarColor = resources.getColor(R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        backButton.setOnClickListener { finishAfterTransition() }

        saveButton.setOnClickListener {
            startSavingProcess()
        }
    }

    private var dialog: DialogFragment? = null

    private fun startSavingProcess() {
        saveButton.isEnabled = false
        savingStarted = true

        dialog = SavingDialog(marks).also { it.show(supportFragmentManager, "saving-started") }

        if (marksReceived >= minimalProfilingCount) {
            delayedOnUI(maxSaveDelay) {
                doSaveRoom()
            }
        }
    }

    private fun doSaveRoom() {
        if (!saved) {
            saved = true
            listUpdater.dispose()
            dialog?.dismiss()

            val roomWifiProfile = map
                .map { Pair(it.value.toDouble() / marksReceived, it.key) }
                .sortedBy { it.first }
                .reversed()
                .slice(0 .. min(roomProfileWifiAmount, map.size - 1))

            val room = Room(roomName.text.toString())
            room.temperature = 2000 + 5000 * temperature.progress / 100
            room.brightness = brightness.progress
            room.wifiProfile.addAll(roomWifiProfile.map { Pair(it.second, it.first) })

            roomRepository.addRoom(room)

            finishAfterTransition()
        }
    }

    override fun onResume() {
        super.onResume()
        wifiAdapter.scanner.startScanning()

        listUpdater = wifiAdapter.scanner.scanResults
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                marksReceived += 1
                marks.onNext(marksReceived)
                it.forEach { pair ->
                    if (map.containsKey(pair.second)) {
                        map[pair.second] = map[pair.second]!! + pair.first
                    } else {
                        map[pair.second] = pair.first
                    }
                }

                if (savingStarted && marksReceived >= minimalProfilingCount) {
                    doSaveRoom()
                }
            }
    }

    override fun onPause() {
        super.onPause()
        if (!listUpdater.isDisposed) {
            listUpdater.dispose()
        }
    }
}