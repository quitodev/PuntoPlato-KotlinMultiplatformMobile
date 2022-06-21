package com.servirunplatomas.android.ui.points

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.servirunplatomas.android.R
import com.servirunplatomas.android.databinding.FragmentPointsBinding
import com.servirunplatomas.android.utils.ClusterMap
import com.servirunplatomas.android.utils.ClusterMarker
import com.servirunplatomas.domain.model.points.Points
import com.servirunplatomas.presentation.points.*
import com.servirunplatomas.utils.Response
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.databinding.BR

@ExperimentalCoroutinesApi
@SuppressLint("MissingPermission")
class PointsFragment : MvvmFragment<FragmentPointsBinding, PointsViewModel>(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // OBSERVER
    private lateinit var getPointsObserver : (state: PointsState) -> Unit

    // VIEW MODEL
    override val layoutId: Int = R.layout.fragment_points
    override val viewModelVariableId: Int = BR.viewModel
    override val viewModelClass: Class<PointsViewModel> = PointsViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory {
        return createViewModelFactory { PointsViewModel() }
    }

    // GOOGLE MAPS & CURRENT LOCATION
    private var mMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null

    // LIST OF POINTS & MARKERS
    private val listPoints = mutableListOf<Points>()
    private val clusterManager by lazy { ClusterManager<ClusterMap>(context, mMap) }

    // ACTIVITY RESULT
    private lateinit var activityResult: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()
        setMapFragment()
        setActivityResult()
        observeViewModel()
    }

    // SET BUTTONS
    private fun setButtons() {
        binding.contactButton.setOnClickListener {
            findNavController().navigate(R.id.menuContactFragment)
        }
        binding.historyButton.setOnClickListener {
            findNavController().navigate(R.id.menuHistoryFragment)
        }
    }

    // SET MAP FRAGMENT
    private fun setMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // SET ACTIVITY RESULT FOR LOCATION
    private fun setActivityResult() {
        activityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            when (result[ACCESS_FINE_LOCATION]) {
                true -> getLocation()
                else -> {}
            }
        }
    }

    // OBSERVER FOR VIEW MODEL
    private fun observeViewModel() {
        getPointsObserver = { getPoints(viewModel.pointsLiveData.value) }
        viewModel.pointsLiveData.addObserver(getPointsObserver)
        viewModel.getPoints()
    }

    // GET POINTS
    private fun getPoints(state: PointsState) {
        when (state) {
            is LoadingPointsState -> showLoading()
            is SuccessPointsState -> {
                hideLoading()
                val response = state.response as Response.Success
                setPoints(response.data)
                Log.d("DATA", response.data.toString())
            }
            is ErrorPointsState -> {
                hideLoading()
                val response = state.response as Response.Error
                showError(response.message)
            }
        }
    }

    // SETUP MAP
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-34.588817, -58.450779)))
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(11f))
        mMap?.setOnCameraIdleListener(clusterManager)
        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map))
        mMap?.setOnMarkerClickListener(this)
        getLocationPermission()
    }

    // LOCATION PERMISSIONS
    private fun getLocationPermission() {
        if (checkLocationPermission()) {
            mMap?.isMyLocationEnabled = true
            locationRequest()
            locationCallback()
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
    }

    private fun checkLocationPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                activityResult.launch(arrayOf(ACCESS_FINE_LOCATION))
            } else {
                activityResult.launch(arrayOf(ACCESS_FINE_LOCATION))
            }
            false
        } else {
            true
        }
    }

    private fun getLocation() {
        if (checkLocationPermission()) {
            mMap?.isMyLocationEnabled = true
            locationRequest()
            locationCallback()
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
    }

    private fun locationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 120 * 1000
            fastestInterval = 120 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun locationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                location = locationResult.locations[locationResult.locations.size - 1]
                when {
                    locationResult.locations.isNotEmpty() -> {
                        val geopoint = location?.latitude?.let { location?.longitude?.let { it1 -> LatLng(it, it1) } }
                        mMap?.moveCamera(CameraUpdateFactory.newLatLng(geopoint!!))
                        mMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
                    }
                }
            }
        }
    }

    // SET POINTS ON MAP
    private fun setPoints(result: List<Points>) {
        binding.layoutMap.visibility = View.VISIBLE
        binding.layoutMap.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right))
        mMap?.clear()
        listPoints.clear()
        clusterManager.clearItems()
        clusterManager.renderer = ClusterMarker(requireContext(), mMap!!, clusterManager)

        val arrayList: ArrayList<ClusterMap> = ArrayList()
        result.forEach { points ->
            val point = ClusterMap(points.name, LatLng(points.latitude.toDouble(), points.longitude.toDouble()))
            arrayList.add(point)
            listPoints.add(points)
        }

        clusterManager.addItems(arrayList)
        clusterManager.cluster()
        arrayList.clear()
    }

    // ON MARKER CLICK
    override fun onMarkerClick(p0: Marker): Boolean {
        listPoints.forEach { points ->
            if (p0.title == points.name) {
                val bundle = Bundle()
                bundle.putString("name", points.name)
                bundle.putString("address", points.address)
                bundle.putString("instagram", points.instagram)
                bundle.putString("schedule", points.schedule)
                bundle.putString("image", points.image)

                val pointsDetailFragment = PointsDetailFragment()
                pointsDetailFragment.arguments = bundle

                findNavController().navigate(R.id.menuPointsDetailFragment, bundle)
            }
        }
        return true
    }

    // MANAGE LOADING
    private fun showLoading() {
        binding.layoutProgress.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.layoutProgress.visibility = View.GONE
    }

    // SHOW TOAST
    private fun showError(message: String?) {
        Toast.makeText(context,"Por favor, revise su conexión!",Toast.LENGTH_SHORT).show()
        Log.d("DATA ERROR", message.toString())
    }

    // MANAGE LOCATION CALLBACK
    override fun onStop() {
        when (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> fusedLocationProviderClient?.removeLocationUpdates(locationCallback!!)
        }
        super.onStop()
    }

    override fun onResume() {
        when (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.pointsLiveData.removeObserver(getPointsObserver)
    }
}