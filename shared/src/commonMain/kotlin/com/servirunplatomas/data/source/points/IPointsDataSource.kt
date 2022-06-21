package com.servirunplatomas.data.source.points

import com.servirunplatomas.utils.Response
import com.servirunplatomas.domain.model.points.Points

abstract class IPointsDataSource {
    abstract suspend fun getPointsRepo(): Response<List<Points>>
}