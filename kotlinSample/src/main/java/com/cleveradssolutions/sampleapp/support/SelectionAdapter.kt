package com.cleveradssolutions.sampleapp.support

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleveradssolutions.sampleapp.R

class SelectionAdapter : RecyclerView.Adapter<SelectionAdapter.Holder>() {

    private val items = AdContainers.entries

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.selection_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.textView.run {
            setText(item.titleResId)
            setOnClickListener { view ->
                val context = view.context
                context.startActivity(
                    Intent(context, item.activity.java)
                )
            }
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView as TextView
    }

}
