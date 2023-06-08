package com.bluetriangle.android.demo.kotlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluetriangle.android.demo.tests.ANRTest
import com.bluetriangle.android.demo.tests.ANRTestScenario

class TestListViewModel : ViewModel() {
    val anrTest: MutableLiveData<ANRTest> = MutableLiveData<ANRTest>(ANRTest.HeavyLoopTest)
    val anrTestScenario: MutableLiveData<ANRTestScenario> =
        MutableLiveData<ANRTestScenario>(ANRTestScenario.OnActivityCreate)

    fun onTestChange(pos: Int, id: Long) {
        anrTest.postValue(ANRTest.values()[pos])
    }

    fun onTestScenarioChange(pos: Int, id: Long) {
        anrTestScenario.postValue(ANRTestScenario.values()[pos])
    }
}