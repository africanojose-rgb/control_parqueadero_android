package com.africano.parqueadero1.ui.registro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.africano.parqueadero1.R
import com.africano.parqueadero1.data.local.database.AppDatabase
import com.africano.parqueadero1.data.repository.VehiculoRepository
import com.africano.parqueadero1.domain.usecase.RegistrarVehiculoUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {

    private lateinit var viewModel: RegistroViewModel

    private lateinit var etPlaca: EditText
    private lateinit var etTipo: EditText
    private lateinit var etParqueadero: EditText
    private lateinit var etTorre: EditText
    private lateinit var etApto: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        inicializarViewModel()
        inicializarViews()
        setupListeners()
        observeState()
    }

    private fun inicializarViewModel() {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = VehiculoRepository(db.vehiculoDao())
        val registrarUseCase = RegistrarVehiculoUseCase(repository)

        val factory = RegistroViewModel.Factory(registrarUseCase)
        viewModel = ViewModelProvider(this, factory)[RegistroViewModel::class.java]
    }

    private fun inicializarViews() {
        etPlaca = findViewById(R.id.etPlaca)
        etTipo = findViewById(R.id.etTipo)
        etParqueadero = findViewById(R.id.etParqueadero)
        etTorre = findViewById(R.id.etTorre)
        etApto = findViewById(R.id.etApto)
    }

    private fun setupListeners() {
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            val placa = etPlaca.text.toString()
            val tipo = etTipo.text.toString()
            val parq = etParqueadero.text.toString()
            val torre = etTorre.text.toString()
            val apto = etApto.text.toString()

            if (placa.isBlank() || torre.isBlank()) {
                Toast.makeText(this, "Placa y Torre son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registrarVehiculo(placa, tipo, parq, torre, apto)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (state.registroExitoso) {
                    Toast.makeText(this@RegistroActivity, state.mensaje, Toast.LENGTH_SHORT).show()
                    finish()
                }

                if (state.error.isNotEmpty()) {
                    Toast.makeText(this@RegistroActivity, state.error, Toast.LENGTH_LONG).show()
                }

                if (state.mensaje.isNotEmpty() && !state.registroExitoso) {
                    Toast.makeText(this@RegistroActivity, state.mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}