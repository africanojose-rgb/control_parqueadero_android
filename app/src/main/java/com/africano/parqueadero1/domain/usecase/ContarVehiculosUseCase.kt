package com.africano.parqueadero1.domain.usecase

import com.africano.parqueadero1.data.repository.VehiculoRepository

class ContarVehiculosUseCase(private val repository: VehiculoRepository) {

    data class Contadores(
        val carros: Int,
        val motos: Int
    )

    sealed class Result {
        data class Success(val contadores: Contadores) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(): Result {
        return try {
            val carros = repository.contarCarros()
            val motos = repository.contarMotos()
            Result.Success(Contadores(carros, motos))
        } catch (e: Exception) {
            Result.Error("Error al contar: ${e.message}")
        }
    }
}