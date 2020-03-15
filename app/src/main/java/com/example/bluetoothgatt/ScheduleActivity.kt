package com.example.bluetoothgatt

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_schedule.*
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private lateinit var adapter: RecyclerView.Adapter<Holder>
    private val mSchedules = ArrayList<TimeItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        //fixme test
        mSchedules.add(TimeItem("00:00:11", "tomorrow", true))
        mSchedules.add(TimeItem("00:11:11", "today", false))
        mSchedules.add(TimeItem("11:00:11", "one", true))
        mSchedules.add(TimeItem("11:11:11", "two", true))
        mSchedules.add(TimeItem("22:00:11", "three", false))


        adapter = Adapter(mSchedules, this)
        schedule_rvList.adapter = adapter
        schedule_rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}

class TimeItem(
    var dataTime: String = "00",
    var tips: String = "",
    var isOpen: Boolean = false
) {

}

class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTime: TextView = itemView.findViewById(R.id.item_tvTime)
    val tvTips: TextView = itemView.findViewById(R.id.item_tvTips)
    val swOpen: Switch = itemView.findViewById(R.id.item_swOpen)
}

class Adapter(private val mSchedules: ArrayList<TimeItem>, val context: Context) :
    RecyclerView.Adapter<Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_item_time, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = mSchedules.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = mSchedules[position]

        if (item.isOpen xor holder.swOpen.isEnabled) holder.swOpen.toggle()
        holder.swOpen.setOnCheckedChangeListener { _, isChecked -> item.isOpen = isChecked }
        holder.tvTime.text = item.dataTime
        holder.tvTips.text = item.tips
    }

}