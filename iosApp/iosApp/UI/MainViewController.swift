//
//  MainViewController.swift
//  iosApp
//
//  Created by Marcos Vitureira on 31/01/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import shared

protocol MainViewControllerDelegate: AnyObject {
    func bindToViewController(target_viewcontroller: UIViewController)
    func showBackButton()
}

class MainViewController: UIViewController, MainViewControllerDelegate {
    
    // VIEWS
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var contactButton: UIButton!
    @IBOutlet weak var historyButton: UIButton!
    @IBOutlet weak var containerView: UIView!

    // CURRENT VIEW CONTROLLER
    private var currentViewController: UIViewController!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setPointsViewController()
        setButtonsEvents()
    }
    
    func setPointsViewController() {
        hideBackButton()
        let storyboard = UIStoryboard(name: "MainStoryboard", bundle: nil)
        let vc = storyboard.instantiateViewController(identifier: "PointsViewController") as PointsViewController
        vc.delegate = self
        bindToViewController(target_viewcontroller: vc)
    }
    
    // MANAGE CONTAINER VIEW
    func bindToViewController(target_viewcontroller: UIViewController) {
        if (currentViewController != nil) {
            removeViewController()
        }
       
        UIView.transition(with: containerView, duration: 0.25, options: .transitionCrossDissolve, animations: {
            self.addChild(target_viewcontroller)
            target_viewcontroller.view.frame = self.containerView.frame
            self.containerView.addSubview(target_viewcontroller.view)
            self.currentViewController = target_viewcontroller
        }, completion: nil)
    }
    
    func removeViewController() {
        currentViewController.view?.removeFromSuperview()
        currentViewController.removeFromParent()
    }
    
    // MANAGE BUTTONS EVENTS
    func setButtonsEvents() {
        contactButton.setTitle("", for: UIControl.State.normal)
        contactButton.addTarget(self, action: #selector(didButtonContactClick), for: .touchUpInside)
        historyButton.setTitle("", for: UIControl.State.normal)
        historyButton.addTarget(self, action: #selector(didButtonHistoryClick), for: .touchUpInside)
        backButton.setTitle("", for: UIControl.State.normal)
        backButton.addTarget(self, action: #selector(didBackButtonClick), for: .touchUpInside)
    }
    
    @objc func didButtonContactClick(_ sender: UIButton) {
        showBackButton()
        let storyboard = UIStoryboard(name: "MainStoryboard", bundle: nil)
        let vc = storyboard.instantiateViewController(identifier: "ContactViewController")
        bindToViewController(target_viewcontroller: vc)
    }
    
    @objc func didButtonHistoryClick(_ sender: UIButton) {
        showBackButton()
        let storyboard = UIStoryboard(name: "MainStoryboard", bundle: nil)
        let vc = storyboard.instantiateViewController(identifier: "HistoryViewController")
        bindToViewController(target_viewcontroller: vc)
    }
    
    @objc func didBackButtonClick(_ sender: UIButton) {
        setPointsViewController()
    }
    
    func showBackButton() {
        UIView.transition(with: backButton, duration: 0.25, options: .transitionCrossDissolve, animations: {
            self.backButton.isHidden = false
        })
        UIView.transition(with: contactButton, duration: 0.25, options: .transitionCrossDissolve, animations: {
            self.contactButton.isHidden = true
        })
        UIView.transition(with: historyButton, duration: 0.25, options: .transitionCrossDissolve, animations: {
            self.historyButton.isHidden = true
        })
    }
    
    func hideBackButton() {
        UIView.transition(with: backButton, duration: 0.25, options: .transitionCrossDissolve, animations: {
            self.backButton.isHidden = true
        })
        UIView.transition(with: contactButton, duration: 0.25, options: .transitionCrossDissolve, animations: {
            self.contactButton.isHidden = false
        })
        UIView.transition(with: historyButton, duration: 0.25, options: .transitionCrossDissolve, animations: {
            self.historyButton.isHidden = false
        })
    }
}
