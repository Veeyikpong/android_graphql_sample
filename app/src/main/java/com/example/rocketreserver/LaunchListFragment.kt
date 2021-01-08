package com.example.rocketreserver

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.example.rocketreserver.databinding.LaunchListFragmentBinding
import kotlinx.coroutines.channels.Channel
import java.lang.Exception

class LaunchListFragment : BaseFragment() {
    private lateinit var binding: LaunchListFragmentBinding
    val launches = mutableListOf<LaunchListQuery.Launch>()
    val channel = Channel<Unit>(Channel.CONFLATED)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LaunchListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LaunchListAdapter(launches)
        binding.launches.layoutManager = LinearLayoutManager(requireContext())
        binding.launches.adapter = adapter



        // offer a first item to do the initial load else the list will stay empty forever
        channel.offer(Unit)
        adapter.onEndOfListReached = {
            channel.offer(Unit)
        }
        adapter.onItemClicked={
            findNavController().navigate(
                LaunchListFragmentDirections.openLaunchDetails(launchId = it.id)
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            var cursor: String? = null
            for(item in channel) {
                val response = try {
                    apolloClient.query(LaunchListQuery(cursor = Input.fromNullable(cursor))).await()
                } catch (e: Exception) {
                    Log.d("LaunchList", "Failure", e)
                    return@launchWhenResumed
                }

                val newLaunches = response.data?.launches?.launches?.filterNotNull()
                if (newLaunches != null) {
                    launches.addAll(newLaunches)
                    adapter.notifyDataSetChanged()
                }

                cursor = response.data?.launches?.cursor
                if (response.data?.launches?.hasMore != true) {
                    break
                }
            }

            adapter.onEndOfListReached = null
            channel.close()
        }
    }
}