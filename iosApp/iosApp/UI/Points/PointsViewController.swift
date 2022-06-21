//
//  MainViewController.swift
//  iosApp
//
//  Created by Marcos Vitureira on 25/01/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import shared
import GoogleMaps
import GoogleMapsUtils

class PointsViewController: UIViewController, GMSMapViewDelegate {
    
    // DELEGATES
    weak var delegate: MainViewControllerDelegate!
    
    // VIEWS
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var loadingView: UIView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    // VIEW MODEL
    private var pointsViewModel: PointsViewModel!
    
    // GOOGLE MAPS
    private var mapView: GMSMapView!
    private var clusterManager: GMUClusterManager!
    private var locationManager: CLLocationManager!
    
    // MODEL
    private var points: [Points] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setMap()
        setViewModel()
    }
    
    // GOOGLE MAPS FUNCTIONS
    func setMap() {
        mapView = GMSMapView()
        locationManager = CLLocationManager()
        setLocationManager(locationManager, delegate: self)
        createMap(on: mainView, locationManager: locationManager, map: mapView)
        setMapStyle()
    }
    
    func setLocationManager(_ locationManager: CLLocationManager, delegate: UIViewController) {
        var locationManager =  locationManager
        locationManager = CLLocationManager()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        locationManager.distanceFilter = 50
        locationManager.startUpdatingLocation()
        locationManager.delegate = delegate as? CLLocationManagerDelegate
    }
    
    func createMap(on view: UIView, locationManager: CLLocationManager, map: GMSMapView) {
        let zoomLevel: Float = 12.0
        let location = CLLocation(latitude: -34.615662, longitude: -58.503338)
        let camera = GMSCameraPosition.camera(withLatitude: location.coordinate.latitude,
                                              longitude: location.coordinate.longitude,
                                              zoom: zoomLevel)
        
        mapView = GMSMapView.map(withFrame: view.bounds, camera: camera)
        mapView.settings.myLocationButton = true
        mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        mapView.isMyLocationEnabled = true
        view.addSubview(mapView)
    }
    
    func setMapStyle() {
        do {
            if let styleURL = Bundle.main.url(forResource: "Map Style", withExtension: "json") {
                mapView.mapStyle = try GMSMapStyle(contentsOfFileURL: styleURL)
            } else {
                NSLog("Unable to find style.json")
            }
        } catch {
            NSLog("One or more of the map styles failed to load. \(error)")
        }
    }
    
    func didUpdateLocations(_ locations: [CLLocation], locationManager: CLLocationManager, mapView: GMSMapView) {
        let zoomLevel: Float = 13.0
        let location: CLLocation = locations.last!
        let camera = GMSCameraPosition.camera(withLatitude: location.coordinate.latitude,
                                              longitude: location.coordinate.longitude,
                                              zoom: zoomLevel)
        mapView.camera = camera
    }
    
    func handle(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        // Check accuracy authorization
        let accuracy = manager.accuracyAuthorization
        switch accuracy {
        case .fullAccuracy:
            print("Location accuracy is precise.")
        case .reducedAccuracy:
            print("Location accuracy is not precise.")
        @unknown default:
            fatalError()
        }
        
        // Handle authorization status
        switch status {
        case .restricted:
            print("Location access was restricted.")
        case .denied:
            print("User denied access to location.")
            // Display the map using the default location.
        case .notDetermined:
            print("Location status not determined.")
        case .authorizedAlways: fallthrough
        case .authorizedWhenInUse:
            print("Location status is OK.")
        @unknown default:
            fatalError()
        }
    }
    
    func setCluster() {
        let iconGenerator = GMUDefaultClusterIconGenerator()
        let algorithm = GMUNonHierarchicalDistanceBasedAlgorithm()
        let renderer = GMUDefaultClusterRenderer(mapView: mapView,
                                    clusterIconGenerator: iconGenerator)
        clusterManager = GMUClusterManager(map: mapView, algorithm: algorithm,
                                                          renderer: renderer)
        clusterManager.setMapDelegate(self)
        
        for point in points {
            let position = CLLocationCoordinate2D(latitude: Double(point.latitude)!, longitude: Double(point.longitude)!)
            let marker = GMSMarker(position: position)
            marker.title = point.name
            marker.icon = UIGraphicsImageRenderer(size: .init(width: 60.0, height: 60.0)).image { context in
                UIImage(named: "pin")!.draw(in: .init(origin: .zero, size: context.format.bounds.size))
            }
            clusterManager.add(marker)
        }
        
        clusterManager.cluster()
    }
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        if (marker.title != nil) {
            for point in points {
                if (point.name == marker.title) {
                    let storyboard = UIStoryboard(name: "MainStoryboard", bundle: nil)
                    let vc = storyboard.instantiateViewController(identifier: "PointsDetailViewController") as PointsDetailViewController
                    vc.point = point
                    vc.delegate = delegate
                    delegate.bindToViewController(target_viewcontroller: vc)
                }
            }
            return true
        } else {
            return false
        }
    }
    
    // SET VIEW MODEL
    func setViewModel() {
        pointsViewModel = PointsViewModel()
        observeViewModel()
        pointsViewModel.getPoints()
    }
    
    func observeViewModel() {
        pointsViewModel.pointsLiveData.addObserver { state in
            switch (state) {
            case is LoadingPointsState:
                self.showSpinner()
                break
            case is SuccessPointsState:
                let successState = state as! SuccessPointsState
                let response = (successState.response as! ResponseSuccess)
                let value = response.data as! [Points]
                self.points = value
                self.setCluster()
                self.hideSpinner()
                break
            case is ErrorPointsState:
                self.hideSpinner()
                NSLog("ERROR")
                break
            default:
                break
            }
        }
    }

    deinit {
        pointsViewModel.onCleared()
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

// DELEGATES TO HANDLE LOCATION MANAGER
extension PointsViewController: CLLocationManagerDelegate {
    
    // INCOMING LOCATION EVENTS
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        didUpdateLocations(locations, locationManager: locationManager, mapView: mapView)
    }
    
    // AUTHORIZATION FOR LOCATION MANAGER
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
       handle(manager, didChangeAuthorization: status)
    }
    
    // LOCATION MANAGER ERROR
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        locationManager.stopUpdatingLocation()
        print("Error: \(error)")
    }
}
