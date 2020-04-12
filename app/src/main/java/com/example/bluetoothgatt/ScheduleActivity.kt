package com.example.bluetoothgatt

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_schedule.*
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private lateinit var adapter: RecyclerView.Adapter<Holder>
    private val mSchedules = ArrayList<TimeItem>()
    lateinit var pendingIntent: PendingIntent
    lateinit var alarm: AlarmManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        //显示返回按钮
        supportActionBar?.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

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

        //添加计划按钮被单击
        schedule_fabAdd.setOnClickListener {

        }


//        alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager//todo add alarm function
////        val intent = Intent(this, TimerReceiver::class.java)
//        val intent = Intent(this, EditScheduleActivity::class.java)
//        pendingIntent =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
////        alarm.setRepeating(AlarmManager.RTC, 2000, 4000, pendingIntent)
//        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, pendingIntent)


//        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, ScheduleReceiver::class.java)
//        val pendingIntent =
//            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        alarm.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent)


    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.run {
            //返回
            if (itemId == android.R.id.home) finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
//        alarm.cancel(pendingIntent)
//        Log.d(TAG, "onDestroy: alarm cancel")

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.run {
            title = "Schedule"

        }
        return super.onCreateOptionsMenu(menu)
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.item_tvTime)
        val tvTips: TextView = itemView.findViewById(R.id.item_tvTips)
        val swOpen: Switch = itemView.findViewById(R.id.item_swOpen)
        val clContainer: ConstraintLayout = itemView.findViewById(R.id.item_clContainer)
    }

    class Adapter(private val mSchedules: ArrayList<TimeItem>, private val context: Context) :
        RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.sample_item_time, parent, false)
            return Holder(view)
        }

        override fun getItemCount(): Int = mSchedules.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = mSchedules[position]

            if (item.isOpen xor holder.swOpen.isEnabled) holder.swOpen.toggle()
            holder.swOpen.setOnCheckedChangeListener { _, isChecked -> item.isOpen = isChecked }
            holder.tvTime.text = item.dataTime
            holder.tvTips.text = item.tips
            holder.clContainer.setOnClickListener {
                val intent = Intent(context, EditScheduleActivity::class.java)
                context.startActivity(intent)
            }
        }

    }

}

class TimeItem(
    var dataTime: String = "00",
    var tips: String = "",
    var isOpen: Boolean = false
) {

}
