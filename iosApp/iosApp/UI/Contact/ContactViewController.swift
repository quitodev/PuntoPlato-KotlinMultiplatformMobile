//
//  ContactViewController.swift
//  iosApp
//
//  Created by Marcos Vitureira on 29/01/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import shared
import MessageUI
import Kingfisher

class ContactViewController: UIViewController, MFMailComposeViewControllerDelegate {
    
    // VIEWS
    @IBOutlet weak var instagramLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var instagramStack: UIStackView!
    @IBOutlet weak var emailStack: UIStackView!
    @IBOutlet weak var sliderCollectionView: UICollectionView!
    @IBOutlet weak var sliderPageControl: UIPageControl!
    @IBOutlet weak var loadingView: UIView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    // SLIDER TIMER
    private var timer = Timer()
    private var counter = 0
    
    // VIEW MODEL
    private var contactViewModel: ContactViewModel!
    
    // MODEL
    private var contact: [Contact] = [Contact(images: [""], instagram: "", email: "", version: "")]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setViewModel()
        setButtonsBackground()
    }
    
    // SET VIEW MODEL
    func setViewModel() {
        contactViewModel = ContactViewModel()
        observeViewModel()
        contactViewModel.getAppDetails()
    }
    
    func observeViewModel() {
        contactViewModel.appDetailsLiveData.addObserver { state in
            switch (state) {
            case is LoadingContactState:
                self.showSpinner()
                self.instagramLabel.text = "..."
                self.emailLabel.text = "..."
                break
            case is SuccessContactState:
                let successState = state as! SuccessContactState
                let response = (successState.response as! ResponseSuccess)
                self.contact = response.data as! [Contact]
                self.sliderCollectionView.reloadData()
                self.setButtonsEvents()
                self.setSliderControl()
                self.setSliderTimer()
                self.hideSpinner()
            case is ErrorContactState:
                self.hideSpinner()
                NSLog("Error")
                break
            default:
                break
            }
        }
    }
    
    deinit {
        contactViewModel.onCleared()
    }
    
    // SET BUTTONS
    func setButtonsBackground() {
        var gradientLayer = CAGradientLayer()
        gradientLayer.locations = [0.0, 1.0]
        gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
        
        var colorStart = UIColor(red: 220.0/255.0, green: 30.0/255.0, blue: 99.0/255.0, alpha: 1.0).cgColor
        var colorEnd = UIColor(red: 255.0/255.0, green: 183.0/255.0, blue: 0.0/255.0, alpha: 1.0).cgColor
                   
        gradientLayer.colors = [colorStart, colorEnd]
        gradientLayer.frame = instagramStack.bounds
        self.instagramStack.layer.insertSublayer(gradientLayer, at:0)
        
        gradientLayer = CAGradientLayer()
        gradientLayer.locations = [0.0, 1.0]
        gradientLayer.startPoint = CGPoint(x: 0.0, y: 0.5)
        gradientLayer.endPoint = CGPoint(x: 1.0, y: 0.5)
        
        colorStart = UIColor(red: 0.0/255.0, green: 104.0/255.0, blue: 186.0/255.0, alpha: 1.0).cgColor
        colorEnd = UIColor(red: 82.0/255.0, green: 201.0/255.0, blue: 255.0/255.0, alpha: 1.0).cgColor
                   
        gradientLayer.colors = [colorStart, colorEnd]
        gradientLayer.frame = emailStack.bounds
        self.emailStack.layer.insertSublayer(gradientLayer, at:0)
    }
    
    func setButtonsEvents() {
        instagramLabel.text = contact[0].instagram
        emailLabel.text = contact[0].email
        
        let tapInstagramStack = UITapGestureRecognizer(target: self, action: #selector(didInstagramStackClick(tapGestureRecognizer:)))
        instagramStack.isUserInteractionEnabled = true
        instagramStack.addGestureRecognizer(tapInstagramStack)
        
        let tapEmailStack = UITapGestureRecognizer(target: self, action: #selector(didEmailStackClick(tapGestureRecognizer:)))
        emailStack.isUserInteractionEnabled = true
        emailStack.addGestureRecognizer(tapEmailStack)
    }
    
    @objc func didInstagramStackClick(tapGestureRecognizer: UITapGestureRecognizer) {
        let user = contact[0].instagram.replacingOccurrences(of: "@", with: "")
        guard let url = URL(string: "https://instagram.com/\(user)") else { return }
        if UIApplication.shared.canOpenURL(url) {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }

    @objc func didEmailStackClick(tapGestureRecognizer: UITapGestureRecognizer) {
        NSLog("Envia email a: \(contact[0].email)")
        if MFMailComposeViewController.canSendMail() {
            let mail = MFMailComposeViewController()
            mail.mailComposeDelegate = self
            mail.setToRecipients([contact[0].email])
            present(mail, animated: true)
        } else {
            let alert = UIAlertController(title: "Error!", message: "No se pudo enviar.", preferredStyle: UIAlertController.Style.alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertAction.Style.default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true)
    }
    
    // SET SLIDER
    func setSliderControl() {
        sliderPageControl.numberOfPages = contact[0].images.count
        sliderPageControl.currentPage = 0
        sliderPageControl.isHidden = false
        sliderPageControl.isEnabled = false
    }
    
    func setSliderTimer() {
        timer = Timer.scheduledTimer(timeInterval: 4.0, target: self, selector: #selector(sliderImage), userInfo: nil, repeats: true)
    }
    
    @objc func sliderImage() {
        let index = IndexPath(item: counter, section: 0)
        if counter < contact[0].images.count {
            sliderCollectionView.scrollToItem(at: index, at: .centeredHorizontally, animated: true)
            sliderPageControl.currentPage = counter
            counter += 1
        } else {
            counter = 0
            let first = IndexPath(item: counter, section: 0)
            sliderCollectionView.scrollToItem(at: first, at: .centeredHorizontally, animated: true)
            sliderPageControl.currentPage = counter
            counter = 1
        }
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let scrollPos = scrollView.contentOffset.x / view.frame.width
        sliderPageControl.currentPage = Int(scrollPos)
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

// EXTENSIONS FOR IMAGE SLIDER
extension ContactViewController: UICollectionViewDataSource, UICollectionViewDelegate {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return contact[0].images.count
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = sliderCollectionView.dequeueReusableCell(withReuseIdentifier: "sliderCell", for: indexPath) as! SliderCollectionViewCell
        
        if (contact[0].images[0] as! String != "") {
            cell.imageView.kf.setImage(with: URL(string: contact[0].images[indexPath.row] as! String)!)
        } else {
            cell.imageView.image = UIImage(named: "contact")
        }
        
        return cell
    }
}

extension ContactViewController: UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: collectionView.frame.size.width, height: collectionView.frame.size.height)
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
}
