//
//  HistoryViewController.swift
//  iosApp
//
//  Created by Marcos Vitureira on 29/01/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import shared

class HistoryViewController: UIViewController {
    
    // VIEWS
    @IBOutlet weak var frameStackView: UIStackView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setDataFrame()
    }
    
    func setDataFrame() {
        frameStackView.layer.borderWidth = 3
        frameStackView.layer.borderColor = UIColor(named: "AccentColor")?.cgColor
        frameStackView.layer.cornerRadius = 10
    }
}
