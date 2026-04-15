package sv.edu.udb.retrofitcrudapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sv.edu.udb.retrofitcrudapp.data.util.Resource
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://66240d4504457d4aaf9b8530.mockapi.io/api/"

    // 1. Configuramos el cliente con Interceptors y Timeouts
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // 2. Instancia de Retrofit
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 3. Tu función SafeApiCall mejorada
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Cuerpo de respuesta vacío")
                }
            } else {
                Resource.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error de conexión desconocido")
        }
    }
}