// HistoryAdapter.kt
package com.dicoding.fruiteasy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HistoryAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATE_HEADER = 0
        private const val VIEW_TYPE_HISTORY_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DateHeader -> VIEW_TYPE_DATE_HEADER
            is HistoryItem -> VIEW_TYPE_HISTORY_ITEM
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            VIEW_TYPE_HISTORY_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
                HistoryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(items[position] as DateHeader, getItemCountInSection(position))
            is HistoryViewHolder -> holder.bind(items[position] as HistoryItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItemCountInSection(position: Int): Int {
        var count = 0
        for (i in position + 1 until items.size) {
            if (items[i] is DateHeader) break
            count++
        }
        return count
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val dottedLine: View = itemView.findViewById(R.id.dottedLine)

        fun bind(dateHeader: DateHeader, itemCount: Int) {
            tvDate.text = dateHeader.date
            val params = dottedLine.layoutParams
            val totalHeight = itemCount * itemView.resources.getDimensionPixelSize(R.dimen.history_item_height)
            params.height = totalHeight
            dottedLine.layoutParams = params
        }
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(historyItem: HistoryItem) {
            tvTitle.text = historyItem.title
            tvSubtitle.text = historyItem.subtitle
            Glide.with(itemView.context)
                .load(historyItem.imageUrl)
                .into(imageView)
        }
    }
}
