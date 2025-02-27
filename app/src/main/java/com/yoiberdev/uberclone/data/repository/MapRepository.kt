package com.yoiberdev.uberclone.data.repository

import com.yoiberdev.uberclone.data.source.firebase.LocationUpdate
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    suspend fun updateLocation(location: LocationUpdate): Result<Unit>
    fun listenForLocations(): Flow<List<LocationUpdate>>
}