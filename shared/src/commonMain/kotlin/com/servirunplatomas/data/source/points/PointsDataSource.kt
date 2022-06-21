package com.servirunplatomas.data.source.points

import com.servirunplatomas.data.network.WebService
import io.ktor.client.*

class PointsDataSource(private val webService: WebService,
                       private val client: HttpClient): IPointsDataSource() {
    override suspend fun getPointsRepo() = webService.getPointsDataSource(client)
}