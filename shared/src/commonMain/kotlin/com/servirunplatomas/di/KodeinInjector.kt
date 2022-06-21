package com.servirunplatomas.di

import com.servirunplatomas.data.network.WebService
import com.servirunplatomas.data.source.contact.ContactDataSource
import com.servirunplatomas.data.source.points.PointsDataSource
import com.servirunplatomas.domain.usecase.contact.ContactUseCase
import com.servirunplatomas.domain.usecase.contact.IContactUseCase
import com.servirunplatomas.domain.usecase.points.IPointsUseCase
import com.servirunplatomas.domain.usecase.points.PointsUseCase
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.kodein.di.*
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
val KodeinInjector = DI {

    bind<CoroutineContext>() with provider { Dispatchers.Main }

    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json = Json {
                isLenient = false
                ignoreUnknownKeys = true
                allowSpecialFloatingPointValues = true
                useArrayPolymorphism = false
            })
        }
    }

    /**
     * WEB SERVICE
     **/
    bind<WebService>() with provider { WebService() }

    /**
     * DATA SOURCES
     **/
    bind<ContactDataSource>() with provider { ContactDataSource(instance(), client) }
    bind<PointsDataSource>() with provider { PointsDataSource(instance(), client) }

    /**
     * USE CASES
     **/
    bind<IContactUseCase>() with singleton { ContactUseCase(instance()) }
    bind<IPointsUseCase>() with singleton { PointsUseCase(instance()) }
}