package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer

class BTTTimerGroupManager(val groupDecayInSeconds: ()-> Int) {

    private val activeGroups = mutableListOf<BTTTimerGroup>()

    fun add(timer: Timer) {
        val lastActive = activeGroups.lastOrNull { !it.isClosed }

        if(lastActive != null) {
            lastActive.add(timer)
        } else {
            val newGroup = BTTTimerGroup(groupDecayInSecs = groupDecayInSeconds(), onCompleted = this::onGroupCompleted)
            newGroup.add(timer)
            activeGroups.add(newGroup)
        }
    }

    fun onGroupCompleted(group: BTTTimerGroup) {
        group.submit()
        group.flush()
        activeGroups.remove(group)
    }
}