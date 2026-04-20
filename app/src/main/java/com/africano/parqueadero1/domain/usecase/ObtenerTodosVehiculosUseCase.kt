package com.africano.parqueadero1.domain.usecase

import com.africano.parqueadero1.domain.model.Vehiculo
import com.africano.parqueadero1.data.repository.VehiculoRepository

class ObtenerTodosVehiculosUseCase(private val repository: VehiculoRepository) {

    sealed class Result {
        data class Success(val vehiculos: List<Vehiculo>) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(): Result {
        return try {
            val vehiculos = repository.obtenerTodos()
            Result.Success(vehiculos)
        } catch (e: Exception) {
            Result.Error("Error al obtener vehículos: ${e.message}")
        }
    }
}