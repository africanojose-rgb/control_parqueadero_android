package com.africano.parqueadero1.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.africano.parqueadero1.domain.model.Vehiculo
import com.africano.parqueadero1.domain.usecase.BuscarVehiculoUseCase
import com.africano.parqueadero1.domain.usecase.ContarVehiculosUseCase
import com.africano.parqueadero1.domain.usecase.EliminarVehiculoUseCase
import com.africano.parqueadero1.domain.usecase.ObtenerTodosVehiculosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val buscarVehiculoUseCase: BuscarVehiculoUseCase,
    private val contarVehiculosUseCase: ContarVehiculosUseCase,
    private val eliminarVehiculoUseCase: EliminarVehiculoUseCase,
    private val obtenerTodosVehiculosUseCase: ObtenerTodosVehiculosUseCase
) : ViewModel() {

    data class UiState(
        val countCarros: Int = 0,
        val countMotos: Int = 0,
        val vehiculoEncontrado: Vehiculo? = null,
        val mensaje: String = "",
        val isLoading: Boolean = false,
        val reporte: String = ""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _contadoresState = MutableStateFlow<ContarVehiculosUseCase.Contadores?>(null)
    val contadoresState: StateFlow<ContarVehiculosUseCase.Contadores?> = _contadoresState.asStateFlow()

    init {
        cargarContadores()
    }

    fun cargarContadores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = contarVehiculosUseCase.execute()) {
                is ContarVehiculosUseCase.Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        countCarros = result.contadores.carros,
                        countMotos = result.contadores.motos,
                        isLoading = false
                    )
                }
                is ContarVehiculosUseCase.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        mensaje = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun buscarVehiculo(placa: String, torre: String, apartamento: String, nroParqueadero: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, mensaje = "")
            when (val result = buscarVehiculoUseCase.execute(placa, torre, apartamento, nroParqueadero)) {
                is BuscarVehiculoUseCase.Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        vehiculoEncontrado = result.vehiculo,
                        mensaje = if (result.vehiculo != null) "" else "No encontrado",
                        isLoading = false
                    )
                }
                is BuscarVehiculoUseCase.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        mensaje = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun eliminarVehiculo(placa: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = eliminarVehiculoUseCase.execute(placa)) {
                is EliminarVehiculoUseCase.Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        vehiculoEncontrado = null,
                        mensaje = "Eliminado",
                        isLoading = false
                    )
                    cargarContadores()
                }
                is EliminarVehiculoUseCase.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        mensaje = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun generarReporte() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = obtenerTodosVehiculosUseCase.execute()) {
                is ObtenerTodosVehiculosUseCase.Result.Success -> {
                    val vehiculos = result.vehiculos
                    if (vehiculos.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "No hay datos para reportar",
                            isLoading = false
                        )
                    } else {
                        val reporte = StringBuilder().apply {
                            append("📋 *REPORTE PARQUEADERO TORONTO*\n")
                            append("--------------------------------\n")
                            vehiculos.forEach {
                                append("🚗 Placa: ${it.placa} | T: ${it.torre} A: ${it.apartamento} | P: ${it.nroParqueadero}\n")
                            }
                            append("--------------------------------\n")
                            append("_\"Soy el mejor, Toronto es la Mejor.. \"_")
                        }.toString()
                        _uiState.value = _uiState.value.copy(
                            reporte = reporte,
                            isLoading = false
                        )
                    }
                }
                is ObtenerTodosVehiculosUseCase.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        mensaje = result.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = "")
    }

    fun limpiarReporte() {
        _uiState.value = _uiState.value.copy(reporte = "")
    }

    class Factory(
        private val buscarVehiculoUseCase: BuscarVehiculoUseCase,
        private val contarVehiculosUseCase: ContarVehiculosUseCase,
        private val eliminarVehiculoUseCase: EliminarVehiculoUseCase,
        private val obtenerTodosVehiculosUseCase: ObtenerTodosVehiculosUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                buscarVehiculoUseCase,
                contarVehiculosUseCase,
                eliminarVehiculoUseCase,
                obtenerTodosVehiculosUseCase
            ) as T
        }
    }
}