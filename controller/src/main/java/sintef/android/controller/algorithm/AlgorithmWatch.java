package sintef.android.controller.algorithm;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sintef.android.controller.sensor.SensorData;
import sintef.android.controller.sensor.SensorSession;
import sintef.android.controller.sensor.data.LinearAccelerationData;

/**
 * Created by araneae on 09.02.15.
 */
public class AlgorithmWatch implements AlgorithmInterface{
    //TODO: get data to make the thresholds better.
    private static final double thresholdFall = 1; //20

    //Calculate the acceleration.
    private static double fallIndex(List<LinearAccelerationData> sensors, int startList){

        List <Double> x = new ArrayList<>();
        List <Double> y = new ArrayList<>();
        List <Double> z = new ArrayList<>();
        int startValue = startList;

        for (LinearAccelerationData xyz : sensors){
            x.add((double) xyz.getX());
            y.add((double) xyz.getY());
            z.add((double) xyz.getZ());
        }

        List <List> sensorData = new ArrayList<List>();
        sensorData.add(x);
        sensorData.add(y);
        sensorData.add(z);

        double directionAcceleration = 0;
        double totAcceleration = 0;

        for (int i = 0; i < sensorData.size(); i++){
            for (int j = startValue; j < sensorData.get(i).size(); j++){
                directionAcceleration += Math.pow((Double)sensorData.get(i).get(j) - (Double)sensorData.get(i).get(j - 1), 2);
            }
            totAcceleration += directionAcceleration;
            directionAcceleration = 0;
        }
        return Math.sqrt(totAcceleration);
    }
    //Recognize fall pattern, and decide if there is a fall or not
    public static boolean thresholdAlgorithmWatch(List<LinearAccelerationData> sensors){
        if (sensors.size() == 0) {return true;}

        double accelerationData;
        int startList = 1;

        accelerationData = fallIndex(sensors, startList);

        return accelerationData >= thresholdFall;
    }

    @Override
    public boolean isFall(SensorAlgorithmPack pack) {
        List<LinearAccelerationData> accDataWatch = new ArrayList<>();

        for (Map.Entry<SensorSession, List<SensorData>> entry : pack.getSensorData().entrySet()) {
            switch (entry.getKey().getSensorDevice()) {
                case WATCH:
                    switch (entry.getKey().getSensorType()){
                        case Sensor.TYPE_LINEAR_ACCELERATION:
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                accDataWatch.add((LinearAccelerationData) entry.getValue().get(i).getSensorData());
                            }
                            break;
                    }
                    break;
            }

        }
        return thresholdAlgorithmWatch(accDataWatch);
    }
}
