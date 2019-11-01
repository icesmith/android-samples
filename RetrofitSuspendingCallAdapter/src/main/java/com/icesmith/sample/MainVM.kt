package com.icesmith.sample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

data class Bar(
    @SerializedName("foo")
    val foo: String
)

interface Service {
    @GET("bar")
    suspend fun getBar(): Result<Bar>

    @GET("bars")
    suspend fun getBars(): Result<List<Bar>>
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
    final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
    final override fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)
    abstract fun cloneImpl(): Call<TOut>
}

class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, Result<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<Result<T>>) = proxy.enqueue(object: Callback<T> {
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

    override fun cloneImpl() = ResultCall(proxy.clone())
}

class ResultAdapter(
    private val type: Type
): CallAdapter<Type, Call<Result<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)
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
                    ResultAdapter(resultType)
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
    private val service: Service

    init {
        val mockInterceptor = MockInterceptor()
        val mockClient = OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://mock.com/")
            .client(mockClient)
            .addCallAdapterFactory(MyCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(Service::class.java)
    }


    fun executeRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            val bar = service.getBar()
            Log.i("test", bar.toString())

            val bars = service.getBars()
            Log.i("test", bars.toString())
        }
    }
}

/**
 * A Mock interceptor that returns a test data
 */
class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val response = when (chain.request().url().encodedPath()) {
            "/bar" -> """{"foo":"baz"}"""
            "/bars" -> """[{"foo":"baz1"},{"foo":"baz2"}]"""
            else -> throw Error("unknown request")
        }

        val mediaType = MediaType.parse("application/json")
        val responseBody = ResponseBody.create(mediaType, response)

        return okhttp3.Response.Builder()
            .protocol(Protocol.HTTP_1_0)
            .request(chain.request())
            .code(200)
            .message("")
            .body(responseBody)
            .build()
    }
}
