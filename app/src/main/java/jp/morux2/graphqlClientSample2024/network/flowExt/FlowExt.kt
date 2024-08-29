package jp.morux2.graphqlClientSample2024.network.flowExt

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Operation
import jp.morux2.graphqlClientSample2024.network.GraphQlNoDataException
import jp.morux2.graphqlClientSample2024.network.GraphQlServerException
import jp.morux2.graphqlClientSample2024.network.NoNetworkException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import timber.log.Timber
import kotlin.math.pow

sealed class Lce<out T> {
    data object Loading : Lce<Nothing>()
    data class Content<T>(val data: T) : Lce<T>()
    data class Error(val throwable: Throwable) : Lce<Nothing>()

    val isLoading
        get() = this is Loading

    fun getDataIfContent() = (this as? Content)?.data
    fun getThrowableIfError() = (this as? Error)?.throwable
}

fun <T> Flow<T>.toLce() = map<T, Lce<T>> {
    Lce.Content(it)
}.onStart {
    emit(Lce.Loading)
}.catch {
    emit(Lce.Error(it))
}

fun <T : Operation.Data> ApolloCall<T>.toThrowableFlow() = toFlow().map { response ->
    response.errors?.let {
        throw GraphQlServerException(it)
    } ?: run {
        response.data ?: run {
            val exception = GraphQlNoDataException()
            throw exception
        }
    }
}.retryWhen { cause, attempt ->
    if (cause is NoNetworkException || attempt >= 5) return@retryWhen false
    // exponential backoff でリトライ間隔を徐々に伸ばす
    val delayMillis = (300 * 2.0.pow(attempt.toDouble()) * Math.random()).toLong()
    delay(delayMillis)
    Timber.d("retryCount: ${attempt + 1}, delayMills: $delayMillis")
    true
}

