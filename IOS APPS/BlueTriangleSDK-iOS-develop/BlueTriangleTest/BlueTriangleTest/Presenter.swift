//
//  Presenter.swift
//  BlueTriangleTest
//
//  Created by Jeremy Greenwood on 7/26/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

import Foundation

struct SettingInfo {
    let name: String
    var value: String?

    mutating func setValue(_ value: String) {
        self.value = value
    }
}

protocol Presenter: class {
    var rowCount: Int { get }

    func settingInfo(index: Int) -> SettingInfo?
    func updateSettingInfoValue(_ value: String, index: Int)
}
