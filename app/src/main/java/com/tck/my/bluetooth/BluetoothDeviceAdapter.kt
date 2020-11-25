package com.tck.my.bluetooth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 *<p>description:</p>
 *<p>created on: 2020/11/25 13:08</p>
 * @author tck
 * @version v3.7.5
 *
 */
class BluetoothDeviceAdapter(
    private val context: Context,
    private val dataList: List<BluetoothDeviceModel>
) :
    RecyclerView.Adapter<BluetoothDeviceViewHolder>() {

    var clickListener: ((pos: Int, type: String) -> Unit)? = null

    fun setOnItemClickListener(clickListener: ((pos: Int, type: String) -> Unit)) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        return BluetoothDeviceViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_bluetooth_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        val data = dataList[position]
        holder.tv_device_name.text = data.name
        holder.tv_device_address.text = data.address

        holder.itemView.setOnClickListener {
            clickListener?.invoke(position, "")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


class BluetoothDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tv_device_name = itemView.findViewById<TextView>(R.id.tv_device_name)
    val tv_device_address = itemView.findViewById<TextView>(R.id.tv_device_address)
}