package com.nevmem.helvarapp

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nevmem.helvar.network.BackendNetworkService
import com.nevmem.helvar.network.WifiAdapter
import com.nevmem.helvarapp.activities.AddRoomActivity
import com.nevmem.helvarapp.activities.SmartModeActivity
import com.nevmem.helvarapp.data.Room
import com.nevmem.helvarapp.data.RoomRepository
import com.nevmem.helvarapp.permissions.ListenableActivity
import com.nevmem.helvarapp.permissions.Permission
import com.nevmem.helvarapp.permissions.PermissionManager
import com.nevmem.helvarapp.utils.delayedOnUI
import com.nevmem.helvarapp.utils.infiniteTry
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.room_page.view.*
import kotlinx.android.synthetic.main.start_activity.*
import javax.inject.Inject

interface PagePickerListener {
    fun prevPageCalled()
    fun nextPageCalled()
}

class StartActivity : ListenableActivity(), PagePickerListener, WifiAdapter.WifiAdapterListener {
    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var backendNetworkService: BackendNetworkService

    @Inject
    lateinit var wifiAdapter: WifiAdapter

    private var canAddRoom = false
    private var roomsCount = 0

    lateinit var pagerAdapter: PagerAdapter

    override fun connectedToHome() {
        Log.d("debug", "connectedToHome")
        delayedOnUI(100) {
            backendNetworkService.switchOnEntrance()
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        window.statusBarColor = resources.getColor(R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        (applicationContext as HelvarApp).appComponent.inject(this)

        roomRepository.allRooms
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("Debug", "Received new rooms array")
                roomsCount = it.size
                pagerAdapter = PagerAdapter(this, it, backendNetworkService)
                pager.adapter = pagerAdapter
            }

        roomRepository.currentRoom
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("debug", "currentRoomUpdated ${it.name}")
                // Toast.makeText(this, it.name, Toast.LENGTH_LONG).show()
                val index = pagerAdapter.indexOf(it)
                if (index != -1) {
                    pager.currentItem = index
                }
            }

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                checkButtons()
            }
        })

        prevButton.setOnClickListener {
            prevPageCalled()
        }

        nextButton.setOnClickListener {
            nextPageCalled()
        }

        smartMode.setOnClickListener {
            gotoSmartModePage()
        }

        addButton.setOnClickListener {
            gotoAddRoomPage()
        }

        PermissionManager(this).requestPermissions(listOf(
            Permission.INTERNET, Permission.FINE_LOCATION,
            Permission.COARSE_LOCATION, Permission.CHANGE_WIFI_STATE)) {
            if (it) {
                canAddRoom = true
            }
        }

        wifiAdapter.subscribe(this)
    }

    override fun onPause() {
        super.onPause()
        wifiAdapter.scanner.stopScanning()
    }

    override fun onResume() {
        super.onResume()
        wifiAdapter.scanner.startScanning()
    }

    private fun gotoSmartModePage() {
        startActivity(
            Intent(this, SmartModeActivity::class.java),
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun gotoAddRoomPage() {
        if (canAddRoom) {
            startActivity(
                Intent(this, AddRoomActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            Toast.makeText(this, R.string.cannotAddRoom, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkButtons() {
        val position = pager.currentItem
        prevButton.isEnabled = position != 0
        nextButton.isEnabled = position != roomsCount - 1
    }

    override fun prevPageCalled() {
        if (pager.currentItem != 0) {
            pager.currentItem = pager.currentItem - 1
        }
    }

    override fun nextPageCalled() {
        if (pager.currentItem != roomsCount - 1) {
            pager.currentItem = pager.currentItem + 1
        }
    }

    override fun onBackPressed() {
        // TODO: (handle this shit)
    }

    class PagerAdapter(
            private val ctx: Context,
            private val rooms: List<Room>,
            private val backendNetworkService: BackendNetworkService) : RecyclerView.Adapter<PagerAdapter.VH>() {

        fun indexOf(room: Room) = rooms.indexOf(room)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val inflater = LayoutInflater.from(ctx)
            val view = inflater.inflate(R.layout.room_page, parent, false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return rooms.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val room = rooms[position]
            holder.heading.text = room.name
            holder.image.setImageResource(room.imageId)
            val padding = 28
            holder.temperature.setPadding(padding, 0, padding, 0)
            holder.brightness.setPadding(padding, 0, padding, 0)
            holder.mode.text = room.modeString()
            holder.checkbox.setPadding(0, 0, 0, 0)
            holder.checkboxLabel.setOnClickListener {
                val newValue = !holder.checkbox.isChecked
                holder.checkbox.isChecked = newValue
                room.bySensor = newValue
            }
            holder.checkbox.isChecked = room.bySensor

            holder.temperature.progress = (room.temperature - 2000) * 100 / 5000
            holder.temperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(bar: SeekBar?, value: Int, byUser: Boolean) {
                    room.temperature = 2000 + 5000 * value / 100
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {
                    updateLight(room)
                }
            })

            holder.brightness.progress = room.brightness
            holder.brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(bar: SeekBar?, value: Int, byUser: Boolean) {
                    room.brightness = value
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {
                    updateLight(room)
                }
            })
        }

        fun updateLight(room: Room) {
            Log.d("debug", "${room.brightness} ${room.temperature}")
            backendNetworkService.remoteControl(room.roomId, room.brightness, room.temperature)
        }

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val heading = view.headingText
            val image = view.roomImage
            val temperature = view.temperature
            val brightness = view.brightness
            val mode = view.mode
            val checkbox = view.sensorCheckbox
            val checkboxLabel = view.checkboxLabel
        }
    }
}
