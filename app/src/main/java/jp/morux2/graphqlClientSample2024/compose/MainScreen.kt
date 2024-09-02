package jp.morux2.graphqlClientSample2024.compose

import androidx.annotation.VisibleForTesting
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jp.morux2.graphqlClientSample2024.MainScreenQuery
import jp.morux2.graphqlClientSample2024.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.fetchContent()
    }

    MainScreenContent(
        modifier = Modifier.fillMaxSize(),
        viewState = viewState,
        refetch = viewModel::fetchContent,
    )
}

@Composable
fun MainScreenContent(
    viewState: MainViewModel.ViewState,
    refetch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { contentPadding ->
        viewState.content?.let { content ->
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
        if (viewState.throwable != null) {
            ErrorAlertDialog(
                throwable = viewState.throwable,
                refech = refetch,
            )
        }
        if (viewState.isLoading) {
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
private fun MainScreenPreview() {
    MainScreenContent(
        modifier = Modifier.fillMaxSize(),
        viewState = MainViewModel.ViewState(
            isLoading = false,
            throwable = null,
            content = dummyContent
        ),
        refetch = {},
    )
}

@VisibleForTesting
val dummyContent = MainScreenQuery.Data(
    posts = List(5) {
        MainScreenQuery.Post(
            __typename = "",
            postCardFragment = dummyPostCardFragment
        )
    }
)