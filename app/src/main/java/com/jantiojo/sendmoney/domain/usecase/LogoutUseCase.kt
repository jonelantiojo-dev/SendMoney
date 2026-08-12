package com.jantiojo.sendmoney.domain.usecase

import com.jantiojo.sendmoney.domain.repository.SessionRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke() {
        sessionRepository.logout()
    }
}
