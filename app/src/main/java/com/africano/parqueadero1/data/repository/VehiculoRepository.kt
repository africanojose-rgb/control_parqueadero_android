package com.africano.parqueadero1.data.repository

import com.africano.parqueadero1.data.local.dao.VehiculoDao
import com.africano.parqueadero1.domain.model.Vehiculo

class VehiculoRepository(private val vehiculoDao: VehiculoDao) {

    suspend fun obtenerTodos(): List<Vehiculo> = vehiculoDao.obtenerTodos()

    suspend fun buscarPorPlaca(placa: String): Vehiculo? = vehiculoDao.buscarPorPlaca(placa)

    suspend fun buscar(placa: String, torre: String, apartamento: String, nroParqueadero: String): Vehiculo? {
        return vehiculoDao.buscar(placa, torre, apartamento, nroParqueadero)
    }

    suspend fun insertar(vehiculo: Vehiculo) = vehiculoDao.insertar(vehiculo)

    suspend fun eliminarPorPlaca(placa: String) = vehiculoDao.eliminarPorPlaca(placa)

    suspend fun contarCarros(): Int = vehiculoDao.contarCarros()

    suspend fun contarMotos(): Int = vehiculoDao.contarMotos()

    suspend fun existePlaca(placa: String): Boolean = vehiculoDao.existePlaca(placa)
}