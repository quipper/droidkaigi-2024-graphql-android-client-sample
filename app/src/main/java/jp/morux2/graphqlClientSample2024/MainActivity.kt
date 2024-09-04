package jp.morux2.graphqlClientSample2024

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.AndroidEntryPoint
import jp.morux2.graphqlClientSample2024.compose.MainScreen2
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var apolloClient: ApolloClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen2(
                apolloClient = apolloClient
            )
        }
    }
}