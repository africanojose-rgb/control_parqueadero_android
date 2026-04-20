package com.africano.parqueadero1.domain.usecase

import com.africano.parqueadero1.domain.model.Vehiculo
import com.africano.parqueadero1.data.repository.VehiculoRepository

class RegistrarVehiculoUseCase(private val repository: VehiculoRepository) {

    sealed class Result {
        data class Success(val vehiculo: Vehiculo) : Result()
        data class Error(val message: String) : Result()
        object YaExiste : Result()
    }

    suspend fun execute(placa: String, tipo: String, nroParqueadero: String, torre: String, apartamento: String): Result {
        if (placa.isBlank()) {
            return Result.Error("La placa es obligatoria")
        }
        if (torre.isBlank()) {
            return Result.Error("La torre es obligatoria")
        }

        val placaNormalizada = placa.uppercase().trim()
        val tipoNormalizado = when {
            tipo.lowercase().contains("moto") -> "Moto"
            else -> "Carro"
        }

        return try {
            val existe = repository.existePlaca(placaNormalizada)
            if (existe) {
                Result.YaExiste
            } else {
                val vehiculo = Vehiculo(
                    placa = placaNormalizada,
                    tipo = tipoNormalizado,
                    nroParqueadero = nroParqueadero.trim(),
                    torre = torre.trim(),
                    apartamento = apartamento.trim()
                )
                repository.insertar(vehiculo)
                Result.Success(vehiculo)
            }
        } catch (e: Exception) {
            Result.Error("Error al registrar: ${e.message}")
        }
    }
}