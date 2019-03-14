package com.ravi.android.face.detection.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ravi.android.face.detection.R
import com.ravi.android.face.detection.domain.model.Features


class FeatureAdapter(private var featuresList: ArrayList<Features>) : BaseAdapter() {

    override fun getView(index: Int, view: View?, parent: ViewGroup?): View {
        val layout: View
        val viewHolder: ViewHolder

        val item = featuresList[index]
        if (view == null) {
            val inflater = LayoutInflater.from(parent!!.context)
            layout = inflater.inflate(R.layout.item_list_view, parent, false)
            viewHolder = ViewHolder()
            viewHolder.txtId = layout?.findViewById(R.id.txt_itemId)!!
            viewHolder.txtSmile = layout.findViewById(R.id.txt_smile_prob)!!
            viewHolder.txtRightEyeOpenProb = layout.findViewById(R.id.txt_right_eye_open_prob)!!
            viewHolder.txtLeftEyeOpenProb = layout.findViewById(R.id.txt_left_eye_open_prob)!!

            viewHolder.txtLabelId = layout.findViewById(R.id.txt_label_face_id)

            layout.tag = viewHolder
        } else {
            layout = view
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.txtId.text = item.trackingId
        viewHolder.txtSmile.text = item.smileProb
        viewHolder.txtRightEyeOpenProb.text = item.rightEyeOpenProb
        viewHolder.txtLeftEyeOpenProb.text = item.leftEyeOpenProb.toString()

        viewHolder.txtId.setTextColor(item.color)
        viewHolder.txtLabelId.setTextColor(item.color)

        return layout
    }

    override fun getItem(p0: Int): Any = featuresList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = featuresList.size

    private class ViewHolder {
        lateinit var txtId: TextView
        lateinit var txtSmile: TextView
        lateinit var txtRightEyeOpenProb: TextView
        lateinit var txtLeftEyeOpenProb: TextView
        lateinit var txtLabelId: TextView
    }

}

