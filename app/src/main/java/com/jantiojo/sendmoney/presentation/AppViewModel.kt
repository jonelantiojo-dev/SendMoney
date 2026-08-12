package com.jantiojo.sendmoney.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jantiojo.sendmoney.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    sessionRepository: SessionRepository
) : ViewModel() {

    val uiState: StateFlow<AppUiState> =
        sessionRepository.isLoggedIn
            .map { isLoggedIn ->
                AppUiState(
                    isLoading = false,
                    isLoggedIn = isLoggedIn
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppUiState()
            )
}
