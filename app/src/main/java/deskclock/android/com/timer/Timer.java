package deskclock.android.com.timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chenghao on 2017/4/6.
 */
public class Timer extends View implements View.OnTouchListener{

    private static final int SCALE_LINE_LENGTH =25; //刻度线长
    private static final int SCALE_LINE_WIDTH = 3; //刻度线宽
    private static final double MIN_DEGREE = 6;//刻度之间的角度
    private static final int RED_POINT_RADIUS=20;//红点的半径
    private static final int NUMBER_SIZE=40;//刻度的字体大小
    private static final int TIME_SIZE=60;//时间字体大小
    private static final String ZERO="0";
    private int mWidth=0;
    private int mheight=0;
    private int mX;
    private int mY;
    private int mRadius;
    private Paint mPaint;
    private Paint mRedLinePaint;
    private Paint mFill;
    private Paint timePaint;

    private double redPointDegree=0;//记录红点的角度
    private int lastLineNum=0;//记录红点上一次的刻度
    private int lineNum=0;//记录红点当前刻度
    private boolean isRedPointSelected=false;//判断红点被点击
    private int mHour=0;
    private int mMinute=0;
    private int mSecend=0;
    private final static String[] times={"15m","30m","45m"};
    public Timer(Context context) {
        super(context);
        init();
    }

