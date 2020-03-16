package com.example.bluetoothgatt

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_edit_schedule.*
import kotlinx.android.synthetic.main.activity_schedule.*
import java.util.*

class EditScheduleActivity : AppCompatActivity() {

    private lateinit var adapter: Adapter
    private val mItems = ArrayList<ItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule)

        mItems.add(ItemData("Time", "00:00", true))
        mItems.add(ItemData("Window", "up", true))
        mItems.add(ItemData("Led", "1", true))
        mItems.add(ItemData("Led flash", "0", true))
        mItems.add(ItemData("Led Fading", "0", true))
        mItems.add(ItemData("Led Lum", "25", true))

        adapter = Adapter(mItems, this)
        edit_schedule_rvList.adapter = adapter
        edit_schedule_rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.run {
            title = "Edit Schedule"
        }
        return super.onCreateOptionsMenu(menu)
    }

    data class ItemData(var title: String, var content: String, var enable: Boolean)

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.item_edit_tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.item_edit_tvContent)
        val swEnable: Switch = itemView.findViewById(R.id.item_edit_swEnable)
    }

    class Adapter(private val mItems: ArrayList<ItemData>, private val context: Context) :
        RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.sample_item_edit, parent, false)
            return Holder(view)
        }

        override fun getItemCount(): Int = mItems.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = mItems[position]

            holder.run {
                if (swEnable.isEnabled xor item.enable) swEnable.toggle()
                tvTitle.text = item.title
                tvContent.text = item.content
            }
        }

    }
}
