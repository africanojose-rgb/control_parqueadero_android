package com.africano.parqueadero1.domain.usecase

import com.africano.parqueadero1.domain.model.Vehiculo
import com.africano.parqueadero1.data.repository.VehiculoRepository

class BuscarVehiculoUseCase(private val repository: VehiculoRepository) {

    sealed class Result {
        data class Success(val vehiculo: Vehiculo?) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(
        placa: String = "",
        torre: String = "",
        apartamento: String = "",
        nroParqueadero: String = ""
    ): Result {
        if (placa.isBlank() && torre.isBlank() && apartamento.isBlank() && nroParqueadero.isBlank()) {
            return Result.Error("Ingrese al menos un criterio de búsqueda")
        }

        return try {
            val placaNormalizada = placa.uppercase().trim()
            val resultado = repository.buscar(
                placa = placaNormalizada,
                torre = torre.trim(),
                apartamento = apartamento.trim(),
                nroParqueadero = nroParqueadero.trim()
            )
            Result.Success(resultado)
        } catch (e: Exception) {
            Result.Error("Error en la búsqueda: ${e.message}")
        }
    }
}