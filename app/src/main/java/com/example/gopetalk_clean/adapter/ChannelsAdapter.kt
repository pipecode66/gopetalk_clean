package com.example.gopetalk_clean.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gopetalk_clean.R

class ChannelsAdapter(
    private var channels: List<String>,
    private val onChannelClick: (String) -> Unit
) : RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder>() {

    inner class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val channelNameText: TextView = itemView.findViewById(R.id.text_channel)

        fun bind(channel: String) {
            channelNameText.text = channel
            itemView.setOnClickListener { onChannelClick(channel) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size

    fun updateData(newChannels: List<String>) {
        channels = newChannels
        notifyDataSetChanged()
    }
}
