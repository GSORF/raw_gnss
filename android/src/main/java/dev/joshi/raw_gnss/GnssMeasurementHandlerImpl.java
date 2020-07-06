package dev.joshi.raw_gnss;

import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.flutter.plugin.common.EventChannel;

public class GnssMeasurementHandlerImpl implements EventChannel.StreamHandler {
    LocationManager locationManager;
    GnssMeasurementsEvent.Callback listener;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    GnssMeasurementHandlerImpl(LocationManager manager) {
        locationManager = manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        listener = createSensorEventListener(events);
        locationManager.registerGnssMeasurementsCallback(listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, locationListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCancel(Object arguments) {
        locationManager.unregisterGnssMeasurementsCallback(listener);
        locationManager.removeUpdates(locationListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    GnssMeasurementsEvent.Callback createSensorEventListener(final EventChannel.EventSink events) {
        return new GnssMeasurementsEvent.Callback() {
            @Override
            public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                super.onGnssMeasurementsReceived(eventArgs);
                HashMap<String, Object> resultMap = new HashMap<>();
                resultMap.put("contents", eventArgs.describeContents());
                resultMap.put("string", eventArgs.toString());
                resultMap.put("status", eventArgs.getClock());

                GnssClock clock = eventArgs.getClock();
                HashMap<String, Object> clockMap = new HashMap<>();

                clockMap.put("contents", clock.describeContents());
                clockMap.put("biasNanos", clock.getBiasNanos());
                clockMap.put("biasUncertaintyNanos", clock.getBiasUncertaintyNanos());
                clockMap.put("driftNanosPerSecond", clock.getDriftNanosPerSecond());
                clockMap.put("driftUncertaintyNanosPerSecond", clock.getDriftUncertaintyNanosPerSecond());
                clockMap.put("fullBiasNanos", clock.getFullBiasNanos());
                clockMap.put("hardwareClockDiscontinuityCount", clock.getHardwareClockDiscontinuityCount());
                clockMap.put("leapSecond", clock.getLeapSecond());
                clockMap.put("timeNanos", clock.getTimeNanos());
                clockMap.put("timeUncertaintyNanos", clock.getTimeUncertaintyNanos());

                resultMap.put("clock", clockMap);

                Collection<GnssMeasurement> measurements = eventArgs.getMeasurements();

                ArrayList<HashMap<String, Object>> measurementsMapList  = new ArrayList<HashMap<String, Object>>();

                for(int i = 0; i < measurements.size(); i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    GnssMeasurement measurement = (GnssMeasurement) measurements.toArray()[i];

                    map.put("contents", measurement.describeContents());
                    map.put("accumulatedDeltaRangeMeters", measurement.getAccumulatedDeltaRangeMeters());
                    map.put("accumulatedDeltaRangeState", measurement.getAccumulatedDeltaRangeState());
                    map.put("accumulatedDeltaRangeUncertaintyMeters", measurement.getAccumulatedDeltaRangeUncertaintyMeters());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        map.put("automaticGainControlLevelDb", measurement.getAutomaticGainControlLevelDb());
                    }
                    map.put("carrierFrequencyHz", measurement.getCarrierFrequencyHz());
                    map.put("cn0DbHz", measurement.getCn0DbHz());
                    map.put("constellationType", measurement.getConstellationType());
                    map.put("multipathIndicator", measurement.getMultipathIndicator());
                    map.put("pseudorangeRateMetersPerSecond", measurement.getPseudorangeRateMetersPerSecond());
                    map.put("pseudorangeRateUncertaintyMetersPerSecond", measurement.getPseudorangeRateUncertaintyMetersPerSecond());
                    map.put("receivedSvTimeNanos", measurement.getReceivedSvTimeNanos());
                    map.put("receivedSvTimeUncertaintyNanos", measurement.getReceivedSvTimeUncertaintyNanos());
                    map.put("snrInDb", measurement.getSnrInDb());
                    map.put("state", measurement.getState());
                    map.put("svid", measurement.getSvid());
                    map.put("timeOffsetNanos", measurement.getTimeOffsetNanos());
                    map.put("string", measurement.toString());

                    measurementsMapList.add(map);
                }

                resultMap.put("measurements", measurementsMapList);

                events.success(resultMap);
            }

            @Override
            public void onStatusChanged(int status) {
                super.onStatusChanged(status);
            }
        };
    }
}
