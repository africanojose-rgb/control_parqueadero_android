package com.africano.parqueadero1.domain.usecase

import com.africano.parqueadero1.data.repository.VehiculoRepository

class EliminarVehiculoUseCase(private val repository: VehiculoRepository) {

    sealed class Result {
        object Success : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(placa: String): Result {
        if (placa.isBlank()) {
            return Result.Error("Placa inválida")
        }

        return try {
            repository.eliminarPorPlaca(placa)
            Result.Success
        } catch (e: Exception) {
            Result.Error("Error al eliminar: ${e.message}")
        }
    }
}