package com.example.akash.streetracer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    GameSurface gameSurface;
    SensorManager sensorManager;
    Sensor accel;
    double ax;
    double ay;
    double az;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);

    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }

    //----------------------------GameSurface Below This Line--------------------------
    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap racecar;
        Bitmap obsacles;
        int xval =100;
        int time = 0;

        int screenWidth;
        int screenHeight;

        public GameSurface(Context context) {
            super(context);

            holder=getHolder();
           // myImage.setHeight(80);
           // myImage.setWidth(80);
            racecar = BitmapFactory.decodeResource(getResources(),R.drawable.car);
            obsacles = BitmapFactory.decodeResource(getResources(),R.drawable.obst);


            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener( this, accel, sensorManager.SENSOR_DELAY_NORMAL);



        }

        @Override
        public void run() {
            while (running == true){

                if (holder.getSurface().isValid() == false)
                    continue;

                Canvas canvas= holder.lockCanvas();
                time++;

                canvas.drawRGB(255,0,0);
                xval-=(ax*4);

                if(xval<=0){
                    xval=0;
                }
                if(xval>=925){
                    xval=925;
                }

                if(time ==2000){
                    time = 0;
                    Bitmap ob = BitmapFactory.decodeResource(getResources(),R.drawable.obst);
                    canvas.drawBitmap( ob,xval,0,null);

                }
                canvas.drawBitmap( racecar,xval,1500,null);
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener( this, accel, sensorManager.SENSOR_DELAY_NORMAL);
                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                ax=event.values[0];
                if(ax>-.5 || ax<.5){
                    ax=0;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }//GameSurface
}//Activity
