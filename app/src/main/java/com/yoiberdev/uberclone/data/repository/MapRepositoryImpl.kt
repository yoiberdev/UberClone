package com.yoiberdev.uberclone.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yoiberdev.uberclone.data.source.firebase.FirebaseMapSource
import com.yoiberdev.uberclone.data.source.firebase.LocationUpdate

class MapRepositoryImpl(firestore: FirebaseFirestore) : MapRepository {
    private val mapSource = FirebaseMapSource(firestore)

    override suspend fun updateLocation(location: LocationUpdate): Result<Unit> {
        return mapSource.updateLocation(location)
    }

    override fun listenForLocations() = mapSource.listenForLocations()
}