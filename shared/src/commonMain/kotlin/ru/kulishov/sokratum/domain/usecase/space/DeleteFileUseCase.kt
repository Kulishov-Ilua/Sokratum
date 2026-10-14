package ru.kulishov.sokratum.domain.usecase.space

import ru.kulishov.sokratum.data.remote.model.space.SpaceAndFiles
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class DeleteFileUseCase(
    private val repository: SpaceRepository
) {
    suspend operator fun invoke(spaceId:Long, fileId:String): SpaceAndFiles? = repository.deleteFile(spaceId,fileId)
}