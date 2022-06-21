package com.servirunplatomas.android.ui.contact

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.servirunplatomas.android.BuildConfig
import com.servirunplatomas.android.R
import com.servirunplatomas.android.databinding.FragmentContactBinding
import com.servirunplatomas.utils.Response
import com.servirunplatomas.domain.model.contact.Contact
import com.servirunplatomas.presentation.contact.*
import dev.icerock.moko.mvvm.MvvmFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.databinding.BR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.Exception

@ExperimentalCoroutinesApi
class ContactFragment : MvvmFragment<FragmentContactBinding, ContactViewModel>() {

    // OBSERVER
    private lateinit var getAppDetailsObserver : (state: ContactState) -> Unit

    // VIEW MODEL
    override val layoutId: Int = R.layout.fragment_contact
    override val viewModelVariableId: Int = BR.viewModel
    override val viewModelClass: Class<ContactViewModel> = ContactViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory {
        return createViewModelFactory { ContactViewModel() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()
        observeViewModel()
    }

    // SET BUTTONS
    private fun setButtons() {
        binding.dismissButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    // OBSERVER FOR VIEW MODEL
    private fun observeViewModel() {
        getAppDetailsObserver = { getAppDetails(viewModel.appDetailsLiveData.value) }
        viewModel.appDetailsLiveData.addObserver(getAppDetailsObserver)
        viewModel.getAppDetails()
    }

    // GET APP DETAILS
    private fun getAppDetails(state: ContactState) {
        when (state) {
            is LoadingContactState -> showLoading()
            is SuccessContactState -> {
                hideLoading()
                val response = state.response as Response.Success
                setDataContact(response.data[0])
                checkAppVersion(response.data[0])
                Log.d("DATA", response.data.toString())
            }
            is ErrorContactState -> {
                hideLoading()
                val response = state.response as Response.Error
                showError(response.message)
            }
        }
    }

    // SET DATA CONTACT
    private fun setDataContact(contact: Contact) {
        val imageList = ArrayList<SlideModel>()
        contact.images.forEach { image ->
            imageList.add(SlideModel(imageUrl = image))
        }
        binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        binding.textInstagram.text = contact.instagram
        binding.textEmail.text = contact.email
        setContactButtons(contact)
    }

    // SET CONTACT BUTTONS
    private fun setContactButtons(contact: Contact) {
        binding.layoutInstagram.setOnClickListener {
            var intent: Intent
            try {
                context?.packageManager?.getPackageInfo("com.instagram.android", 0)
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/${contact.instagram.replace("@","")}"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            } catch (e: Exception) {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/${contact.instagram.replace("@","")}"))
            }
            startActivity(intent)
        }
        binding.layoutEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${contact.email}")
            }
            startActivity(intent)
        }
    }

    // CHECK APP VERSION TO SHOW ALERT DIALOG TO UPDATE
    private fun checkAppVersion(contact: Contact) {
        if (BuildConfig.VERSION_CODE < contact.version.toInt()) {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Existe una nueva versión de la aplicación. Podés actualizarla ahora o más tarde!").setCancelable(false)
                .setPositiveButton("Actualizar ahora") { dialog, _ ->
                    openPlaystore()
                    dialog.dismiss()
                }
                .setNegativeButton("Más tarde") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Actualización disponible")
            alert.show()
        }
    }

    // OPEN PLAYSTORE TO UPDATE APP
    private fun openPlaystore(){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri
                .parse("market://details?id=com.servirunplatomas.puntoplato")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri
                .parse("https://play.google.com/store/apps/details?id=com.servirunplatomas.puntoplato")))
        }
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

    override fun onPause() {
        binding.imageSlider.stopSliding()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.appDetailsLiveData.removeObserver(getAppDetailsObserver)
    }
}