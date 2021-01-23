package com.example.puyo_base_simulator.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.puyo_base_simulator.R
import com.example.puyo_base_simulator.ui.home.LoadFieldAdapter.FieldViewHolder
import java.util.*
import kotlin.text.*

class LoadFieldAdapter(private val mFields: List<FieldPreview>) : RecyclerView.Adapter<FieldViewHolder>() {
    private lateinit var mFieldSelectedListener: (Int, FieldPreview) -> Unit
    fun setFieldSelectedListener(fieldSelectedListener:(Int, FieldPreview) -> Unit) {
        mFieldSelectedListener = fieldSelectedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_field, parent, false))
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        val fieldPreview = mFields[position]
        holder.fieldID.text =   String.format(Locale.JAPAN, "seed: %d",fieldPreview.seed)
        holder.fieldCanvas.setField(fieldPreview.content)
        holder.itemView.setOnClickListener {
            mFieldSelectedListener.invoke(position, fieldPreview)
        }
    }

    override fun getItemCount(): Int {
        return mFields.size
    }

    class FieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fieldID: TextView = itemView.findViewById(R.id.fieldID)
        var fieldCanvas: FieldPreviewCanvas = itemView.findViewById(R.id.fieldCanvas)
    }
}