package com.africano.parqueadero1.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.africano.parqueadero1.domain.usecase.RegistrarVehiculoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val registrarVehiculoUseCase: RegistrarVehiculoUseCase
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val mensaje: String = "",
        val registroExitoso: Boolean = false,
        val error: String = ""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun registrarVehiculo(
        placa: String,
        tipo: String,
        nroParqueadero: String,
        torre: String,
        apartamento: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, mensaje = "", error = "")

            when (val result = registrarVehiculoUseCase.execute(
                placa = placa,
                tipo = tipo,
                nroParqueadero = nroParqueadero,
                torre = torre,
                apartamento = apartamento
            )) {
                is RegistrarVehiculoUseCase.Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensaje = "${result.vehiculo.tipo} ${result.vehiculo.placa} Guardado",
                        registroExitoso = true
                    )
                }
                is RegistrarVehiculoUseCase.Result.YaExiste -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "La placa ya existe. Use otra placa."
                    )
                }
                is RegistrarVehiculoUseCase.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun limpiarEstado() {
        _uiState.value = UiState()
    }

    class Factory(
        private val registrarVehiculoUseCase: RegistrarVehiculoUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegistroViewModel(registrarVehiculoUseCase) as T
        }
    }
}