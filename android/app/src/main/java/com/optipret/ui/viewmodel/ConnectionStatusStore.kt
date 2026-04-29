package com.optipret.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ConnectionStatus {
  Unknown,
  Testing,
  Connected,
  Disconnected
}

object ConnectionStatusStore {
  private val _status = MutableStateFlow(ConnectionStatus.Unknown)
  val status: StateFlow<ConnectionStatus> = _status

  fun update(status: ConnectionStatus) {
    _status.value = status
  }
}
