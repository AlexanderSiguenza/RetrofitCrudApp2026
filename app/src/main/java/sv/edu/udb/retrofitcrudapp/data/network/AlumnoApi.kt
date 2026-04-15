package sv.edu.udb.retrofitcrudapp.data.network

import retrofit2.Response
import retrofit2.http.*
import sv.edu.udb.retrofitcrudapp.model.Alumno

interface AlumnoApi {

    @GET("escuela/alumno")
    suspend fun obtenerAlumnos(): Response<List<Alumno>>

    @GET("escuela/alumno/{id}")
    suspend fun obtenerAlumnoPorId(@Path("id") id: String): Response<Alumno> // Cambiado a Int

    @POST("escuela/alumno")
    suspend fun crearAlumno(@Body alumno: Alumno): Response<Alumno>

    @PUT("escuela/alumno/{id}")
    suspend fun actualizarAlumno(@Path("id") id: String, @Body alumno: Alumno): Response<Alumno>

    @DELETE("escuela/alumno/{id}")
    suspend fun eliminarAlumno(@Path("id") id: String): Response<Void>
}