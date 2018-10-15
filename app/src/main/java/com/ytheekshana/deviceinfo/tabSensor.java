package com.ytheekshana.deviceinfo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class tabSensor extends Fragment {

    String getcount;
    TextView sensor_count;
    Thread loadSensors;
    SwipeRefreshLayout swipesensorlist;
    RecyclerView recyclerSensors;
    Context context;
    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity){
            activity=(Activity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.tabsensor, container, false);
        recyclerSensors = rootView.findViewById(R.id.recyclerSensors);
        sensor_count = rootView.findViewById(R.id.sensor_count);
        swipesensorlist = rootView.findViewById(R.id.swipesensorlist);
        swipesensorlist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(loadSensors).start();
            }
        });

        getcount = String.valueOf(SplashActivity.numberOfSensors) + " Sensors are available on your device";

        loadSensors = new Thread() {
            @Override
            public void run() {
                swipesensorlist.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!swipesensorlist.isRefreshing()) {
                            swipesensorlist.setRefreshing(true);
                        }
                    }
                });

                final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                final RecyclerView.Adapter sensorAdapter = new SensorAdapter(context,getAllSensors());
                recyclerSensors.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerSensors.setLayoutManager(layoutManager);
                        recyclerSensors.setAdapter(sensorAdapter);
                    }
                });
                sensor_count.post(new Runnable() {
                    @Override
                    public void run() {
                        sensor_count.setText(getcount);
                    }
                });

                swipesensorlist.post(new Runnable() {
                    @Override
                    public void run() {
                        if (swipesensorlist.isRefreshing()) {
                            swipesensorlist.setRefreshing(false);
                        }
                    }
                });
            }
        };
        loadSensors.start();
        return rootView;
    }

    private ArrayList<SensorInfo> getAllSensors() {

        ArrayList<SensorInfo> allsensors = new ArrayList<>();
        SensorManager mSensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = Objects.requireNonNull(mSensorManager).getSensorList(Sensor.TYPE_ALL);

        for (Sensor s : deviceSensors) {
            String sensorName = s.getName();
            String sensorVendor = "Vendor : " + s.getVendor();
            String sensorType = "Type : " + GetDetails.GetSensorType(s.getType());
            String wakup = s.isWakeUpSensor() ? "Yes" : "No";
            String wakeUpType = "Wake Up Sensor : " + wakup;
            String sensorPower = "Power : " + s.getPower() + "mA";
            allsensors.add(new SensorInfo(sensorName, sensorVendor, sensorType, wakeUpType, sensorPower));
        }
        return allsensors;
    }
}