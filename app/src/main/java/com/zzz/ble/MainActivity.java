package com.zzz.ble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    RecyclerView recBle;
    TextView tvMsg;

    List<BleDevice> bleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = findViewById(R.id.tv_msg);
        setUpRecyclerView();

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                scanBle();
            }
        }).start();

    }

    private void setUpRecyclerView() {
        recBle = findViewById(R.id.rec_ble);
        recBle.setLayoutManager(new LinearLayoutManager(this));
        BleRecyclerAdapter adapter = new BleRecyclerAdapter(this, bleList);
        recBle.setAdapter(adapter);

        //Item点击事件
        adapter.setOnCellClickListener(new BleRecyclerAdapter.OnCellClickListener() {
            @Override
            public void onCellClick(final BleDevice bleDevice) {
                //点击连接设备
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connectBle(bleDevice);
                    }
                }).start();

            }
        });
    }

    //连接设备
    private void connectBle(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                // 开始连接
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                // 连接失败
                Toast.makeText(MainActivity.this, "连接失败" + exception.getDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();

                //配置接受信息
                BleManager.getInstance().notify(
                        bleDevice,
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        new BleNotifyCallback() {
                            @Override
                            public void onNotifySuccess() {
                                // 打开通知操作成功
                            }

                            @Override
                            public void onNotifyFailure(BleException exception) {
                                // 打开通知操作失败
                            }

                            @Override
                            public void onCharacteristicChanged(byte[] data) {
                                // 打开通知后，设备发过来的数据将在这里出现
                                tvMsg.setText(Arrays.toString(data));
                            }
                        });

            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接中断，isActiveDisConnected表示是否是主动调用了断开连接方法
            }
        });
    }

    //扫描蓝牙设备
    private void scanBle() {
        BleManager.getInstance().enableBluetooth();
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                // 开始扫描的回调
                Toast.makeText(MainActivity.this, "开始扫描", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                //当搜索到多了一个设备，添加在列表中并更新列表控件
                bleList.add(bleDevice);
                ((BleRecyclerAdapter) recBle.getAdapter()).replaceData(bleList);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 扫描完成的回调，列表里将不会有重复的设备

            }
        });
    }


}
