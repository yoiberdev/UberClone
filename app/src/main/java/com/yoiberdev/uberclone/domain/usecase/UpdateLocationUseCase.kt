package com.yoiberdev.uberclone.domain.usecase

import com.yoiberdev.uberclone.data.repository.MapRepository
import com.yoiberdev.uberclone.data.source.firebase.LocationUpdate

class UpdateLocationUseCase(private val repository: MapRepository) {
    suspend operator fun invoke(location: LocationUpdate): Result<Unit> {
        return repository.updateLocation(location)
    }
}