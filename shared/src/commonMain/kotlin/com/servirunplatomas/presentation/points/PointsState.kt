package com.servirunplatomas.presentation.points

import com.servirunplatomas.utils.Response
import com.servirunplatomas.domain.model.points.Points

sealed class PointsState {
    abstract val response: Response<List<Points>>?
}
data class SuccessPointsState(override val response: Response<List<Points>>) : PointsState()
data class LoadingPointsState(override val response: Response<List<Points>>? = null) : PointsState()
data class ErrorPointsState(override val response: Response<List<Points>>) : PointsState()