package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.repository.SessionRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String
    ): Result<Unit> {

        if (username.isBlank() || password.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Username and password are required"
                )
            )
        }

        if (
            username != "test" ||
            password != "123456"
        ) {
            return Result.failure(
                IllegalArgumentException(
                    "Invalid username or password"
                )
            )
        }

        sessionRepository.saveSession(username)

        return Result.success(Unit)
    }
}
