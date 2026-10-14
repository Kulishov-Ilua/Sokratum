package ru.kulishov.sokratum.domain.usecase.space

import ru.kulishov.sokratum.data.remote.model.space.SelectedFile
import ru.kulishov.sokratum.data.remote.model.space.SpaceAndFiles
import ru.kulishov.sokratum.domain.repository.SpaceRepository

class AddFileUseCase(
    private val repository: SpaceRepository
) {
    suspend operator fun invoke(spaceId: Long,file: SelectedFile): SpaceAndFiles? = repository.addFile(spaceId.toInt(),file)
}