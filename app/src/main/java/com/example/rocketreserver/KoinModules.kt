package com.example.rocketreserver

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.example.rocketreserver.AuthInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val apolloModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(androidContext()))
            .build()
    }

    single {
        ApolloClient.builder()
            .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com")
            .okHttpClient(get())
            .subscriptionTransportFactory(
                WebSocketSubscriptionTransport.Factory(
                    "wss://apollo-fullstack-tutorial.herokuapp.com/graphql",
                    get<OkHttpClient>()
                )
            )
            .build()
    }
}