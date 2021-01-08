package com.example.rocketreserver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toFlow
import com.example.rocketreserver.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val apolloClient by inject<ApolloClient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        apolloClient.subscribe(TripsBookedSubscription()).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }
            .onEach {
                val trips = it.data?.tripsBooked
                val text = when {
                    trips == null -> getString(R.string.subscriptionError)
                    trips == -1 -> getString(R.string.tripCancelled)
                    else -> getString(R.string.tripBooked, trips)
                }
                Snackbar.make(
                    findViewById(R.id.main_frame_layout),
                    text,
                    Snackbar.LENGTH_LONG
                ).show()
            }
            .launchIn(lifecycleScope)
    }
}
