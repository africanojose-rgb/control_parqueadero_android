package com.africano.parqueadero1.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tabla_vehiculos",
    indices = [
        Index(value = ["placa"], unique = true),
        Index(value = ["tipo"]),
        Index(value = ["torre", "apartamento"]),
        Index(value = ["nroParqueadero"])
    ]
)
data class Vehiculo(
    @PrimaryKey
    val placa: String,
    val tipo: String,
    val nroParqueadero: String,
    val torre: String,
    val apartamento: String
)