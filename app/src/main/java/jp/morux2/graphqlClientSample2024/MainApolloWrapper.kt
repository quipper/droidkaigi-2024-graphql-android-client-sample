package jp.morux2.graphqlClientSample2024

import com.apollographql.apollo3.ApolloClient
import jp.morux2.graphqlClientSample2024.network.flowExt.toThrowableFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainApolloWrapper @Inject constructor(
    private val apolloClient: ApolloClient
) {
    fun fetchMainScreenData(): Flow<MainScreenQuery.Data> {
        return apolloClient.query(MainScreenQuery())
            .toThrowableFlow()
    }

    // ↓ 理想形ではないスキーマの場合
    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchMainScreenData2(): Flow<List<Pair<PostQuery.Post, AuthorQuery.Author>>> {
        return fetchPosts().flatMapLatest { posts ->
            val postFlows = posts.map { post ->
                // post に対応する author の情報を取得する
                fetchAuthor(authorId = post.author.id).map { author ->
                    author?.let {
                        Pair(post, it)
                    }
                }
            }
            combine(postFlows) {
                it.toList().filterNotNull()
            }
        }
    }

    private fun fetchPosts(): Flow<List<PostQuery.Post>> {
        return apolloClient.query(PostQuery())
            .toThrowableFlow()
            .map { it.posts }
    }

    private fun fetchAuthor(authorId: String): Flow<AuthorQuery.Author?> {
        return apolloClient.query(AuthorQuery(authorId))
            .toThrowableFlow()
            .map { it.author }
    }
}

