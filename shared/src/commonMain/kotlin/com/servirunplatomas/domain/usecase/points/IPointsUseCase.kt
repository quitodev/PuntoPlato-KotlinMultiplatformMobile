package com.servirunplatomas.domain.usecase.points

import com.servirunplatomas.domain.model.points.Points
import com.servirunplatomas.utils.Response

abstract class IPointsUseCase {
    abstract suspend fun getPoints(): Response<List<Points>>
}