//
//  TimerPresenter.swift
//  BlueTriangleTest
//
//  Created by Jeremy Greenwood on 7/26/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

import Foundation
import BlueTriangle

final class TimerPresenter: Presenter {
    private let timer: BTTimer
    lazy private var settings: [SettingInfo] = {
        return [SettingInfo(name: "Page Name", value: self.timer.allFields()[kPageName] as? String),
                SettingInfo(name: "Traffic Segment", value: self.timer.allFields()[kTrafficSegmentName] as? String),
                SettingInfo(name: "AB Test ID", value: self.timer.allFields()[kABTestID] as? String),
                SettingInfo(name: "Content Group Name", value: self.timer.allFields()[kContentGroupName] as? String),
                SettingInfo(name: "Brand Value", value: self.timer.allFields()[kBrandValue] as? String),
                SettingInfo(name: "Cart Value", value: self.timer.allFields()[kCartValue] as? String),
                SettingInfo(name: "Order Number", value: self.timer.allFields()[kOrderNumber] as? String),
                SettingInfo(name: "Order Time", value: self.timer.allFields()[kOrderTime] as? String),
                SettingInfo(name: "Campaign Name", value: self.timer.allFields()[kCampaignName] as? String),
                SettingInfo(name: "Campaign Source", value: self.timer.allFields()[kCampaignSource] as? String),
                SettingInfo(name: "Campaign Medium", value: self.timer.allFields()[kCampaignMedium] as? String),
                SettingInfo(name: "Time On Page", value: self.timer.allFields()[kTimeOnPage] as? String),
                SettingInfo(name: "URL", value: self.timer.allFields()[kURL] as? String),
                SettingInfo(name: "Referrer", value: self.timer.allFields()[kReferrerURL] as? String)]
    }()

    var rowCount: Int {
        return settings.count
    }

    init(timer: BTTimer) {
        self.timer = timer
    }

    func settingInfo(index: Int) -> SettingInfo? {
        guard settings.count > index else {
            return nil
        }

        return settings[index]
    }

    func updateSettingInfoValue(_ value: String, index: Int) {
        settings[index].setValue(value)

        switch index {
        case 0:
            timer.setPageName(value)
        case 1:
            timer.setTrafficSegmentName(value)
        case 2:
            timer.setABTestIdentifier(value)
        case 3:
            timer.setContentGroupName(value)
        case 4:
            guard let doubleValue = Double(value) else { return }
            timer.setBrandValue(doubleValue)
        case 5:
            guard let doubleValue = Double(value) else { return }
            timer.setCartValue(doubleValue)
        case 6:
            timer.setOrderNumber(value)
        case 7:
            guard let intValue = Int(value) else { return }
            timer.setOrderTime(intValue)
        case 8:
            timer.setCampaignName(value)
        case 9:
            timer.setCampaignSource(value)
        case 10:
            timer.setCampaignMedium(value)
        case 11:
            guard let intValue = Int(value) else { return }
            timer.setTimeOnPage(intValue)
        case 12:
            timer.setURL(value)
        case 13:
            timer.setReferrer(value)
        default:
            break
        }
    }
}
