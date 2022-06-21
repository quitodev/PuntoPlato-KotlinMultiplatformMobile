package com.servirunplatomas.android.ui.points

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.servirunplatomas.android.databinding.FragmentPointsDetailBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class PointsDetailFragment : Fragment() {

    // VIEW BINDING
    private var _binding: FragmentPointsDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPointsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()
        showLoadingImages()
        showDataMarker()
        openInstagram()
    }

    // SET BUTTONS
    private fun setButtons() {
        binding.dismissButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    // SHOW LOADING IMAGES
    private fun showLoadingImages() {
        binding.layoutProgressDetail.visibility = View.VISIBLE
    }

    // SHOW DETAILS POINT
    private fun showDataMarker() {
        binding.textName.text = arguments?.getString("name")
        binding.textName.text = arguments?.getString("name")
        binding.textAddress.text = arguments?.getString("address")
        binding.textInstagram.text = arguments?.getString("instagram")
        binding.textSchedule.text = arguments?.getString("schedule")

        Picasso.get().load(arguments?.getString("image")).into(binding.imagePoint, object: Callback {
            override fun onSuccess() {
                binding.layoutProgressDetail.visibility = View.GONE
            }
            override fun onError(e: Exception?) {
                object : CountDownTimer(3000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.layoutErrorDetailFragment.visibility = View.VISIBLE
                    }
                    override fun onFinish() {
                        binding.layoutErrorDetailFragment.visibility = View.GONE
                    }
                }.start()
            }
        })
    }

    // SET BUTTON FUNCTIONS
    private fun openInstagram() {
        binding.layoutInstagram.setOnClickListener {
            var intent: Intent
            try {
                context?.packageManager?.getPackageInfo("com.instagram.android", 0)
                intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/_u/${arguments?.getString("instagram")?.replace("@","")}"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } catch (e: Exception) {
                intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://instagram.com/${arguments?.getString("instagram")?.replace("@","")}"))
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}