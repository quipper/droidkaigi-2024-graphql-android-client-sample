package jp.morux2.graphqlClientSample2024

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jp.morux2.graphqlClientSample2024.compose.dummyContent
import jp.morux2.graphqlClientSample2024.network.GraphQlServerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters", "TestFunctionName")
class MainViewModelTest {

    private fun viewModelFactory(apolloWrapper: MainApolloWrapper = mockk()): MainViewModel {
        return MainViewModel(
            apolloWrapper = apolloWrapper
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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
            apolloWrapper = mockk {
                every { fetchMainScreenData() } returns flowOf(dummyContent)
            }
        )

        viewModel.fetchContent()

        viewModel.viewState.value shouldBe MainViewModel.ViewState(
            isLoading = false,
            throwable = null,
            content = dummyContent,
        )
    }

    @Test
    fun エラーの場合はダイアログを表示すること() {
        val exception = GraphQlServerException(listOf(mockk(relaxed = true)))
        val viewModel = viewModelFactory(
            apolloWrapper = mockk {
                every { fetchMainScreenData() } returns flow { throw exception }
            }
        )

        viewModel.fetchContent()

        viewModel.viewState.value shouldBe MainViewModel.ViewState(
            isLoading = false,
            throwable = exception,
            content = null,
        )
    }
}