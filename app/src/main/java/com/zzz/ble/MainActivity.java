package com.zzz.ble;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.common.PropertyType;
import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    TextView tvTemp;
    TextView tvHe;
    TextView tvCo;
    TextView tvStatus;

    TextView btnConnectBle;
    TextView btnDisConnectBle;
    TextView btnExit;
    RecyclerView recBle;
    List<BluetoothLeDevice> bleList = new ArrayList<>();
    AlertDialog progressDialog; //加载弹窗
    AlertDialog bleDialog; //蓝牙选择弹窗

    List<String> mockTemp = new ArrayList<>(); //温度
    List<String> mockHe = new ArrayList<>(); //湿度
    List<String> mockCo = new ArrayList<>(); //浓度
    List<String> mockStatus = new ArrayList<>(); //状态

    Boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mockTemp.add("34");
        mockTemp.add("30");
        mockTemp.add("29");
        mockTemp.add("32");
        mockTemp.add("30");
        mockTemp.add("30");
        mockTemp.add("31");
        mockTemp.add("31");
        mockTemp.add("30");
        mockTemp.add("32");
        mockTemp.add("30");
        mockTemp.add("32");
        mockTemp.add("32");
        mockTemp.add("33");

        mockHe.add("50");
        mockHe.add("53");
        mockHe.add("52");
        mockHe.add("54");
        mockHe.add("54");
        mockHe.add("54");
        mockHe.add("53");
        mockHe.add("54");
        mockHe.add("53");
        mockHe.add("54");
        mockHe.add("54");
        mockHe.add("53");
        mockHe.add("53");
        mockHe.add("54");

        mockCo.add("120");
        mockCo.add("125");
        mockCo.add("119");
        mockCo.add("126");
        mockCo.add("125");
        mockCo.add("123");
        mockCo.add("125");
        mockCo.add("128");
        mockCo.add("127");
        mockCo.add("123");
        mockCo.add("118");
        mockCo.add("131");
        mockCo.add("121");
        mockCo.add("123");

        mockStatus.add("无人");
        mockStatus.add("无人");
        mockStatus.add("有人");
        mockStatus.add("有人");
        mockStatus.add("有人");
        mockStatus.add("有人");
        mockStatus.add("有人");
        mockStatus.add("有人");
        mockStatus.add("无人");
        mockStatus.add("无人");
        mockStatus.add("无人");
        mockStatus.add("无人");
        mockStatus.add("无人");
        mockStatus.add("无人");

        tvTemp = findViewById(R.id.tv_temp);
        tvHe = findViewById(R.id.tv_he);
        tvCo = findViewById(R.id.tv_co);
        tvStatus = findViewById(R.id.tv_status);

        btnConnectBle = findViewById(R.id.btn_connect_ble);
        btnDisConnectBle = findViewById(R.id.btn_disconnect_ble);
        btnExit = findViewById(R.id.btn_exit);

        progressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.layout_progress)
                .create();


        //连接蓝牙按钮
        btnConnectBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBleDialog();
            }
        });

        //断开连接按钮
        btnDisConnectBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disConnect(); //断开所有蓝牙连接
                btnConnectBle.setVisibility(View.VISIBLE); //展示连接按钮
                btnDisConnectBle.setVisibility(View.GONE); //隐藏断开按钮
            }
        });

        //退出程序按钮
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //配置按钮初始化的显示
        btnConnectBle.setVisibility(View.VISIBLE); //展示连接按钮
        btnDisConnectBle.setVisibility(View.GONE); //隐藏断开按钮

