package sv.edu.udb.retrofitcrudapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import sv.edu.udb.retrofitcrudapp.R
import sv.edu.udb.retrofitcrudapp.data.network.AlumnoApi
import sv.edu.udb.retrofitcrudapp.data.network.RetrofitClient
import sv.edu.udb.retrofitcrudapp.data.util.Resource
import sv.edu.udb.retrofitcrudapp.model.Alumno

class CrearAlumnoActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var crearButton: Button
    private lateinit var api: AlumnoApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_alumno)

        // 1. Inicializar el servicio
        api = RetrofitClient.instance.create(AlumnoApi::class.java)

        // 2. Referenciar las vistas (Asegúrate que coincidan con tu nuevo XML perrón)
        nombreEditText = findViewById(R.id.editTextNombre)
        apellidoEditText = findViewById(R.id.editTextApellido)
        edadEditText = findViewById(R.id.editTextEdad)
        crearButton = findViewById(R.id.btnGuardar)

        // 3. Configurar el evento de click
        crearButton.setOnClickListener {
            ejecutarCreacion()
        }
    }

    private fun ejecutarCreacion() {
        val nombre = nombreEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val edadStr = edadEditText.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val edadInt = edadStr.toIntOrNull() ?: 0

        // CAMBIO IMPORTANTE: Mandamos el ID como String vacío ""
        // MockAPI se encargará de asignarle un ID automático (como "88", "89", etc.)
        val nuevoAlumno = Alumno("", nombre, apellido, edadInt)

        lifecycleScope.launch {
            // Usamos safeApiCall para el manejo de estados
            val result = RetrofitClient.safeApiCall { api.crearAlumno(nuevoAlumno) }

            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this@CrearAlumnoActivity, "Alumno creado exitosamente", Toast.LENGTH_SHORT).show()
                    // Cerramos la actividad para volver al Main (el onResume del Main refrescará la lista)
                    finish()
                }
                is Resource.Error -> {
                    Log.e("API", "Error al crear: ${result.message}")
                    Toast.makeText(this@CrearAlumnoActivity, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> { }
            }
        }
    }
}