package com.example.rocketreserver

import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloClient
import org.koin.android.ext.android.inject

open class BaseFragment : Fragment() {
    val apolloClient by inject<ApolloClient>()
}