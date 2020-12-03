package edu.utap.billbriefer.ui.main

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenstateApi {

    @GET("bills?include=votes&per_page=20")
    suspend fun getBills(
        @Query("apikey") apikey: String,
        @Query("jurisdiction") jurisdiction: String,
        @Query("sort") sort: String,
        @Query("page") page: String
    ): BillSearch

    @GET("people")
    suspend fun getPerson(
        @Query("apikey") apikey: String,
        @Query("id") id: String
    ): PersonSearch

    companion object {
        val TAG = "BillBriefer"
        val constants = Constants()
        // Leave this as a simple, base URL.  That way, we can have many different
        // functions (above) that access different "paths" on this server
        // https://square.github.io/okhttp/4.x/okhttp/okhttp3/-http-url/
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("v3.openstates.org")
            .build()

        // Public create function that ties together building the base
        // URL and the private create function that initializes Retrofit
        fun create(): OpenstateApi = create(url)
        private fun create(httpUrl: HttpUrl): OpenstateApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(Interceptor() {
                    val original = it.request()
                    val request = original.newBuilder()
                        .method(original.method, original.body)
                        .build();

                    var response = it.proceed(request)
                    if (response.code.toString() != "200") { // 403: needs key; 401; wrong key
                        Log.d(TAG, "Error Detected\n${response}")
                        response = Response.Builder()
                            .code(200)
                            .protocol(Protocol.HTTP_2)
                            .request(request)
                            .message("")
                            .body(
                                ("{\"return_code\": \"${constants.REQUEST_ERROR}\"}").toResponseBody("application/json".toMediaTypeOrNull())
                            )
                            .build()
                    }
                    response
                })
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenstateApi::class.java)
        }
    }
}