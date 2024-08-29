package jp.morux2.graphqlClientSample2024.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.morux2.graphqlClientSample2024.network.ApolloClientFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApolloClientModule {

    @Singleton
    @Provides
    fun provideApolloClient(
        @ApplicationContext context: Context,
    ): ApolloClient {
        return ApolloClientFactory.from(context)
    }
}