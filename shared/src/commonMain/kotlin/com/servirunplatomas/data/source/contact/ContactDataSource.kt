package com.servirunplatomas.data.source.contact

import com.servirunplatomas.data.network.WebService
import io.ktor.client.*

class ContactDataSource(private val webService: WebService,
                        private val client: HttpClient): IContactDataSource() {
    override suspend fun getContactRepo() = webService.getContactDataSource(client)
}