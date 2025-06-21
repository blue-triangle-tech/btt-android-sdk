package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen

internal class BTTTimerGroupManager(val groupDecayInSeconds: ()-> Int) {

    private val activeGroups = mutableListOf<BTTTimerGroup>()

    fun add(screen: Screen, timer: Timer) {
        val lastActive = activeGroups.lastOrNull { !it.isClosed }

        if(lastActive != null) {
            lastActive.add(screen, timer)
        } else {
            val toBeRemoved = mutableListOf<BTTTimerGroup>()
            activeGroups.forEach {
                it.submit()
                it.flush()
                toBeRemoved.add(it)
            }
            activeGroups.removeAll(toBeRemoved)
            val newGroup = BTTTimerGroup(groupDecayInSecs = groupDecayInSeconds(), onCompleted = this::onGroupCompleted)
            newGroup.add(screen, timer)
            activeGroups.add(newGroup)
        }
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