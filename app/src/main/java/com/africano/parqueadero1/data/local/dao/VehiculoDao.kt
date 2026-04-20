package com.africano.parqueadero1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.africano.parqueadero1.domain.model.Vehiculo

@Dao
interface VehiculoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(vehiculo: Vehiculo)

    @Query("SELECT * FROM tabla_vehiculos ORDER BY torre, apartamento")
    suspend fun obtenerTodos(): List<Vehiculo>

    @Query("SELECT * FROM tabla_vehiculos WHERE placa = :placa LIMIT 1")
    suspend fun buscarPorPlaca(placa: String): Vehiculo?

    @Query("""
        SELECT * FROM tabla_vehiculos
        WHERE (:placa != '' AND placa = :placa)
        OR (:torre != '' AND :apartamento != '' AND torre = :torre AND apartamento = :apartamento)
        OR (:nroParqueadero != '' AND nroParqueadero = :nroParqueadero)
        LIMIT 1
    """)
    suspend fun buscar(placa: String, torre: String, apartamento: String, nroParqueadero: String): Vehiculo?

    @Query("SELECT COUNT(*) FROM tabla_vehiculos WHERE tipo = 'Carro'")
    suspend fun contarCarros(): Int

    @Query("SELECT COUNT(*) FROM tabla_vehiculos WHERE tipo = 'Moto'")
    suspend fun contarMotos(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM tabla_vehiculos WHERE placa = :placa)")
    suspend fun existePlaca(placa: String): Boolean

    @Query("DELETE FROM tabla_vehiculos WHERE placa = :placa")
    suspend fun eliminarPorPlaca(placa: String)
}