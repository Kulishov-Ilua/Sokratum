package ru.kulishov.sokratum.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.domain.model.RefreshResult
import ru.kulishov.sokratum.domain.repository.AuthRepository

class GetDeviceDataUseCase(
    private val repository: AuthRepository
) {
     operator fun invoke():Flow<RefreshResult<DeviceDataResponse>> = repository.getDevicesData()
}