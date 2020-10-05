//
//  SettingTableViewController.swift
//  BlueTriangleTest
//
//  Created by Jeremy Greenwood on 7/26/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

import UIKit

class SettingTableViewController: UITableViewController {
    private let presenter: Presenter

    init(presenter: Presenter) {
        self.presenter = presenter
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 40.0
        tableView.register(SettingTableViewCell.self, forCellReuseIdentifier: "Cell")
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return presenter.rowCount
    }


    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as? SettingTableViewCell else {
            fatalError("Not a SettingTableViewCell")
        }

        if let info = presenter.settingInfo(index: indexPath.row) {
            cell.set(title: info.name, value: info.value)
        }

        cell.tag = indexPath.row
        cell.delegate = self

        if indexPath.row < presenter.rowCount - 1 {
            cell.textField.returnKeyType = .next
        } else {
            cell.textField.returnKeyType = .done
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, willSelectRowAt indexPath: IndexPath) -> IndexPath? {
        return nil
    }

    override func tableView(_ tableView: UITableView, shouldHighlightRowAt indexPath: IndexPath) -> Bool {
        return false
    }
}

extension SettingTableViewController: SettingCellDelegate {
    func settingCell(_ cell: SettingTableViewCell, didUpdate value: String) {
        presenter.updateSettingInfoValue(value, index: cell.tag)
    }

    func settingCellDidSelectNext(_ cell: SettingTableViewCell) {
        guard let nextCell = tableView.cellForRow(at: IndexPath(row: cell.tag + 1, section: 0)) else {
            _ = cell.resignFirstResponder()
            return
        }

        nextCell.becomeFirstResponder()
    }
}
