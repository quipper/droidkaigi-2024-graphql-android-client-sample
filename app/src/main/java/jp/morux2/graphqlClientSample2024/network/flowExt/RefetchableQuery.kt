package jp.morux2.graphqlClientSample2024.network.flowExt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach

// 拡張関数を生やすことで, retry ブロックの囲み忘れを防ぐ
@Composable
fun <T : Query.Data> ApolloClient.refetchableQuery(query: Query<T>): RefetchableQuery<T> {
    // point : recomposition のたびに, RefetchableQuery (refechTrigger) が生成されてしまい意図しない挙動になる
    return remember {
        RefetchableQuery(
            apolloClient = this,
            query = query
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
data class RefetchableQuery<T : Query.Data>(
    val apolloClient: ApolloClient,
    val query: Query<T>,
) {
    // 購読開始前に値が流れてしまうケースがあるので, replay を 1 に設定する
    private val refechTrigger = MutableSharedFlow<Unit>(replay = 1)

    @Composable
    fun toLce(): State<Lce<T>?> {
        val responseFlow = remember {
            refechTrigger.flatMapLatest {
                apolloClient.query(query = query)
                    .toThrowableFlow()
                    .onEach {
                        // refechTrigger 契機で Flow が一度発火したら, 以降は再発火しないようにする
                        refechTrigger.resetReplayCache()
                    }
                    .toLce()
            }
        }
        // 初期値を null にしておくことで, toLce 呼び出し時点でローディングが流れてしまうことを避ける
        return responseFlow.collectAsStateWithLifecycle(initialValue = null)
    }

    suspend fun load() {
        refechTrigger.emit(Unit)
    }
}

