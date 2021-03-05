//
//  RootTableViewController.swift
//  BlueTriangleTest
//
//  Created by Jeremy Greenwood on 7/26/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

import UIKit
import BlueTriangle

class RootTableViewController: UITableViewController {
    enum Rows: Int {
        case timer
        case gobal
    }

    lazy private var timer = self.vendTimer()
    lazy private var tracker = self.vendTracker()
    private var delay: Double?

    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")

        self.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Clear", style: .plain, target: self, action: #selector(clear))
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Submit", style: .plain, target: self, action: #selector(submitAction))
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 2
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)

        switch indexPath.row {
        case Rows.timer.rawValue:
            cell.textLabel?.text = "Timer Settings"
        case Rows.gobal.rawValue:
            cell.textLabel?.text = "Global Settings"
        default:
            break
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        var presenter: Presenter {
            switch indexPath.row {
            case Rows.timer.rawValue:
                return TimerPresenter(timer: timer)
            case Rows.gobal.rawValue:
                return TrackerPresenter(tracker: tracker)
            default:
                fatalError("Invalid table cell selection")
            }
        }

        let viewController = SettingTableViewController(presenter: presenter)
        navigationController?.pushViewController(viewController, animated: true)
    }
}

private extension RootTableViewController {
    func vendTimer() -> BTTimer {
        return BTTimer(pageName: "ios-test-page", trafficSegment: "ios-test-traffic-segment")
    }

    func vendTracker() -> BTTracker {
        return BTTracker.shared()
    }

    func submitTimer() {
        guard let delay = delay else {
            return
        }

        timer.start()
        DispatchQueue.global().asyncAfter(deadline: .now() + delay, qos: DispatchQoS.utility) {
            self.tracker.submitTimer(self.timer)
        }
    }

    @objc
    func submitAction() {
        if timer.hasEnded {
            let alertViewController = UIAlertController(title: "Notive", message: "Timer has already ended and has been submitted. Tap 'Clear' to create a new timer.", preferredStyle: .alert)
            alertViewController.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
            present(alertViewController, animated: true, completion: nil)
        } else {
            let alertViewController = UIAlertController(title: "Enter Timer Duration", message: "Enter the duration of the timer in seconds", preferredStyle: .alert)
            alertViewController.addTextField { [weak self] textField in
                guard let sself = self else {
                    return
                }

                textField.keyboardType = .decimalPad
                textField.addTarget(sself, action: #selector(sself.textFieldDidChange(_:)), for: .editingChanged)
            }

            alertViewController.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
            alertViewController.addAction(UIAlertAction(title: "Submit", style: .default, handler: { [weak self] _ in
                guard let sself = self else {
                    return
                }

                sself.submitTimer()
            }))

            present(alertViewController, animated: true, completion: nil)
        }
    }

    @objc
    func clear() {
        timer = vendTimer()
        tracker = vendTracker()
    }

    @objc
    func textFieldDidChange(_ textField: UITextField) {
        guard let text = textField.text else {
            return
        }

        delay = Double(text)
    }
}
