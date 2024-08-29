package jp.morux2.graphqlClientSample2024

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import io.mockk.every
import io.mockk.mockk
import jp.morux2.graphqlClientSample2024.compose.dummyContent
import jp.morux2.graphqlClientSample2024.network.GraphQlServerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// ApolloClient を mock した場合
@Suppress("NonAsciiCharacters", "TestFunctionName")
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest2 {

    private fun viewModelFactory(apolloClient: ApolloClient = mockk()): MainViewModel {
        return MainViewModel(
            apolloWrapper = MainApolloWrapper(apolloClient)
        )
    }

    @BeforeEach
    fun setup() {
        val dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun 初期状態() {
        val viewModel = viewModelFactory()

        viewModel.viewState.value shouldBe MainViewModel.ViewState(
            isLoading = false,
            throwable = null,
            content = null,
        )
    }

    @Test
    fun 画面が描画できること() = runTest {
        val viewModel = viewModelFactory(
            apolloClient = mockk {
                every { query(MainScreenQuery()).toFlow() } returns flowOf(
                    ApolloResponse.Builder(
                        requestUuid = mockk(),
                        operation = mockk<MainScreenQuery>(),
                        data = dummyContent
                    ).errors(null).build()
                )
            }
        )

        viewModel.fetchContent()
        advanceUntilIdle()

        viewModel.viewState.value shouldBe MainViewModel.ViewState(
            isLoading = false,
            throwable = null,
            content = dummyContent,
        )
    }

    @Test
    fun エラーの場合はダイアログを表示すること() = runTest {
        val viewModel = viewModelFactory(
            apolloClient = mockk {
                every { query(MainScreenQuery()).toFlow() } returns flowOf(
                    ApolloResponse.Builder(
                        requestUuid = mockk(),
                        operation = mockk<MainScreenQuery>(),
                        data = null,
                    ).errors(listOf(mockk(relaxed = true))).build()
                )
            }
        )

        viewModel.fetchContent()
        advanceUntilIdle()

        viewModel.viewState.value.throwable shouldBe instanceOf<GraphQlServerException>()
    }
}


