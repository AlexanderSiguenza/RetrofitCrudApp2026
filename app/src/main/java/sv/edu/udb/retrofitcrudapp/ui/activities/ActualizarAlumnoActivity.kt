package sv.edu.udb.retrofitcrudapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import sv.edu.udb.retrofitcrudapp.R
import sv.edu.udb.retrofitcrudapp.data.network.AlumnoApi
import sv.edu.udb.retrofitcrudapp.data.network.RetrofitClient
import sv.edu.udb.retrofitcrudapp.data.util.Resource
import sv.edu.udb.retrofitcrudapp.model.Alumno

class ActualizarAlumnoActivity : AppCompatActivity() {

    private lateinit var api: AlumnoApi
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var actualizarButton: Button

    // Variable para guardar el ID que viene del Intent
    private var alumnoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_alumno)

        // 1. Inicializar vistas (Asegúrate que los IDs coincidan con tu nuevo XML perrón)
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        edadEditText = findViewById(R.id.edadEditText)
        actualizarButton = findViewById(R.id.actualizarButton)

        api = RetrofitClient.instance.create(AlumnoApi::class.java)

        // 2. Obtener datos del Intent (OJO: El ID ahora lo recibimos como String)
        alumnoId = intent.getStringExtra("alumno_id") ?: ""
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val edad = intent.getIntExtra("edad", 0)

        // 3. Llenar el formulario con los datos actuales
        nombreEditText.setText(nombre)
        apellidoEditText.setText(apellido)
        edadEditText.setText(edad.toString())

        actualizarButton.setOnClickListener {
            ejecutarActualizacion()
        }
    }

    private fun ejecutarActualizacion() {
        val nombre = nombreEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val edadStr = edadEditText.text.toString().trim()

        // Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val edadInt = edadStr.toIntOrNull() ?: 0

        // Creamos el objeto Alumno con el ID tipo String
        val alumnoActualizado = Alumno(alumnoId, nombre, apellido, edadInt)

        lifecycleScope.launch {
            // Log para depurar: esto te dirá en el Logcat qué URL se está armando
            Log.d("API_DEBUG", "Enviando PUT a: escuela/alumno/$alumnoId")

            val result = RetrofitClient.safeApiCall {
                api.actualizarAlumno(alumnoId, alumnoActualizado)
            }

            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this@ActualizarAlumnoActivity, "¡Alumno actualizado!", Toast.LENGTH_SHORT).show()
                    finish() // Cerramos y regresamos al Main
                }
                is Resource.Error -> {
                    Log.e("API", "Error: ${result.message}")
                    // Aquí verás si sigue dando 404 o si es otro problema
                    Toast.makeText(this@ActualizarAlumnoActivity, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> { }
            }
        }
    }
}