package com.africano.parqueadero1.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.africano.parqueadero1.R
import com.africano.parqueadero1.data.local.database.AppDatabase
import com.africano.parqueadero1.data.repository.VehiculoRepository
import com.africano.parqueadero1.domain.usecase.BuscarVehiculoUseCase
import com.africano.parqueadero1.domain.usecase.ContarVehiculosUseCase
import com.africano.parqueadero1.domain.usecase.EliminarVehiculoUseCase
import com.africano.parqueadero1.domain.usecase.ObtenerTodosVehiculosUseCase
import com.africano.parqueadero1.ui.registro.RegistroActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var tvCarros: TextView
    private lateinit var tvMotos: TextView
    private lateinit var txtResultado: TextView
    private lateinit var btnEliminar: Button
    private lateinit var etPlaca: EditText
    private lateinit var etTorre: EditText
    private lateinit var etApto: EditText
    private lateinit var etParq: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarViewModel()
        inicializarViews()
        setupListeners()
        observeState()
    }

    private fun inicializarViewModel() {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = VehiculoRepository(db.vehiculoDao())

        val buscarUseCase = BuscarVehiculoUseCase(repository)
        val contarUseCase = ContarVehiculosUseCase(repository)
        val eliminarUseCase = EliminarVehiculoUseCase(repository)
        val obtenerTodosUseCase = ObtenerTodosVehiculosUseCase(repository)

        val factory = MainViewModel.Factory(
            buscarUseCase,
            contarUseCase,
            eliminarUseCase,
            obtenerTodosUseCase
        )
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun inicializarViews() {
        tvCarros = findViewById(R.id.tvCountCarros)
        tvMotos = findViewById(R.id.tvCountMotos)
        txtResultado = findViewById(R.id.txtResultadoBusqueda)
        btnEliminar = findViewById(R.id.btnEliminar)
        etPlaca = findViewById(R.id.etBuscaPlaca)
        etTorre = findViewById(R.id.etBuscaTorre)
        etApto = findViewById(R.id.etBuscaApto)
        etParq = findViewById(R.id.etBuscaParq)
    }

    private fun setupListeners() {
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val btnConsultar = findViewById<Button>(R.id.btnConsultar)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnReporte = findViewById<Button>(R.id.btnReporte)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        btnConsultar.setOnClickListener {
            ocultarTeclado()
            val pB = etPlaca.text.toString().uppercase().trim()
            val tB = etTorre.text.toString().trim()
            val aB = etApto.text.toString().trim()
            val pqB = etParq.text.toString().trim()

            if (pB.isEmpty() && tB.isEmpty() && aB.isEmpty() && pqB.isEmpty()) {
                Toast.makeText(this, "Ingrese al menos un criterio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.buscarVehiculo(pB, tB, aB, pqB)
        }

        btnLimpiar.setOnClickListener {
            limpiarPantalla()
        }

        btnReporte.setOnClickListener {
            viewModel.generarReporte()
        }

        btnEliminar.setOnClickListener {
            val state = viewModel.uiState.value
            val vehiculo = state.vehiculoEncontrado ?: return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Confirmar Salida")
                .setMessage("¿Desea eliminar el registro de ${vehiculo.placa}?")
                .setPositiveButton("SÍ, ELIMINAR") { _, _ ->
                    viewModel.eliminarVehiculo(vehiculo.placa)
                }
                .setNegativeButton("CANCELAR", null)
                .show()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                tvCarros.text = state.countCarros.toString()
                tvMotos.text = state.countMotos.toString()

                if (state.vehiculoEncontrado != null) {
                    val v = state.vehiculoEncontrado
                    txtResultado.text = "✅ HALLADO:\n🚗 Placa: ${v.placa} (${v.tipo})\n📍 Torre: ${v.torre} - Apto: ${v.apartamento}\n🅿️ Parq: ${v.nroParqueadero}"
                    txtResultado.setTextColor(getColor(android.R.color.holo_green_dark))
                    btnEliminar.visibility = View.VISIBLE
                } else if (state.mensaje.isNotEmpty()) {
                    txtResultado.text = "❌ ${state.mensaje}"
                    txtResultado.setTextColor(getColor(android.R.color.holo_red_dark))
                    btnEliminar.visibility = View.GONE
                }

                if (state.mensaje == "Eliminado") {
                    Toast.makeText(this@MainActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                    limpiarPantalla()
                }

                if (state.reporte.isNotEmpty()) {
                    compartirReporte(state.reporte)
                    viewModel.limpiarReporte()
                }

                if (state.mensaje.isNotEmpty() && state.vehiculoEncontrado == null && state.mensaje != "Eliminado") {
                    Toast.makeText(this@MainActivity, state.mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun compartirReporte(reporte: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, reporte)
        }
        startActivity(Intent.createChooser(intent, "Enviar reporte por:"))
    }

    private fun limpiarPantalla() {
        etPlaca.text.clear()
        etTorre.text.clear()
        etApto.text.clear()
        etParq.text.clear()
        txtResultado.text = ""
        btnEliminar.visibility = View.GONE
    }

    private fun ocultarTeclado() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarContadores()
    }
}