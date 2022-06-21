package com.servirunplatomas.domain.usecase.contact

import com.servirunplatomas.data.source.contact.ContactDataSource

class ContactUseCase(private val contactDataSource: ContactDataSource) : IContactUseCase() {
    override suspend fun getContact() = contactDataSource.getContactRepo()
}