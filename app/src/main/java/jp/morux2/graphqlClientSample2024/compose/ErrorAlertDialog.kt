package jp.morux2.graphqlClientSample2024.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jp.morux2.graphqlClientSample2024.network.GraphQlNoDataException
import jp.morux2.graphqlClientSample2024.network.GraphQlServerException
import jp.morux2.graphqlClientSample2024.network.NoNetworkException

@Composable
fun ErrorAlertDialog(
    throwable: Throwable,
    refech: () -> Unit,
    dismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    dismiss()
                    refech()
                }
            ) {
                Text("Retry")
            }
        },
        title = {
            Text(
                when (throwable) {
                    is GraphQlServerException, is GraphQlNoDataException -> "エラーが発生しました"
                    is NoNetworkException -> "ネットワーク接続がありません"
                    else -> "サーバーに接続されていません" // ApolloNetworkException (npm start していない場合)
                }
            )
        },
        text = {
            Text("通信をやり直しますか？")
        },
    )
}

class ThrowableProvider : PreviewParameterProvider<Throwable> {
    override val values = sequenceOf(
        NoNetworkException(),
        GraphQlServerException(errors = listOf()),
        Throwable()
    )
}

@Preview
@Composable
private fun ErrorAlertDialogPreview(
    @PreviewParameter(ThrowableProvider::class) throwable: Throwable
) {
    ErrorAlertDialog(
        throwable = throwable,
        refech = {},
        dismiss = {}
    )
}