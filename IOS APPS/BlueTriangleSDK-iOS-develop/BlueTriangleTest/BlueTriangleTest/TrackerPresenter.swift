//
//  TrackerPresenter.swift
//  BlueTriangleTest
//
//  Created by Jeremy Greenwood on 7/26/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

import Foundation
import BlueTriangle

final class TrackerPresenter: Presenter {
    private let tracker: BTTracker
    lazy private var settings: [SettingInfo] = {
        return [SettingInfo(name: "Site ID", value: self.tracker.allGlobalFields()[kSiteID] as? String),
                SettingInfo(name: "Session ID", value: self.tracker.allGlobalFields()[kSessionID] as? String),
                SettingInfo(name: "Global User ID", value: self.tracker.allGlobalFields()[kGlobalUserID] as? String)]
    }()

    var rowCount: Int {
        return settings.count
    }

    init(tracker: BTTracker) {
        self.tracker = tracker
    }

    func settingInfo(index: Int) -> SettingInfo? {
        guard settings.count > index else {
            return nil
        }

        return settings[index]
    }

    func updateSettingInfoValue(_ value: String, index: Int) {
        guard settings.count > index else {
            return
        }

        settings[index].setValue(value)

        switch index {
        case 0:
            tracker.setSiteID(value)
        case 1:
            tracker.setSessionID(value)
        case 2:
            tracker.setGlobalUserID(value)
        default:
            break
        }
    }
}
