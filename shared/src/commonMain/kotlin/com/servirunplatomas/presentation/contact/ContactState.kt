package com.servirunplatomas.presentation.contact

import com.servirunplatomas.utils.Response
import com.servirunplatomas.domain.model.contact.Contact

sealed class ContactState {
    abstract val response: Response<List<Contact>>?
}
data class SuccessContactState(override val response: Response<List<Contact>>) : ContactState()
data class LoadingContactState(override val response: Response<List<Contact>>? = null) : ContactState()
data class ErrorContactState(override val response: Response<List<Contact>>) : ContactState()