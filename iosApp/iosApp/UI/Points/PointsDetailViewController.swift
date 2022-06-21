//
//  PointsDetailViewController.swift
//  iosApp
//
//  Created by Marcos Vitureira on 29/01/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import shared
import Kingfisher

class PointsDetailViewController: UIViewController {
    
    // DELEGATES
    weak var delegate: MainViewControllerDelegate!
    
    // VIEWS
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var addressLabel: UILabel!
    @IBOutlet weak var scheduleLabel: UILabel!
    @IBOutlet weak var instagramLabel: UILabel!
    @IBOutlet weak var frameView: UIView!
    @IBOutlet weak var pointImage: UIImageView!
    @IBOutlet weak var instagramStack: UIStackView!
    @IBOutlet weak var loadingView: UIView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    // MODEL
    var point: Points = Points(name: "", address: "", instagram: "", schedule: "", image: "", latitude: "", longitude: "")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        showSpinner()
        setData()
        setButtonsEvents()
        setBackgroundColor()
        setDataFrame()
        delegate.showBackButton()
    }
    
    func setData() {
        pointImage.kf.setImage(with: URL(string: point.image)) { result in
            switch result {
            case .success:
                self.hideSpinner()
            case .failure(let error):
                self.hideSpinner()
                print(error)
            }
        }
        pointImage.contentMode = .scaleToFill
        nameLabel.text = point.name
        addressLabel.text = point.address
        scheduleLabel.text = point.schedule
        instagramLabel.text = point.instagram
    }

    func setButtonsEvents() {
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(didInstagramStackClick(tapGestureRecognizer:)))
        instagramStack.isUserInteractionEnabled = true
        instagramStack.addGestureRecognizer(tapGestureRecognizer)
    }
    
    @objc func didInstagramStackClick(tapGestureRecognizer: UITapGestureRecognizer) {
        let user = point.instagram.replacingOccurrences(of: "@", with: "")
        guard let url = URL(string: "https://instagram.com/\(user)") else { return }
        if UIApplication.shared.canOpenURL(url) {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }
    
    func setBackgroundColor() {
        let gradientLayer = CAGradientLayer()
        gradientLayer.locations = [0.0, 1.0]
        
        let colorTop = UIColor(red: 79.0/255.0, green: 177.0/255.0, blue: 255.0/255.0, alpha: 1.0).cgColor
        let colorBottom = UIColor(red: 0.0/255.0, green: 79.0/255.0, blue: 142.0/255.0, alpha: 1.0).cgColor
                    
        gradientLayer.colors = [colorTop, colorBottom]
        gradientLayer.frame = self.view.bounds
        self.view.layer.insertSublayer(gradientLayer, at:0)
    }

    func setDataFrame() {
        frameView.layer.borderWidth = 3
        frameView.layer.borderColor = UIColor.white.cgColor
        frameView.layer.cornerRadius = 10
    }
    
    // MANAGE SPINNER
    func showSpinner() {
        self.activityIndicator.startAnimating()
        self.loadingView.isHidden = false
    }
    
    func hideSpinner() {
        self.activityIndicator.stopAnimating()
        self.loadingView.isHidden = true
    }
}
