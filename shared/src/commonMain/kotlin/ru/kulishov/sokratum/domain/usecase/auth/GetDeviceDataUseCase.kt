package ru.kulishov.sokratum.domain.usecase.auth

import ru.kulishov.sokratum.data.remote.model.auth.DeviceDataResponse
import ru.kulishov.sokratum.domain.repository.AuthRepository

class GetDeviceDataUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke():List<DeviceDataResponse> = repository.getDevicesData()
}