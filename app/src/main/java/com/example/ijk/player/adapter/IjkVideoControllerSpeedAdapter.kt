package com.example.ijk.player.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ijk.player.R
import com.example.ijk.player.ui.view.IjkVideoControllerSpeedView

/**
 * @author YangJ
 */
class IjkVideoControllerSpeedAdapter(val context: Context, private val list: ArrayList<Float>) :
    RecyclerView.Adapter<IjkVideoControllerSpeedAdapter.SpeedViewHolder>() {

    private val mInflater = LayoutInflater.from(context)

    // Listener
    private var mListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeedViewHolder {
        val view = this.mInflater.inflate(R.layout.item_ijk_video_controller_speed, parent, false)
        return SpeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpeedViewHolder, position: Int) {
        val item = list[position]
        holder.mTvSpeed.text = convert(item)
        holder.itemView.setOnClickListener {
            this.mListener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun convert(float: Float): String {
        val value = if (IjkVideoControllerSpeedView.SPEED_0_7_5_X == float) {
            String.format("%.2f", float)
        } else {
            String.format("%.1f", float)
        }
        return context.getString(R.string.video_speed_x, value)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(value: Float)
    }

    class SpeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvSpeed: TextView = itemView.findViewById(R.id.tv_speed)
    }
}