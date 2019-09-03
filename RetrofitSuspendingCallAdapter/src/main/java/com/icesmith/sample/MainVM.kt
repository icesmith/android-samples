package com.icesmith.sample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

data class TestResponse(
    @SerializedName("slideshow")
    val slideshow: Any
)

interface Service {
    @GET("json")
    @Headers("Cache-Control: no-cache")
    suspend fun test(): Result<TestResponse>
}

sealed class Result<out T> {
    data class Success<T>(val data: T?) : Result<T>()
    data class Failure(val statusCode: Int?) : Result<Nothing>()
    object NetworkError : Result<Nothing>()
}

abstract class CallDelegate<TIn, TOut>(
    protected val proxy: Call<TIn>
) : Call<TOut> {
    override fun execute(): Response<TOut> = throw NotImplementedError()
    override fun enqueue(callback: Callback<TOut>): Unit = throw NotImplementedError()
    override fun clone(): Call<TOut> = throw NotImplementedError()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled
}

class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, Result<T>>(proxy) {
    override fun enqueue(callback: Callback<Result<T>>) {
        proxy.enqueue(object: Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val code = response.code()
                val result = if (code in 200 until 300) {
                    val body = response.body()
                    Result.Success(body)
                } else {
                    Result.Failure(code)
                }

                callback.onResponse(this@ResultCall, Response.success(result))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val result = if (t is IOException) {
                    Result.NetworkError
                } else {
                    Result.Failure(null)
                }

                callback.onResponse(this@ResultCall, Response.success(result))
            }
        })
    }
}

class ResultAdapter<T>(
    private val clazz: Class<T>
): CallAdapter<T, Call<Result<T>>> {
    override fun responseType() = clazz
    override fun adapt(call: Call<T>): Call<Result<T>> = ResultCall(call)
}

class MyCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                Result::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    ResultAdapter(getRawType(resultType))
                }
                else -> null
            }
        }
        else -> null
    }
}

/**
 * https://stackoverflow.com/q/56483235/4858777
 * https://stackoverflow.com/q/57625272/4858777
 */
class MainVM : ViewModel() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://httpbin.org/")
        .addCallAdapterFactory(MyCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(Service::class.java)

    fun executeRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = service.test()
            Log.i("test", result.toString())
        }
    }
}
