package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen

internal class BTTTimerGroupManager(val groupDecayInSeconds: ()-> Int) {

    private val activeGroups = mutableListOf<BTTTimerGroup>()

    fun add(screen: Screen, timer: Timer) {
        val lastActive = getLastActiveGroup()

        if(lastActive != null) {
            lastActive.add(screen, timer)
        } else {
            createNewGroupAndAdd(screen, timer)
        }
    }

    private fun getLastActiveGroup(): BTTTimerGroup? {
        return activeGroups.lastOrNull { !it.isClosed }
    }

    private fun createNewGroupAndAdd(
        screen: Screen,
        timer: Timer
    ) {
        submitAllExistingTimers()

        val newGroup = BTTTimerGroup(groupDecayInSecs = groupDecayInSeconds(), onCompleted = this::onGroupCompleted)
        newGroup.add(screen, timer)

        addToActiveGroup(newGroup)
    }


    private fun addToActiveGroup(group: BTTTimerGroup) {
        activeGroups.add(group)
    }

    private fun submitAllExistingTimers() {
        activeGroups.forEach {
            it.submit()
            it.flush()
        }
        activeGroups.clear()
    }

    fun setScreenName(screenName: String) {
        activeGroups.lastOrNull { !it.isSubmitted }?.setScreenName(screenName)
    }

    fun onGroupCompleted(group: BTTTimerGroup) {
        group.submit()
        group.flush()
        activeGroups.remove(group)
    }
}