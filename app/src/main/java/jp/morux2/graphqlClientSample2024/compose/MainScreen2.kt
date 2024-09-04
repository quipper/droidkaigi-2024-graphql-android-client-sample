package jp.morux2.graphqlClientSample2024.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.compose.exception
import com.apollographql.apollo3.compose.toState
import jp.morux2.graphqlClientSample2024.MainScreenQuery
import jp.morux2.graphqlClientSample2024.network.GraphQlServerException
import java.util.UUID

// Apollo の toState 関数を用いる場合のサンプル
@OptIn(ApolloExperimental::class)
@Composable
fun MainScreen2(
    apolloClient: ApolloClient,
) {
    val mainScreenState by apolloClient.query(MainScreenQuery()).toState()

    MainScreenContent2(
        modifier = Modifier.fillMaxSize(),
        mainScreenState = mainScreenState,
    )
}

@OptIn(ApolloExperimental::class)
@Composable
fun MainScreenContent2(
    mainScreenState: ApolloResponse<MainScreenQuery.Data>?,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { contentPadding ->
        mainScreenState?.data?.let { content ->
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content.posts.forEach { item ->
                    item {
                        PostCard(
                            modifier = Modifier.fillParentMaxWidth(),
                            postCardFragment = item.postCardFragment,
                        )
                    }
                }
            }
        }
        // note : https://github.com/apollographql/apollo-kotlin/issues/5019
        // 3系では exception をキャッチできない (NoNetworkException)
        if (mainScreenState?.hasErrors() == true || mainScreenState?.exception != null) {
            ErrorAlertDialog(
                throwable = mainScreenState.exception?.cause
                    ?: GraphQlServerException(errors = mainScreenState.errors!!),
                refech = {},
            )
        }
        if (mainScreenState == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainScreen2Preview() {
    MainScreenContent2(
        modifier = Modifier.fillMaxSize(),
        mainScreenState = ApolloResponse.Builder(
            requestUuid = UUID.randomUUID(),
            operation = MainScreenQuery(),
            data = dummyContent
        ).errors(null).build(),
    )
}