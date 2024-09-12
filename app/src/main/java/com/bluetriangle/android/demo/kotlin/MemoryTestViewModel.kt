package com.bluetriangle.android.demo.kotlin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluetriangle.analytics.Timer
import com.bluetriangle.android.demo.tests.MemoryHeapTest

class MemoryTestViewModel : ViewModel() {

    private var timer: Timer? = null
    var isTimerStarted = MutableLiveData(false)

    fun onTimerButtonClick() {
        val isStarted = isTimerStarted.value ?: return
        isTimerStarted.value = !isStarted
        if (isStarted) {
            timer?.submit()
        } else {
            timer = Timer()
            timer?.setPageName("Memory Usage")
            timer?.start()
        }
    }

    fun onAllocateHeapMemory() {
        Thread {
            MemoryHeapTest(120).run()
        }.start()
    }

    fun onAllocateStackMemory() {
        Thread {
            MemoryHeapTest(30).run()
        }.start()
    }

    class MemoryBlock {
        private val memoryBlock = ByteArray((Runtime.getRuntime().maxMemory() * 0.2).toInt())

        init {
            Log.d("MemoryBlock", "Used: ${memoryBlock.size}")
        }
    }

    private var memoryBlock: ArrayList<MemoryBlock>? = null

    fun useMemory() {
        if(memoryBlock == null) {
            memoryBlock = arrayListOf()
        }
        memoryBlock?.add(MemoryBlock())
    }

    fun clearMemory() {
        memoryBlock?.clear()
        memoryBlock = null
        Runtime.getRuntime().gc()
    }

}