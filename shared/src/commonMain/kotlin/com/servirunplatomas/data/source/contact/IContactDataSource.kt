package com.servirunplatomas.data.source.contact

import com.servirunplatomas.utils.Response
import com.servirunplatomas.domain.model.contact.Contact

abstract class IContactDataSource {
    abstract suspend fun getContactRepo(): Response<List<Contact>>
}