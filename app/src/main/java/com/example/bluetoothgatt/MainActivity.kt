package com.example.bluetoothgatt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.OutputStream
import java.util.*

const val TAG = "msg"

class MainActivity : AppCompatActivity() {
    private val UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb") //服务UUID
    private val UUID_RW = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb") //读写UUID
    private val addr_mr_wus_blue = "D5:3D:7C:DC:A3:EF"

    private val UUID_NOTIFY = UUID.fromString("00006a02-0000-1000-8000-00805f9b34fb") //订阅通知的UUID
    private val UUID_NOTIFY_DESCRIPTOR =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") //订阅通知的UUID

    val data = ArrayList<BluetoothDevice>()
    var mIndex = 0
    lateinit var mBluetoothAdapter: BluetoothAdapter
    var out: OutputStream? = null
    var gatt: BluetoothGatt? = null
    lateinit var adapter: Adapter
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            try {
                if (!result.device.name.isNullOrEmpty()) {
                    data.add(result.device)
                    adapter.notifyItemChanged(data.size - 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
//                    toast("state change")
//                    if (mBluetoothAdapter.isEnabled) bluetoothInit()
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    main_tvIndicate.setBackgroundColor(Color.LTGRAY)
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    main_tvIndicate.setBackgroundColor(Color.GREEN)
                }
                else -> {
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        requestPermissions(
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )

        adapter = Adapter(this)
        main_rvList.adapter = adapter
        main_rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        main_rvList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))

        startActivity(Intent(this, ScheduleActivity::class.java))//fixme test something
        return

        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        }
        registerReceiver(broadcastReceiver, filter)
        bluetoothInit()
    }


    override fun onStart() {
        super.onStart()

        if (!mBluetoothAdapter.isEnabled)
            if (!mBluetoothAdapter.enable()) toast("bluetooth open failed!")

        if (mBluetoothAdapter.isEnabled && main_btOpen.text == "open") main_btOpen.performClick()
    }

    override fun onStop() {
        super.onStop()
        if (main_btOpen.text == "close") {
            main_btOpen.performClick()
            main_tvIndicate.setBackgroundColor(Color.GRAY)
        }
    }

    //初始化LE设备
    private fun bluetoothInit() {

        //打开|关闭GATT
        main_btOpen.setOnClickListener {
            val device =
                if (mIndex >= data.size) mBluetoothAdapter.getRemoteDevice(addr_mr_wus_blue)
                else data[mIndex]
            main_tvTitle.text = device.name
            main_btOpen.setTextColor(Color.BLACK)
            main_btOpen.text =
                when (main_btOpen.text) {
                    "open" -> {
                        gatt = device.connectGatt(this@MainActivity, false, gattCallback)

                        if (gatt == null) {
                            main_btOpen.setTextColor(Color.RED)
                            "open"
                        } else "close"
                    }
                    else -> {
                        gatt?.close()
                        main_tvIndicate.setBackgroundColor(Color.LTGRAY)
                        "open"
                    }
                }
        }
        //扫描|取消扫描GATT
        main_btScan.setOnClickListener {
            val leScanner = mBluetoothAdapter.bluetoothLeScanner
            main_btScan.setTextColor(Color.BLACK)

            main_btScan.text = when (main_btScan.text) {
                "scan" -> {
                    data.clear()
                    adapter.notifyDataSetChanged()

                    if (leScanner == null) {
                        main_btScan.setTextColor(Color.RED)
                        "scan"
                    } else {
                        outMsg("scanning...")
                        leScanner.startScan(scanCallback)

                        //自动关闭LE设备扫描
                        Thread {
                            Thread.sleep(10_000)
                            if (main_btScan.text != "scan") {
                                runOnUiThread { main_btScan.performClick() }
                            }
                        }.start()
                        "stop scan"
                    }
                }
                else -> {
                    leScanner?.stopScan(scanCallback)
                    "scan"
                }
            }
        }
        //发送GATT数据
        main_btSend.setOnClickListener {
            val text = main_etContent.text.toString()
            gatt?.send(text)
        }
        //长按清空Log数据
        main_tvLog.setOnLongClickListener { main_tvLog.text = "";count = 0;true }
        //发送“上”
        main_btUp.setOnClickListener {
            main_btUp.setTypeface(null, Typeface.NORMAL)
            main_btUp.text =
                when (main_btUp.text) {
                    "up" -> {
                        gatt?.send(-8)
                        main_btDown.text = "down"
                        main_btDown.setTypeface(null, Typeface.NORMAL)
                        main_btUp.setTypeface(null, Typeface.BOLD)
                        "stop"
                    }
                    else -> {
                        gatt?.send(-7)
                        main_btDown.text = "down"
                        "up"
                    }
                }

        }
        //发送“下”
        main_btDown.setOnClickListener {
            main_btDown.setTypeface(null, Typeface.NORMAL)
            main_btDown.text =
                when (main_btDown.text) {
                    "down" -> {
                        gatt?.send(-9)
                        main_btUp.text = "up"
                        main_btUp.setTypeface(null, Typeface.NORMAL)
                        main_btDown.setTypeface(null, Typeface.BOLD)
                        "stop"
                    }
                    else -> {
                        gatt?.send(-7)
                        main_btUp.text = "up"
                        "down"
                    }
                }
        }
        //发送数值
        main_btNumber.setOnClickListener {
            val str = main_etContent.text.toString()
            var byte: Byte = 1
            try {
                byte = str.toByte()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            gatt?.send(byte)
            outMsg("digital: $byte")
        }
        //拖动亮度
        main_sbSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val progress = main_sbSeek.progress.toByte()
                gatt?.send(-2, progress)
                main_etContent.setText(progress.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        //渐变亮度
        main_btFading.setOnClickListener {
            main_btFading.text = when (main_btFading.text) {
                "fading" -> {
                    val strText = main_etContent.text.toString()
                    val value: Byte
                    if (strText.isEmpty()) {
                        main_etContent.text.append("0")
                        value = 0
                    } else value = strText.toByte()
                    gatt?.send(-1, value)
                    "pause"
                }
                else -> {
                    gatt?.send(-2, 100)
                    "fading"
                }
            }

        }
        //切换led
        main_btFull.setOnClickListener {
            main_btFull.text =
                when (main_btFull.text) {
                    "led1" -> {
                        gatt?.send(-4)
                        "led2"
                    }
                    else -> {
                        gatt?.send(-5)
                        "led1"
                    }
                }
        }
        //点亮全部led
        main_btFull.setOnLongClickListener {
            gatt?.send(-6)
            main_btFull.text = "led1"
            true
        }
        //添加计划
        main_btAddSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }
    }


    var mComm = 0.toByte()
    private fun BluetoothGatt.send(comm: Byte, value: Byte) {
        if (comm != mComm) {
            mComm = comm
            gatt?.send(comm)
        }
        gatt?.send(value)
    }


    /**发送字节数组*/
    private fun BluetoothGatt.send(vararg bytes: Byte) {
        val service = getService(UUID_SERVICE)
        if (service == null) {
            outMsg("no service")
            return
        }

        val sb = StringBuffer()
        for (byte in bytes) sb.append("$byte ")

        service.getCharacteristic(UUID_RW)?.run {
            value = bytes
            setCharacteristicNotification(this, true)
            outMsg("$sb ${if (writeCharacteristic(this)) "" else "write failed!"}")

            getDescriptor(UUID_NOTIFY_DESCRIPTOR)?.run {
                value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                Log.d(TAG, "send: writeDescriptor = ${writeDescriptor(this)}");
            }
        }
    }


    /**发送字符串*/
    private fun BluetoothGatt.send(text: String) {
        val service = getService(UUID_SERVICE)
        if (service == null) {
            outMsg("service is null")
            return
        }

        outMsg(text)
        val chara = service.getCharacteristic(UUID_RW).apply { setValue(text) }
        setCharacteristicNotification(chara, true)
        writeCharacteristic(chara)
    }


    /**gatt回调函数*/
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            characteristic?.run {
                for (byte in value) outMsg("feedback: $byte")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> outMsg("GATT_SUCCESS")
                BluetoothGatt.GATT_FAILURE -> outMsg("GATT_FAILURE")
                else -> outMsg("don't know what...")
            }
        }

        @SuppressLint("ResourceAsColor")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    outMsg("2 connected")
                    runOnUiThread {
                        main_tvIndicate.setBackgroundColor(Color.GREEN)
                    }
                    val discoverServices = gatt?.discoverServices()
                    outMsg("discoverServices:$discoverServices")
                }
                BluetoothGatt.STATE_CONNECTING -> outMsg("2 connecting")
                BluetoothGatt.STATE_DISCONNECTED -> {
                    runOnUiThread {
                        if (main_btOpen.text == "close") main_btOpen.performClick()
                        main_tvIndicate.setBackgroundColor(Color.LTGRAY)
                    }
                }
                BluetoothGatt.STATE_DISCONNECTING -> outMsg("2 disconnecting")
                else -> outMsg("2 otherwise")
            }
        }
    }


    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


    var count = 0
    fun outMsg(msg: String) {
        count++
        runOnUiThread {
            main_tvLog.text = "$count.$msg\n${main_tvLog.text}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    /**适配器*/
    inner class Adapter(private val context: Context) : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.sample_item1, parent, false)
            return Holder(view)
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val myData = data[position]
            holder.title.text = myData.name
            holder.uuid.text = myData.address
            holder.itemView.setOnClickListener {
                main_tvTitle.text = myData.name
                mIndex = position
            }
        }

    }

    /**持有者*/
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.sample_tvTitle)
        val uuid: TextView = itemView.findViewById(R.id.sample_tvUuid)
    }

}
