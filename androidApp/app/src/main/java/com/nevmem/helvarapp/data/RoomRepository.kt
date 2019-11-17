package com.nevmem.helvarapp.data

import android.util.Log
import com.nevmem.helvar.network.BackendNetworkService
import com.nevmem.helvar.network.WifiAdapter
import com.nevmem.helvarapp.utils.runOnUi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class RoomRepository @Inject constructor(
        private val wifiAdapter: WifiAdapter,
        backendNetworkService: BackendNetworkService,
        private val appDatabase: AppDatabase,
        @Named("background") private val backgroundExecutor: Executor) {
    private var rooms = ArrayList<Room>()

    val currentRoom = BehaviorSubject.create<Room>()
    val allRooms = BehaviorSubject.create<List<Room>>()

    private var prevRoom = Room("none")

    init {
        backgroundExecutor.execute {
            val roomsFromDb = appDatabase.roomsDao().getAll()
            runOnUi {
                rooms.addAll(roomsFromDb)
                allRooms.onNext(rooms)
            }
        }
        Observable.combineLatest(
            wifiAdapter.scanner.scanResults,
            allRooms,
            BiFunction<List<Pair<Int, String>>, List<Room>, Pair<List<Pair<Int, String>>, List<Room>>>
                { first, second -> Pair(first, second) })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it.second
                    .minBy { room -> room.calculateProfileSimilarity(
                        it.first.map { Pair(it.second, it.first.toDouble()) }) }
                    ?.let { room -> run {
                        currentRoom.onNext(room)
                        if (prevRoom.name != room.name) {
                            backendNetworkService.move(prevRoom.roomName, room.roomName)
                        }
                        prevRoom = room
                    }}
            }

    }

    fun addRoom(room: Room) {
        val newRooms = ArrayList<Room>()
        newRooms.addAll(rooms)
        backgroundExecutor.execute {
            appDatabase.roomsDao().addRoom(room)
        }
        Log.d("debug", "Adding new room")
        newRooms.add(room)
        rooms = newRooms
        allRooms.onNext(rooms)
    }
}