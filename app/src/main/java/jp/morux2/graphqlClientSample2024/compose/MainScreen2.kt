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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.apollographql.apollo3.ApolloClient
import jp.morux2.graphqlClientSample2024.MainScreenQuery
import jp.morux2.graphqlClientSample2024.network.flowExt.Lce
import jp.morux2.graphqlClientSample2024.network.flowExt.refetchableQuery
import kotlinx.coroutines.launch

// 自前の RefetchableQuery を用いる場合のサンプル
@Composable
fun MainScreen2(
    apolloClient: ApolloClient,
) {
    val coroutineScope = rememberCoroutineScope()
    val mainScreenQuery = apolloClient.refetchableQuery(query = MainScreenQuery())
    val mainLce by mainScreenQuery.toLce()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        coroutineScope.launch {
            mainScreenQuery.load()
        }
    }
    mainLce?.let {
        MainScreenContent2(
            modifier = Modifier.fillMaxSize(),
            lce = it,
            refetch = {
                coroutineScope.launch {
                    mainScreenQuery.load()
                }
            },
        )
    }
}

@Composable
fun MainScreenContent2(
    lce: Lce<MainScreenQuery.Data>,
    refetch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { contentPadding ->
        lce.getDataIfContent()?.let { content ->
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
        lce.getThrowableIfError()?.let { throwable ->
            ErrorAlertDialog(
                throwable = throwable,
                refech = refetch,
            )
        }

        if (lce.isLoading) {
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
        lce = Lce.Content(
            data = dummyContent
        ),
        refetch = {},
    )
}