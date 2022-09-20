package mx.slam.virtualsensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Slam
 */

public class MySensor implements IXposedHookLoadPackage {

    private Sensor vGSensor, vMSensor, vCSensor;
    private SensorEventListener acc_listener;
    private SensorEventListener magnetic_listener;
    private SensorEventListener gyro_listener;
    private SensorEventListener compass_listener;
    private float[] last_acc={0,0,0};
    private float[] last_mag={0,0,0};
    private float[] last_compass={0,0,0};
    private static final String TAG = "Virtual Gyroscope";

    public MySensor(){
            //virtual sensors definition and variables
            try {
                Class<?> c = Class.forName("android.hardware.Sensor");
                Constructor<?> constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true);
                //Virtual Gyrosocope sensor
                vGSensor = (Sensor) constructor.newInstance();
                //Virtual Magnetic Field sensor
                vMSensor = (Sensor) constructor.newInstance();
                //Virtual Compass sensor
                vCSensor = (Sensor) constructor.newInstance();
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "ClassNotFoundException: " + e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "IllegalAccessException: " + e.getMessage(), e);
            } catch (InstantiationException e) {
                Log.e(TAG, "InstantiationException: " + e.getMessage(), e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "InvocationTargetException: " + e.getMessage(), e);
            }
            try {
                Field gType = vGSensor.getClass().getDeclaredField("mType");
                gType.setAccessible(true);
                gType.set(vGSensor, Sensor.TYPE_GYROSCOPE);

                Field gString_type = vGSensor.getClass().getDeclaredField("mStringType");
                gString_type.setAccessible(true);
                gString_type.set(vGSensor, Sensor.STRING_TYPE_GYROSCOPE);

                Field gName = vGSensor.getClass().getDeclaredField("mName");
                gName.setAccessible(true);
                gName.set(vGSensor, "Virtual Gyroscope Sensor");

                Field gVendor = vGSensor.getClass().getDeclaredField("mVendor");
                gVendor.setAccessible(true);
                gVendor.set(vGSensor, "Slam");

                Field gPermission = vGSensor.getClass().getDeclaredField("mRequiredPermission");
                gPermission.setAccessible(true);
                gPermission.set(vGSensor, "android.hardware.sensor.gyroscope");

                Field gHandle = vGSensor.getClass().getDeclaredField("mHandle");
                gHandle.setAccessible(true);
                gHandle.set(vGSensor, 81);

                Field gPower = vGSensor.getClass().getDeclaredField("mPower");
                gPower.setAccessible(true);
                gPower.set(vGSensor, 0.13f);

                Field gVersion = vGSensor.getClass().getDeclaredField("mVersion");
                gVersion.setAccessible(true);
                gVersion.set(vGSensor, 1);

                Field gResolution = vGSensor.getClass().getDeclaredField("mResolution");
                gResolution.setAccessible(true);
                gResolution.set(vGSensor, 360.0f);

                Field gRange = vGSensor.getClass().getDeclaredField("mMaxRange");
                gRange.setAccessible(true);
                gRange.set(vGSensor, 360.0f);
                // End of virtual gyroscope sensor
                // Virtual Magnetometer sensor instance
                Field mType = vMSensor.getClass().getDeclaredField("mType");
                mType.setAccessible(true);
                mType.set(vMSensor, Sensor.TYPE_MAGNETIC_FIELD);

                Field mString_type = vMSensor.getClass().getDeclaredField("mStringType");
                mString_type.setAccessible(true);
                mString_type.set(vMSensor, Sensor.STRING_TYPE_MAGNETIC_FIELD);
                mString_type.set(vMSensor, Sensor.STRING_TYPE_MAGNETIC_FIELD_UNCALIBRATED);

                Field mName = vMSensor.getClass().getDeclaredField("mName");
                mName.setAccessible(true);
                mName.set(vMSensor, "Virtual Magnetometer Sensor");

                Field mVendor = vMSensor.getClass().getDeclaredField("mVendor");
                mVendor.setAccessible(true);
                mVendor.set(vMSensor, "Slam");

                Field mPermission = vMSensor.getClass().getDeclaredField("mRequiredPermission");
                mPermission.setAccessible(true);
                //mPermission.set(vMSensor,"android.hardware.GeomagneticField");
                mPermission.set(vMSensor, "android.sensor.magnetic_field");
                mPermission.set(vMSensor, "android.sensor.magnetic_field_uncalibrated");

                Field mHandle = vMSensor.getClass().getDeclaredField("mHandle");
                mHandle.setAccessible(true);
                mHandle.set(vMSensor, 14);

                Field mPower = vMSensor.getClass().getDeclaredField("mPower");
                mPower.setAccessible(true);
                mPower.set(vMSensor, 0.13f);

                Field mVersion = vMSensor.getClass().getDeclaredField("mVersion");
                mVersion.setAccessible(true);
                mVersion.set(vMSensor, 1);

                Field mResolution = vMSensor.getClass().getDeclaredField("mResolution");
                mResolution.setAccessible(true);
                mResolution.set(vMSensor, 10.0f);

                Field mRange = vMSensor.getClass().getDeclaredField("mMaxRange");
                mRange.setAccessible(true);
                mRange.set(vMSensor, 10000.0f);
                //Virtual Magnetic Field Ends

                //Virtual Compass sensor instance
                Field cType = vCSensor.getClass().getDeclaredField("mType");
                cType.setAccessible(true);
                cType.set(vCSensor, Sensor.TYPE_ORIENTATION);

                Field cString_type = vCSensor.getClass().getDeclaredField("mStringType");
                cString_type.setAccessible(true);
                cString_type.set(vCSensor, Sensor.STRING_TYPE_ORIENTATION);

                Field cName = vCSensor.getClass().getDeclaredField("mName");
                cName.setAccessible(true);
                cName.set(vCSensor, "Virtual Compass Sensor");

                Field cVendor = vCSensor.getClass().getDeclaredField("mVendor");
                cVendor.setAccessible(true);
                cVendor.set(vCSensor, "Slam");

                Field cPermission = vCSensor.getClass().getDeclaredField("mRequiredPermission");
                cPermission.setAccessible(true);
                mPermission.set(vCSensor, "android.sensor.device_orientation");

                Field cHandle = vCSensor.getClass().getDeclaredField("mHandle");
                cHandle.setAccessible(true);
                cHandle.set(vCSensor, 27);

                Field cPower = vCSensor.getClass().getDeclaredField("mPower");
                cPower.setAccessible(true);
                cPower.set(vCSensor, 0.15f);

                Field cVersion = vCSensor.getClass().getDeclaredField("mVersion");
                cVersion.setAccessible(true);
                cVersion.set(vCSensor, 1);

                Field cResolution = vCSensor.getClass().getDeclaredField("mResolution");
                cResolution.setAccessible(true);
                cResolution.set(vCSensor, 10.0f);

                Field cRange = vCSensor.getClass().getDeclaredField("mMaxRange");
                cRange.setAccessible(true);
                cRange.set(vCSensor, 360.0f);
                //Virtual Compass sensor ends

            } catch (NoSuchFieldException e) {
                Log.e(TAG, "NoSuchFieldE: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "IllegalAccess: " + e.getMessage(), e);
            }

            // Real accelerometer listener.
            acc_listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    //String acc = event.values[0] + ";" + event.values[1] + ";" + event.values[2];
                    //acc_vals.setText(acc);

                    last_acc = event.values;
                    putVectorDataOnVirtualSensors(last_acc);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            magnetic_listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    //String mag = event.values[0] + ";" + event.values[1] + ";" + event.values[2];
                    //magnet_vals.setText(mag);

                    last_mag = event.values; //new float[]{event.values[1], event.values[2], 0.0f};
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            gyro_listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    //String gyro = event.values[0] + ";" + event.values[1] + ";" + event.values[2];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            compass_listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    //String compass = event.values[0] + ";" + event.values[1] + ";" + event.values[2];
                    last_compass = event.values;
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
    }

    // Send accelerometer vectors to virtual sensors vectors
    private void putVectorDataOnVirtualSensors(float[] acc){
        // SensorEvent instance
        Constructor[] ctors = SensorEvent.class.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 1)
                break;
        }
        // Setting vector data values
        try {
            ctor.setAccessible(true);
            // Simple vectors modification for virtual gyroscope sensor values
            // use only the Y and Z axis of the accelerometer sensor
            float[] axis_values = new float[]{acc[1],acc[2],0.0f};
            // Data constructor for virtual gyroscope
            SensorEvent event = (SensorEvent) ctor.newInstance(3);
            System.arraycopy(axis_values, 0, event.values, 0, event.values.length);
            event.timestamp = System.nanoTime();
            event.accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            event.sensor = vGSensor;
            gyro_listener.onSensorChanged(event);
            // Same data constructor for virtual magnetic field
            event = (SensorEvent) ctor.newInstance(3);
            System.arraycopy(axis_values, 0, event.values, 0, event.values.length);
            event.timestamp = System.nanoTime();
            event.accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            event.sensor = vMSensor;
            magnetic_listener.onSensorChanged(event);
            // Same data constructor for virtual compass
            event = (SensorEvent) ctor.newInstance(3);
            System.arraycopy(axis_values, 0, event.values, 0, event.values.length);
            event.timestamp = System.nanoTime();
            event.accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            event.sensor = vCSensor;
            compass_listener.onSensorChanged(event);
        }catch(NullPointerException e){
            Log.e(TAG, "NullPointerException: "+e.getMessage(), e);
        }catch(IllegalAccessException e){
            Log.e(TAG, "IllegalAccessException: "+e.getMessage(),e);
        }catch(InvocationTargetException e){
            Log.e(TAG, "InvocationTargetException: "+e.getMessage(),e);
        }catch(InstantiationException e){
            Log.e(TAG, "InstantiationException: "+e.getMessage(),e);
        }
    }

    // Xposed Hook
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            final Class<?> sensorEQ = findClass(
                    "android.hardware.SystemSensorManager$SensorEventQueue",
                    lpparam.classLoader);
            final Class<?> sensorSMGR = findClass(
                    "android.hardware.SystemSensorManager",
                    lpparam.classLoader);
            final Class<?> sensorMGR = findClass(
                    "android.hardware.SensorManager",
                    lpparam.classLoader);

            XposedBridge.hookAllConstructors(sensorSMGR, new
                    XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws
                                Throwable {
                                try {
                                    Field list = param.thisObject.getClass().getDeclaredField("mFullSensorsList");
                                    list.setAccessible(true);
                                    Object list_val = list.get(param.thisObject);
                                    Method add_method = list_val.getClass().getDeclaredMethod("add", Object.class);
                                    add_method.invoke(list_val, vGSensor);
                                    XposedBridge.log("Virtual Gyroscope sensor added!");
                                    Log.e(TAG, "Virtual Gyroscope sensor added!");
                                    add_method.invoke(list_val, vMSensor);
                                    XposedBridge.log("Virtual Magnetometer sensor added!");
                                    Log.e(TAG, "Virtual Magnetometer sensor added!");
                                    add_method.invoke(list_val, vCSensor);
                                    XposedBridge.log("Virtual Compass sensor added!");
                                    Log.e(TAG, "Virtual Compass sensor added!");
                                }catch(NoSuchFieldException e){
                                    Log.e(TAG, "NoSuchFieldException: "+e.getMessage(),e);
                                }catch(IllegalAccessException e){
                                    Log.e(TAG, "IllegalAccessException: "+e.getMessage(),e);
                                }
                        }
                    });

            XposedBridge.hookAllMethods(sensorMGR, "registerListener", new
                    XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws
                                Throwable {
                            if(((Sensor)param.args[1]).getType()==Sensor.TYPE_GYROSCOPE||((Sensor)param.args[1]).getType()==Sensor.TYPE_GYROSCOPE_UNCALIBRATED){
                                gyro_listener = (SensorEventListener)param.args[0];
                                // cloning listeners
                                //magnetic_listener = gyro_listener;

                                param.setResult(true);
                                // Virtual Gyroscope sensor register
                                ((SensorManager)param.thisObject).registerListener(acc_listener, ((SensorManager)param.thisObject).getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
                                // Virtual Magnetometer sensor register
                                ((SensorManager)param.thisObject).registerListener(magnetic_listener, ((SensorManager)param.thisObject).getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
                                // Virtual Compass sensor register
                                ((SensorManager)param.thisObject).registerListener(compass_listener, ((SensorManager)param.thisObject).getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
                            }
                        }
                    });
            XposedBridge.hookAllMethods(sensorMGR, "unregisterListener", new
                    XC_MethodHook() {
                        @SuppressWarnings("unchecked")
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws
                                Throwable {
                            if(param.args.length==2 && param.args[1] instanceof Sensor && (((Sensor)param.args[1]).getType()==Sensor.TYPE_GYROSCOPE||((Sensor)param.args[1]).getType()==Sensor.TYPE_GYROSCOPE_UNCALIBRATED)){
                                ((SensorManager)param.thisObject).unregisterListener(acc_listener);
                            }
                            if(param.args.length==2 && param.args[1] instanceof Sensor && (((Sensor)param.args[1]).getType()==Sensor.TYPE_MAGNETIC_FIELD||((Sensor)param.args[1]).getType()==Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)){
                                ((SensorManager)param.thisObject).unregisterListener(magnetic_listener);
                            }
                            if(param.args.length==2 && param.args[1] instanceof Sensor && (((Sensor)param.args[1]).getType()==Sensor.TYPE_ORIENTATION)){
                                ((SensorManager)param.thisObject).unregisterListener(compass_listener);
                            }
                        }
                    });

        } catch (Throwable t) {
            Log.e(TAG, "Exception in SystemSensorEvent hook: " + t.getMessage(), t);
            XposedBridge.log(t);
            // Do nothing
        }
    }
}