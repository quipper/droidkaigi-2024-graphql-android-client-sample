package jp.morux2.graphqlClientSample2024

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.morux2.graphqlClientSample2024.network.flowExt.toLce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apolloWrapper: MainApolloWrapper
) : ViewModel() {
    private val _viewState = MutableStateFlow(ViewState.INITIAL)
    val viewState = _viewState.asStateFlow()

    fun fetchContent() {
        apolloWrapper.fetchMainScreenData().toLce().onEach { lce ->
            _viewState.update {
                it.copy(
                    isLoading = lce.isLoading,
                    throwable = lce.getThrowableIfError(),
                    content = lce.getDataIfContent()
                )
            }
        }.launchIn(viewModelScope)
    }

    data class ViewState(
        val isLoading: Boolean,
        val throwable: Throwable?,
        val content: MainScreenQuery.Data?,
    ) {
        companion object {

            val INITIAL = ViewState(
                isLoading = false,
                throwable = null,
                content = null
            )
        }
    }
}