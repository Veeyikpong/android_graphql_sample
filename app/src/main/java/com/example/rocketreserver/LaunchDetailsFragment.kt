package com.example.rocketreserver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.rocketreserver.*
import com.example.rocketreserver.databinding.LaunchDetailsFragmentBinding

class LaunchDetailsFragment : BaseFragment() {

    private lateinit var binding: LaunchDetailsFragmentBinding
    val args: LaunchDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LaunchDetailsFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            binding.bookButton.isVisible = false
            binding.bookProgressBar.isVisible = false
            binding.progressBar.isVisible = true
            binding.error.isVisible = false

            val response = try {
                apolloClient.query(LaunchDetailsQuery(id = args.launchId)).await()
            } catch (e: ApolloException) {
                binding.progressBar.isVisible = false
                binding.error.text = "Oh no... A protocol error happened"
                binding.error.isVisible = true
                return@launchWhenResumed
            }

            val launch = response.data?.launch
            if (launch == null || response.hasErrors()) {
                binding.progressBar.isVisible = false
                binding.error.text = response.errors?.get(0)?.message
                binding.error.isVisible = true
                return@launchWhenResumed
            }

            binding.progressBar.visibility = View.GONE

            binding.missionPatch.load(launch.mission?.missionPatch) {
                placeholder(R.drawable.ic_placeholder)
            }
            binding.site.text = launch.site
            binding.missionName.text = launch.mission?.name
            val rocket = launch.rocket
            binding.rocketName.text = "🚀 ${rocket?.name} ${rocket?.type}"

            configureButton(launch.isBooked)
        }
    }

    private fun configureButton(isBooked: Boolean) {
        binding.bookButton.isVisible = true
        binding.bookProgressBar.isVisible = false

        binding.bookButton.text = if (isBooked) {
            getString(R.string.cancel)
        } else {
            getString(R.string.book_now)
        }

        binding.bookButton.setOnClickListener {
            val context = context
            if (context != null && User.getToken(context) == null) {
                findNavController().navigate(
                    R.id.open_login
                )
                return@setOnClickListener
            }

            binding.bookButton.isVisible = false
            binding.bookProgressBar.isVisible = true
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                val mutation = if (isBooked) {
                    CancelTripMutation(id = args.launchId)
                } else {
                    BookTripMutation(id = args.launchId)
                }

                val response = try {
                    apolloClient.mutate(mutation).await()
                } catch (e: ApolloException) {
                    configureButton(isBooked)
                    return@launchWhenResumed
                }

                if (response.hasErrors()) {
                    configureButton(isBooked)
                    return@launchWhenResumed
                }

                configureButton(!isBooked)
            }
        }
    }
}