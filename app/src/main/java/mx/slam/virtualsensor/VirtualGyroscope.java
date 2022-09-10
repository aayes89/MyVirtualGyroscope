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
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Slam on 10/08/2022 based on
 * GyroEmu Xposed module made by Mordraug on 7/22/2016.
 */

public class VirtualGyroscope implements IXposedHookLoadPackage {

    private Sensor vSensor;
    private final SensorEventListener acc_listener;
    private SensorEventListener gyro_listener;
    private float[] last_acc={0,0,0};
    private float[] last_mag={0,0,0};
    private static final String TAG = "Virtual Gyroscope";

    public VirtualGyroscope(){
        //virtual sensor definition and variables
        try {
            Class<?> c = Class.forName("android.hardware.Sensor");
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            vSensor = (Sensor)constructor.newInstance();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "ClassNotFoundException: "+e.getMessage(),e);
        }catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException: "+e.getMessage(),e);
        }catch (IllegalAccessException e){
            Log.e(TAG, "IllegalAccessException: "+e.getMessage(),e);
        }catch(InstantiationException e){
            Log.e(TAG, "InstantiationException: "+e.getMessage(),e);
        }catch(InvocationTargetException e){
            Log.e(TAG, "InvocationTargetException: "+e.getMessage(),e);
        }
        try {
            Field type = vSensor.getClass().getDeclaredField("mType");
            type.setAccessible(true);
            type.set(vSensor, Sensor.TYPE_GYROSCOPE);

            Field string_type = vSensor.getClass().getDeclaredField("mStringType");
            string_type.setAccessible(true);
            string_type.set(vSensor, Sensor.STRING_TYPE_GYROSCOPE);

            Field name = vSensor.getClass().getDeclaredField("mName");
            name.setAccessible(true);
            name.set(vSensor, "Virtual Gyroscope Sensor");

            Field vendor = vSensor.getClass().getDeclaredField("mVendor");
            vendor.setAccessible(true);
            vendor.set(vSensor, "Slam");

            Field permission = vSensor.getClass().getDeclaredField("mRequiredPermission");
            permission.setAccessible(true);
            permission.set(vSensor, "android.hardware.sensor.gyroscope");

            Field handle = vSensor.getClass().getDeclaredField("mHandle");
            handle.setAccessible(true);
            handle.set(vSensor, 81);

            Field power = vSensor.getClass().getDeclaredField("mPower");
            power.setAccessible(true);
            power.set(vSensor, 0.13f);

            Field version = vSensor.getClass().getDeclaredField("mVersion");
            version.setAccessible(true);
            version.set(vSensor, 1);

            Field resolution = vSensor.getClass().getDeclaredField("mResolution");
            resolution.setAccessible(true);
            resolution.set(vSensor, 360.0f);

            Field range = vSensor.getClass().getDeclaredField("mMaxRange");
            range.setAccessible(true);
            range.set(vSensor, 360.0f);

        }catch(NoSuchFieldException e){
            Log.e(TAG, "NoSuchFieldE: "+e.getMessage(),e);
        }catch(IllegalAccessException e){
            Log.e(TAG, "IllegalAccess: "+e.getMessage(),e);
        }

        // Real accelerometer listener.
        acc_listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                last_acc=event.values;
                last_mag = new float[]{last_acc[1], last_acc[2], 0.0f};
                putVectorDataOnVirtualGyro(last_mag);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    // Send accelerometer vectors to virtual gyroscope vectors
    private void putVectorDataOnVirtualGyro(float[] acc){
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
            SensorEvent event = (SensorEvent) ctor.newInstance(3);
            System.arraycopy(acc, 0, event.values, 0, event.values.length);
            event.timestamp = System.nanoTime();
            event.accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            event.sensor = vSensor;
            gyro_listener.onSensorChanged(event);
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
                    "android.hardware.SensorMana" +
                            "ger",
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
                                    add_method.invoke(list_val,vSensor);
                                    Log.e(TAG, "Virtual Gyroscope sensor added!");
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
                                param.setResult(true);
                                ((SensorManager)param.thisObject).registerListener(acc_listener, ((SensorManager)param.thisObject).getDefaultSensor(Sensor.TYPE_ACCELEROMETER), (int)param.args[2]);
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
                        }
                    });

        } catch (Throwable t) {
            Log.e(TAG, "Exception in SystemSensorEvent hook: " + t.getMessage(), t);
            // Do nothing
        }
    }
}
