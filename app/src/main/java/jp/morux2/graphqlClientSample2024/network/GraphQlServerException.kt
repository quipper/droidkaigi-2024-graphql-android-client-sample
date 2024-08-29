package jp.morux2.graphqlClientSample2024.network

import com.apollographql.apollo3.api.Error

class GraphQlServerException(errors: List<Error>) : Exception(errors.joinToString { it.message })