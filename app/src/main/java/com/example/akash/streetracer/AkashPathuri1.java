package com.example.akash.streetracer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Random;

public class AkashPathuri1 extends AppCompatActivity {
    GameSurface gameSurface;
    SensorManager sensorManager;
    Sensor accel;
    Button button;
    double ax;
    double ay;
    boolean fastmode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);

        button = (Button)findViewById(R.id.button);
        
        gameSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fastmode){
                    fastmode = false;
                }
                else{
                    fastmode = true;
                }
            }
        });

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
        Bitmap racecar, brokencar, road;
        Bitmap obsacles;
        Rect rc;
        Rect ob;
        int xval =100;
        int time;
        int playtime;
        int pos=100;
        int factor=15;
        int y = 15;
        int score;
        int random;

        boolean gameOver = false;
        MediaPlayer hit,dodge;

        int screenWidth;
        int screenHeight;

        Paint paintProperty;

        public GameSurface(Context context) {
            super(context);
            holder=getHolder();
            racecar = BitmapFactory.decodeResource(getResources(),R.drawable.car);
            obsacles = BitmapFactory.decodeResource(getResources(),R.drawable.obst);
            brokencar = BitmapFactory.decodeResource(getResources(),R.drawable.bkn);
            road = BitmapFactory.decodeResource(getResources(),R.drawable.road);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;
            random= (int)(Math.random()*(screenWidth-170));

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener( this, accel, sensorManager.SENSOR_DELAY_NORMAL);

            paintProperty= new Paint();
            paintProperty.setTextSize(100);
            hit  = MediaPlayer.create(context,R.raw.hit);
            dodge = MediaPlayer.create(context,R.raw.dodge);
        }

        @Override
        public void run() {
            while (running == true){

                if (holder.getSurface().isValid() == false)
                    continue;

                Canvas canvas= holder.lockCanvas();
                canvas.drawBitmap(road,0,0,null);
                if(fastmode){
                    factor = y+25;
                }
                else{
                    factor = y;
                }
                time+=factor;
                playtime++;
                canvas.drawRGB(0,255,0);
                xval-=(ax*7);
                Rect rect = new Rect(0,0,screenWidth,screenHeight);
                if(xval<=0){
                    xval=0;
                }
                if(xval>=screenWidth-143){
                    xval=screenWidth-143;
                }
                if(time>screenHeight-100){
                    time=-100;
                    dodge.start();
                    random = (int)(Math.random()*(screenWidth-170));
                    factor++;
                    y++;
                    score++;
                }
                if (factor>30){
                    factor=30;
                    y=28;
                }
                if(playtime>=4500){
                    gameOver=true;
                    time = -500;
                }

                ob = new Rect(random,time,random+obsacles.getWidth(),time+obsacles.getHeight());
                rc = new Rect(xval,(screenHeight-400),xval+racecar.getWidth(),(screenHeight-400)+racecar.getHeight());
                if(rc.intersect(ob)){
                    time = -3000;
                    random = (int)(Math.random()*(screenWidth-170));
                    hit.start();
                }
                if(time<-500){
                    canvas.drawBitmap(brokencar,xval,screenHeight-400,null);
                }
                else{
                    canvas.drawBitmap(racecar,xval,screenHeight-400,null);
                }

                if(!gameOver) {
                    canvas.drawText("Time: "+(playtime/100), (screenWidth/2)-200, 300, paintProperty);
                }
                else{
                    canvas.drawText("Game Over", (screenWidth/2)-240,225,paintProperty);
                }
                canvas.drawBitmap( obsacles,random,time,null);
                //canvas.drawBitmap( racecar,xval,screenHeight-400,null);
                canvas.drawText("Score: "+(score),(screenWidth/2)-200,100,paintProperty);

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
                if((Math.abs(ax)<.5)){
                    ax=0;
                }
                ay = event.values[1];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }//GameSurface
}//Activity
