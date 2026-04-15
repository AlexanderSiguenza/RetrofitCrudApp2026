package sv.edu.udb.retrofitcrudapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import sv.edu.udb.retrofitcrudapp.R
import sv.edu.udb.retrofitcrudapp.data.network.AlumnoApi
import sv.edu.udb.retrofitcrudapp.data.network.RetrofitClient
import sv.edu.udb.retrofitcrudapp.data.util.Resource
import sv.edu.udb.retrofitcrudapp.model.Alumno
import sv.edu.udb.retrofitcrudapp.ui.adapters.AlumnoAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlumnoAdapter
    private lateinit var api: AlumnoApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inicializamos la API usando el Singleton
        api = RetrofitClient.instance.create(AlumnoApi::class.java)

        setupRecyclerView()
        setupFab()
        cargarDatos()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFab() {
        val fabAgregar: FloatingActionButton = findViewById(R.id.fab_agregar)
        fabAgregar.setOnClickListener {
            val intent = Intent(this, CrearAlumnoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos() // Refresca la lista al volver de Crear/Actualizar
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            // Llamada a la API con manejo de estados
            val result = RetrofitClient.safeApiCall { api.obtenerAlumnos() }

            when (result) {
                is Resource.Success -> {
                    val alumnos = result.data ?: emptyList()
                    // Ordenamos por ID descendente para ver lo último creado arriba
                    // Volvemos a usar toIntOrNull() porque el ID ahora es String en el modelo
                    val alumnosOrdenados = alumnos.sortedByDescending { it.id.toIntOrNull() ?: 0 }
                    configurarAdapter(alumnosOrdenados)
                }
                is Resource.Error -> {
                    Log.e("API", "Error: ${result.message}")
                    Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Aquí podrías activar un ProgressBar si tuvieras uno
                }
            }
        }
    }

    private fun configurarAdapter(listado: List<Alumno>) {
        adapter = AlumnoAdapter(listado)
        recyclerView.adapter = adapter

        // Listener para la acción de ELIMINAR desde el BottomSheet
        adapter.setOnItemClickListener(object : AlumnoAdapter.OnItemClickListener {
            override fun onItemClick(alumno: Alumno) {
                // Al darle "Eliminar" en el menú chivo, venimos aquí para confirmar
                confirmarEliminacion(alumno)
            }
        })
    }

    private fun confirmarEliminacion(alumno: Alumno) {
        // Usamos MaterialAlertDialogBuilder para que se vea moderno
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Eliminar registro?")
            .setMessage("¿Estás seguro que deseas borrar a ${alumno.nombre} ${alumno.apellido}?")
            .setCancelable(false)
            .setPositiveButton("SÍ, ELIMINAR") { _, _ ->
                ejecutarBorradoAPI(alumno.id)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun ejecutarBorradoAPI(id: String) { // Asegúrate que sea String
        lifecycleScope.launch {
            val result = RetrofitClient.safeApiCall { api.eliminarAlumno(id) }

            // MockAPI a veces devuelve Success pero el contenido es confuso para Retrofit
            if (result is Resource.Success || result.message?.contains("200") == true || result.message?.contains("204") == true) {
                Toast.makeText(this@MainActivity, "Alumno eliminado con éxito", Toast.LENGTH_SHORT).show()
                cargarDatos() // <-- ESTO ES LO QUE TE FALTA PARA QUE SE BORRE DE LA VISTA
            } else {
                // Si entra aquí pero sí lo borró, es porque la respuesta fue un 200 OK vacío
                Log.d("API_DEBUG", "Respuesta recibida: ${result.message}")
                // Forzamos la recarga de todos modos para que el usuario vea la lista real
                cargarDatos()
            }
        }
    }
}