package com.servirunplatomas.presentation.contact

import com.servirunplatomas.utils.Response
import com.servirunplatomas.domain.model.contact.Contact
import com.servirunplatomas.di.KodeinInjector
import com.servirunplatomas.domain.usecase.contact.IContactUseCase
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.kodein.di.instance

class ContactViewModel : ViewModel() {

    var appDetailsLiveData = MutableLiveData<ContactState>(LoadingContactState())

    private val iContactUseCase by KodeinInjector.instance<IContactUseCase>()

    fun getAppDetails() {
        viewModelScope.launch {
            checkResponse(iContactUseCase.getContact())
        }
    }

    private fun checkResponse(response: Response<List<Contact>>) {
        when (response) {
            is Response.Success -> appDetailsLiveData.postValue(SuccessContactState(response))
            is Response.Error -> appDetailsLiveData.postValue(ErrorContactState(response))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}