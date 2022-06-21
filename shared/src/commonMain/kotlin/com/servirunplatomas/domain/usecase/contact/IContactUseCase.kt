package com.servirunplatomas.domain.usecase.contact

import com.servirunplatomas.domain.model.contact.Contact
import com.servirunplatomas.utils.Response

abstract class IContactUseCase {
    abstract suspend fun getContact(): Response<List<Contact>>
}