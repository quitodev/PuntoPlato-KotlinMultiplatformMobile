package com.servirunplatomas.presentation.points

import com.servirunplatomas.utils.Response
import com.servirunplatomas.domain.model.points.Points
import com.servirunplatomas.di.KodeinInjector
import com.servirunplatomas.domain.usecase.points.IPointsUseCase
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.kodein.di.instance

class PointsViewModel : ViewModel() {

    var pointsLiveData = MutableLiveData<PointsState>(LoadingPointsState())

    private val iPointsUseCase by KodeinInjector.instance<IPointsUseCase>()

    fun getPoints() {
        viewModelScope.launch {
            checkResponse(iPointsUseCase.getPoints())
        }
    }

    private fun checkResponse(response: Response<List<Points>>) {
        when (response) {
            is Response.Success -> pointsLiveData.postValue(SuccessPointsState(response))
            is Response.Error -> pointsLiveData.postValue(ErrorPointsState(response))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}