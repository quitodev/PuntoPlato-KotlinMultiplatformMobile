package com.servirunplatomas.domain.usecase.points

import com.servirunplatomas.data.source.points.PointsDataSource

class PointsUseCase(private val pointsDataSource: PointsDataSource) : IPointsUseCase() {
    override suspend fun getPoints() = pointsDataSource.getPointsRepo()
}