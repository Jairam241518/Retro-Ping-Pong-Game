package com.retroPingPong.retropingponggame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameViewHard extends View {
    Context context;
    float xBCor,yBCor;
    float xPCor2, yPCor2;
    Velocity velocity = new Velocity(25,32);
    Velocity velocity2 = new Velocity(25, 32);
    Handler handler;
    final long updateTime = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    float textSize = 120;
    float xPCor, yPCor;
    float oldX, oldPaddleX;
    int points = 0;
    int life = 3;
    Bitmap ball,paddle;
    int dWidth, dHeight;
    MediaPlayer bHit,bMiss,gameEnd;
    Random random;
    SharedPreferences sharedPreferences;
    Boolean audioState;




    public GameViewHard(Context context) {
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        bHit = MediaPlayer.create(context, R.raw.hit);
        bMiss = MediaPlayer.create(context, R.raw.miss);
        gameEnd = MediaPlayer.create(context, R.raw.gameover);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint.setColor(Color.GREEN);
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        random = new Random();
        xBCor = random.nextInt(dWidth-100);
        yBCor = 500;
        yPCor = (dHeight*9)/10;
        xPCor = (dWidth/2) - (paddle.getWidth())/2;
        xPCor2 = (dWidth/2) - (paddle.getWidth())/2;
        yPCor2 = (dHeight*1)/10;
        sharedPreferences = context.getSharedPreferences("my_prof", 0);
        audioState = sharedPreferences.getBoolean("audioState", true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        xBCor += velocity.getX();
        yBCor += velocity.getY();
        xPCor2 = xBCor;
        if((xBCor >= dWidth - ball.getWidth()) || xBCor <= 0 ){
            velocity.setX(velocity.getX() * -1);
        }
        if(yBCor <= 0){
            velocity.setY(velocity.getY() * -1);
            yBCor = random.nextInt(200)+500;
            xBCor = random.nextInt(dWidth-100);
            points++;
        }
        if(xPCor2 >= dWidth-paddle.getWidth() || xPCor2 <= 0){
            velocity2.setX(velocity2.getX()*-1);
        }
        if(yBCor > yPCor + paddle.getHeight()){
            xBCor = random.nextInt(dWidth - ball.getWidth() - 1) + 1;
            yBCor = 400;
            if(bMiss != null && audioState){
                bMiss.start();
            }
            velocity.setX(xVel());
            velocity.setY(32);
            life--;
            if(life==0){
                if(gameEnd != null && audioState){
                    gameEnd.start();
                }
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("points", points);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        }
        if(((xBCor+ball.getWidth()) >= xPCor) && (xBCor <= xPCor + paddle.getWidth()) && ((yBCor + ball.getHeight()) >= yPCor) && ((yBCor+ball.getHeight()) <= yPCor+paddle.getHeight())){
            if(bHit != null && audioState){
                bHit.start();
            }
            velocity.setX(velocity.getX()+1);
            velocity.setY((velocity.getY()+1)*-1);
            points++;
        }
        if(((xBCor+ball.getWidth()) >= xPCor2) && (xBCor <= xPCor2 + paddle.getWidth()) && ((yBCor + ball.getHeight()) >= yPCor2+30) && ((yBCor+ball.getHeight()) <= yPCor2+paddle.getHeight()+50)){
            if(bHit != null && audioState){
                bHit.start();
            }
            velocity.setX(velocity.getX()+1);
            velocity.setY((velocity.getY()+1)*-1);
        }
        canvas.drawBitmap(ball, xBCor, yBCor, null);
        canvas.drawBitmap(paddle, xPCor, yPCor, null);
        canvas.drawBitmap(paddle, xPCor2, yPCor2, null);
        canvas.drawText(""+points, 20, textSize, textPaint);
        if(life == 2){
            healthPaint.setColor(Color.YELLOW);
        }else if(life == 1){
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth-200, 30, dWidth-200+60*life, 80, healthPaint);
        handler.postDelayed(runnable, updateTime);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if(touchY >= yPCor){
            int action = event.getAction();
            if(action == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldPaddleX = xPCor;
            }
            if(action == MotionEvent.ACTION_MOVE){
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if(newPaddleX <= 0){
                    xPCor = 0;
                }else if(newPaddleX >= dWidth - paddle.getWidth()){
                    xPCor = dWidth - paddle.getWidth();
                }else{
                    xPCor = newPaddleX;
                }
            }
        }
        return true;
    }

    private int xVel() {
        int[] values = {-35, -30, -25, 25, 30, 35};
        int randomIndex = random.nextInt(6);
        return values[randomIndex];
    }
}
