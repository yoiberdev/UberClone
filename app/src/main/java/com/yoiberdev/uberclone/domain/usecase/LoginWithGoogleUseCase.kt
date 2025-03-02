package com.yoiberdev.uberclone.domain.usecase

import com.yoiberdev.uberclone.domain.repository.AuthRepository


class LoginWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.loginWithGoogle(idToken)
    }
}