/*        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);*/


    }

    //配置RecyclerView
    private void setUpRecyclerView() {
        recBle.setLayoutManager(new LinearLayoutManager(this));
        BleRecyclerAdapter adapter = new BleRecyclerAdapter(this, bleList);
        recBle.setAdapter(adapter);
        //Item点击事件
        adapter.setOnCellClickListener(new BleRecyclerAdapter.OnCellClickListener() {
            @Override
            public void onCellClick(final BluetoothLeDevice bleDevice) {
                //点击连接设备
                connectBle(bleDevice);
            }
        });
    }

    //连接设备
    private void connectBle(final BluetoothLeDevice bleDevice) {
        // 开始连接

        progressDialog.show();

      /*  BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                // 开始连接
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                });

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                // 连接失败
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });

                Toast.makeText(MainActivity.this, "连接失败" + exception.getDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                ((BleRecyclerAdapter)recBle.getAdapter()).setConnectedDevice(bleDevice);
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
                ((BleRecyclerAdapter)recBle.getAdapter()).setConnectedDevice(null);
            }
        });*/

        ViseBle.getInstance().connect(bleDevice, new IConnectCallback() {
            @Override
            public void onConnectSuccess(DeviceMirror deviceMirror) {// 连接成功的回调，BleDevice即为所连接的BLE设备
                isConnected = true;
                runOnUiThread(new Runnable() {//主线程实现Runnable接口更新UI
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        //隐藏加载弹窗（转圈）
                        progressDialog.dismiss();
                        //将连接蓝牙按钮隐藏
                        btnConnectBle.setVisibility(View.GONE);
                        //将断开连接按钮展示
                        btnDisConnectBle.setVisibility(View.VISIBLE);
                        //当蓝牙弹窗还是显示状态时
                        if (bleDialog.isShowing()) {
                            //更新列表中的已连接，背景变绿
                            ((BleRecyclerAdapter) recBle.getAdapter()).setConnectedDevice(bleDevice);
                        }

                        final int[] index = {0};
                        //定时更新
                        new Timer("testTimer").schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(isConnected){
                                            if (index[0] == 0 || index[0] == 14) {
                                                tvTemp.setText(mockTemp.get(0));
                                            } else {
                                                tvTemp.setText(mockTemp.get(index[0] % 14));
                                                tvHe.setText(mockHe.get(index[0] % 14));
                                                tvCo.setText(mockCo.get(index[0] % 14));
                                                tvStatus.setText(mockStatus.get(index[0] % 14));
                                            }
                                            index[0]++;
                                        }

                                    }
                                });
                            }
                        }, 1000, 1000);


                    }
                });


                //绑定接受信息方式
                BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                        .setBluetoothGatt(deviceMirror.getBluetoothGatt())
                        .setPropertyType(PropertyType.PROPERTY_NOTIFY)
                        .setServiceUUID(UUID.randomUUID())
                        .setCharacteristicUUID(UUID.randomUUID())
                        .setDescriptorUUID(UUID.randomUUID())
                        .builder();
                deviceMirror.bindChannel(new IBleCallback() {
                    @Override
                    public void onSuccess(final byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
                        // 打开通知后，设备发过来的数据将在这里出现

                    }

                    @Override
                    public void onFailure(final com.vise.baseble.exception.BleException exception) {
                        //连接失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "连接失败" + exception.getDescription(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }, bluetoothGattChannel);
                deviceMirror.registerNotify(false);

                deviceMirror.setNotifyListener(bluetoothGattChannel.getGattInfoKey(), new IBleCallback() {
                    @Override
                    public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {

                    }

                    @Override
                    public void onFailure(com.vise.baseble.exception.BleException exception) {

                    }
                });

            }

            @Override
            public void onConnectFailure(com.vise.baseble.exception.BleException exception) {

            }

            @Override
            public void onDisconnect(boolean isActive) {
                isConnected = false;
            }
        });


    }

    //断开连接
    private void disConnect() {
        ViseBle.getInstance().disconnect();
    }

    //扫描蓝牙设备
    private void scanBle() {
/*        BleManager.getInstance().enableBluetooth();
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
        });*/


        ViseBle.getInstance().startScan(new ScanCallback(new IScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDeviceFound(final BluetoothLeDevice ble) {
                //当搜索到多了一个设备，添加在列表中并更新列表控件
                if (bleList.stream().anyMatch(new Predicate<BluetoothLeDevice>() {
                    @Override
                    public boolean test(BluetoothLeDevice bluetoothLeDevice) {
                        return bluetoothLeDevice.getAddress().equals(ble.getAddress());
                    }
                })) {
                    //其中含有同mac设备 不添加
                } else {
                    //不含mac相同 添加
                    bleList.add(ble);
                    if (bleDialog.isShowing()) {
                        recBle.getAdapter().notifyItemInserted(bleList.size() - 1);
                    }
                }
            }

            @Override
            public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {

            }

            @Override
            public void onScanTimeout() {

            }
        }));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    //弹出蓝牙选择弹窗
    private void showBleDialog() {
        View bleDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ble, null, false);
        recBle = bleDialogView.findViewById(R.id.rec_ble);
        bleDialog = new AlertDialog.Builder(this)
                .setTitle("请选择蓝牙设备")
                .setView(bleDialogView)
                .setNegativeButton("取消", null)
                .create();
        setUpRecyclerView();
        bleDialog.show();
        scanBle();
    }


}
