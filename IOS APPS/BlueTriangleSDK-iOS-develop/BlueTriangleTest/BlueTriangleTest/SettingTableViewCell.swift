//
//  SettingTableViewCell.swift
//  BlueTriangleTest
//
//  Created by Jeremy Greenwood on 7/26/18.
//  Copyright © 2018 Blue Triangle. All rights reserved.
//

import UIKit

protocol SettingCellDelegate: class {
    func settingCell(_ cell: SettingTableViewCell, didUpdate value: String)
    func settingCellDidSelectNext(_ cell: SettingTableViewCell)
}

class SettingTableViewCell: UITableViewCell {
    let label = UILabel(frame: .zero)
    let textField = UITextField(frame: .zero)

    weak var delegate: SettingCellDelegate?

    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        label.textAlignment = .left
        textField.font = UIFont.systemFont(ofSize: UIFont.smallSystemFontSize)
        textField.textColor = .gray
        textField.delegate = self
        textField.placeholder = "Not Configured"
        textField.addTarget(self, action: #selector(textFieldValueDidChange(_:)), for: .editingChanged)

        let stackView = UIStackView(arrangedSubviews: [label, textField])
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.axis = .vertical
        stackView.spacing = UIStackView.spacingUseSystem
        contentView.addSubview(stackView)

        NSLayoutConstraint.activate(NSLayoutConstraint.constraints(withVisualFormat: "H:|-[stackView]-|", options: .directionLeadingToTrailing, metrics: nil, views: ["stackView": stackView]))
        NSLayoutConstraint.activate(NSLayoutConstraint.constraints(withVisualFormat: "V:|-[stackView]-|", options: .directionLeadingToTrailing, metrics: nil, views: ["stackView": stackView]))
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func set(title: String, value: String?) {
        label.text = title
        textField.text = value

        label.invalidateIntrinsicContentSize()
        textField.invalidateIntrinsicContentSize()
    }

    override func becomeFirstResponder() -> Bool {
        textField.becomeFirstResponder()

        return true
    }

    override func resignFirstResponder() -> Bool {
        textField.resignFirstResponder()

        return true
    }
}

private extension SettingTableViewCell {
    @objc
    func textFieldValueDidChange(_ textField: UITextField) {
        guard let text = textField.text else {
            return
        }

        delegate?.settingCell(self, didUpdate: text)
    }
}

extension SettingTableViewCell: UITextFieldDelegate {
    public func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        delegate?.settingCellDidSelectNext(self)

        return true
    }
}
