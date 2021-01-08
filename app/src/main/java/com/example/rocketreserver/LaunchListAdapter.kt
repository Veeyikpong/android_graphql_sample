package com.example.rocketreserver

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.example.rocketreserver.databinding.LaunchItemBinding

class LaunchListAdapter(
    val launchList: List<LaunchListQuery.Launch>
) :
    RecyclerView.Adapter<LaunchListAdapter.ViewHolder>() {

    var onEndOfListReached: (() -> Unit)? = null
    var onItemClicked: ((LaunchListQuery.Launch) -> Unit)? = null

    class ViewHolder(val binding: LaunchItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return launchList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LaunchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val launch = launchList[position]

        with(holder) {
            binding.site.text = launch.site
            binding.missionName.text = launch.mission?.name
            binding.missionPatch.load(launch.mission?.missionPatch) {
                placeholder(R.drawable.ic_placeholder)
            }
            binding.root.setOnClickListener {
                onItemClicked?.invoke(launch)
            }
        }

        if (position == launchList.size - 1) {
            onEndOfListReached?.invoke()
        }
    }
}