    public Timer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Timer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Timer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mheight=MeasureSpec.getSize(heightMeasureSpec);
    }

    private void init(){
        mPaint=new Paint();
        mPaint.setStrokeWidth(SCALE_LINE_WIDTH);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        mRedLinePaint=new Paint();
        mRedLinePaint.setStrokeWidth(SCALE_LINE_WIDTH);
        mRedLinePaint.setAntiAlias(true);
        mRedLinePaint.setColor(Color.RED);

        mFill=new Paint();
        mFill.setAntiAlias(true);
        mFill.setColor(Color.RED);
        mFill.setStyle(Paint.Style.FILL);

        this.setOnTouchListener(this);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mX=mWidth/2;
        mY=mheight/2;
        mRadius=Math.min(mX,mY)*7/10;

        drawLines(canvas);
        drawNum(canvas);
        drawRedPoint(canvas);
    }

    private void drawLines(Canvas canvas){
        double mRedPointDegree=redPointDegree*180/Math.PI;
        int mStartX=mX;
        int mStartY=mY-mRadius+SCALE_LINE_LENGTH;
        int mDestX=mStartX;
        int mDeskY=mY - mRadius;
        Paint LinePaint;
        for( int degree=0;degree<360;) {
            if (degree < mRedPointDegree && mRedPointDegree != 0) {
                LinePaint=mRedLinePaint;
            } else {
                LinePaint=mPaint;
            }
            if((degree%90)==0){
                //时间为0,15min，30min，45min
                canvas.drawLine(mStartX, mStartY, mDestX, mDeskY-10, LinePaint);
            }else{
                canvas.drawLine(mStartX, mStartY, mDestX, mDeskY, LinePaint);
            }
            degree+=MIN_DEGREE;
            canvas.rotate((float) MIN_DEGREE,mX,mY);
        }
    }

    private void drawNum(Canvas canvas){
        int nX=mX-15;
        int nY=mY - mRadius-30;
        mPaint.setTextSize(NUMBER_SIZE);
        canvas.drawText(String.valueOf(mHour)+":"+String.valueOf(mMinute),nX,nY,mPaint);
        for(int i=1;i<=3;i++){
            if(i==1){
                nY=mY;
                nX=mX+mRadius+30;
            }else
            if(i==2){
                nX=mX-NUMBER_SIZE;
                nY=mY+mRadius+NUMBER_SIZE+30;
            }else
            if(i==3){
                nY=mY;
                nX=mX-mRadius-30-NUMBER_SIZE*2;
            }
            canvas.drawText(times[i-1],nX,nY,mPaint);
        }
    }

    private void drawRedPoint(Canvas canvas){
        int radius=mRadius*11/10;
        int rX = (int) (mX + radius * Math.sin(redPointDegree));
        int rY = (int) (mY - radius * Math.cos(redPointDegree));
        canvas.drawCircle(rX,rY,RED_POINT_RADIUS,mFill);
    }

    private void drawTime(Canvas canvas){
        //计算文本框的宽高
        int textWidth=TIME_SIZE*9;
        int textHeigth=TIME_SIZE*2;

        if(mHour==0){

        }

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        double degree=0;
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                setJudgeRedPoint(motionEvent.getX(),motionEvent.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRedPointSelected) {
                    if(mHour<=0&&mMinute==0&&motionEvent.getX()<mX){
                        //当时间为0，禁止用户向做滑动
                    }else {
                        degree = getDegree(motionEvent.getX(), motionEvent.getY());
                        Log.d("chenghao:Timer", "degree=" + String.valueOf(degree));
                        setRedPointDegree(degree);
                        this.postInvalidate();
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                isRedPointSelected=false;
                break;
            default:
                break;
        }
        return true;
    }

    //判断用户是否拖住红点
    private void setJudgeRedPoint(float curX,float curY){
        //计算用户点击坐票距离红点圆心的距离，若小于半径，则选中红点
        int radius=mRadius*11/10;
        int rX = (int) (mX + radius * Math.sin(redPointDegree)); //红点x坐标
        int rY = (int) (mY - radius * Math.cos(redPointDegree)); //红点y坐标

        double distance=Math.pow(curX-rX,2)+Math.pow(curY-rY,2);
        Log.d("chenghao:","distance"+String.valueOf(distance));
        if(distance>(RED_POINT_RADIUS*RED_POINT_RADIUS*10)){
            isRedPointSelected=false;
        }else {
            isRedPointSelected=true;
        }

    }

    //获取用户手指的角度
    public double getDegree(float curX,float curY){
        float offsetX=(int) curX-mX;
        float offsetY=(int) curY-mY;
        Log.d("chenghao:loaction","x="+String.valueOf(curX)+",y="+String.valueOf(curY));
        double degree=0;
        if(offsetX>0){
            if(offsetY==0){
                degree=Math.PI/2;
            }else {
                float tan=offsetX/Math.abs(offsetY);
                Log.d("chenghao:valuer","offsetX="+String.valueOf(offsetX)+",offsetY="+String.valueOf(offsetY)+
                        ",tan="+String.valueOf(tan));
                degree=Math.atan(tan);
                Log.d("chenghao:atan",String.valueOf(degree));
                if(offsetY>0)
                    degree=Math.PI-degree;
            }
        }
        else {
            if(offsetY==0){
                degree=-Math.PI/2;
            }else {
                float tan=Math.abs(offsetY)/offsetX;
                Log.d("chenghao:valuer","offsetX="+String.valueOf(offsetX)+",offsetY="+String.valueOf(offsetY)+
                        ",tan="+String.valueOf(tan));
                degree=Math.atan(tan);
                Log.d("chenghao:atan",String.valueOf(degree));
                if(offsetY>0){
                    degree=degree+Math.PI*3/2;
                }else {
                    degree=-degree+Math.PI*3/2;
                }
            }

        }
        lastLineNum=lineNum;
        lineNum=(int)(degree*30/Math.PI);
        mMinute=lineNum;
        if(mMinute==60){
            mMinute=0;
            mHour++;
        }
        if(mMinute==0&&lastLineNum==59){
            mHour++;
        }else
        if(mMinute==59&&lastLineNum==0){
            mHour--;
        }

        degree=lineNum*Math.PI/30;
        return degree;
    }

    public double getRedPointDegree() {
        return redPointDegree;
    }

    public void setRedPointDegree(double redPointDegree) {
        this.redPointDegree = redPointDegree;
    }
}
