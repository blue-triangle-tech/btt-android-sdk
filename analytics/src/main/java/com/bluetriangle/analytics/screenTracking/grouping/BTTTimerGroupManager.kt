package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen

internal class BTTTimerGroupManager(var groupIdleTime: Int) {

    private val activeGroups = mutableListOf<BTTTimerGroup>()
    private val shouldRegisterTap = true

    fun add(screen: Screen, timer: Timer) {
        val lastActive = getLastActiveGroup()
        val lastUserAction = Tracker.instance?.lastTouchEventTimestamp?:0L

        if(lastActive == null || (shouldRegisterTap && lastUserAction > lastActive.startTime)) {
            createNewGroupAndAdd(screen, timer)
        } else {
            lastActive.add(screen, timer)
        }
    }

    private fun getLastActiveGroup(): BTTTimerGroup? {
        return activeGroups.lastOrNull { !it.isClosed }
    }

    private fun createNewGroup(): BTTTimerGroup {
        submitAllExistingTimers()

        val newGroup = BTTTimerGroup(groupIdleTime = groupIdleTime, onCompleted = this::onGroupCompleted)
        addToActiveGroup(newGroup)
        return newGroup
    }

    private fun createNewGroupAndAdd(
        screen: Screen,
        timer: Timer
    ) {
        createNewGroup().apply {
            add(screen, timer)
        }
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

    fun setGroupName(groupName: String) {
        activeGroups.lastOrNull { !it.isSubmitted }?.setGroupName(groupName)
    }

    fun onGroupCompleted(group: BTTTimerGroup) {
        group.submit()
        group.flush()
        activeGroups.remove(group)
    }

    fun setNewGroup(groupName: String) {
        createNewGroup().setManualGroupName(groupName)
    }

    fun destroy() {
        activeGroups.forEach {
            it.flush()
            it.end()
        }
        activeGroups.clear()
    }
}