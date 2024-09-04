package jp.morux2.graphqlClientSample2024.network

import android.content.Context
import android.net.ConnectivityManager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.okHttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber


object ApolloClientFactory {

    private const val BASE_URL = "http://10.0.2.2:4000/"

    fun from(context: Context): ApolloClient {
        return ApolloClient.Builder()
            .okHttpClient(
                OkHttpClient().newBuilder().authenticator(MyAuthenticator()).build()
            )
            .serverUrl(BASE_URL)
            .addInterceptor(ApolloNetworkConnectivityInterceptor(context))
            .build()
    }
}

class MyAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 本来はここでログアウト処理を実施する
        Timber.d("response: $response")
        return null
    }
}

class ApolloNetworkConnectivityInterceptor(private val context: Context) : ApolloInterceptor {
    override fun <D : Operation.Data> intercept(
        request: ApolloRequest<D>,
        chain: ApolloInterceptorChain
    ): Flow<ApolloResponse<D>> {
        return if (context.isNetworkConnected()) {
            chain.proceed(request)
        } else {
            // https://github.com/apollographql/apollo-kotlin/issues/4904#issuecomment-1523178542
            flow { throw NoNetworkException() }
        }
    }

    private fun Context.isNetworkConnected(): Boolean =
        getSystemService(ConnectivityManager::class.java)?.activeNetwork != null
}
