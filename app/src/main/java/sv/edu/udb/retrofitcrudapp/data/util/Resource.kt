package sv.edu.udb.retrofitcrudapp.data.util

/**
 * Esta clase sellada envuelve nuestros datos para manejar estados.
 * T es el tipo de dato que esperamos (ej. Alumno o List<Alumno>).
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}