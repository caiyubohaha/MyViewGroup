package com.cyb.cyb.cyb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by user on 2017/2/9.
 */

public class MyVerticalLinearLayout extends ViewGroup {
    //手指按下位置
    public int mStartY=0;
    //手指抬起位置
    public int mEndY=0;
    //屏幕高度
    public int mScreenHeight=0;
    public Scroller mScroller=null;
    //子控件数量
    public int mCount=0;

    public boolean scrolling=false;
    //记录时时的的位置
    int mScrollY = 0;
    //记录开始位置
    int mStrtScrollY=0;
    //记录结束的位置
    int mEndScrollY = 0;
    //
    int mScrolldy=0;
    int mPage=0;
    public OnPageChangeListener mOnPageChangeListener=null;

    /**
     *加速度检测
     */
    public VelocityTracker mVelocityTracker;
    private int mLastY;

    public MyVerticalLinearLayout(Context context) {
        super(context);
    }

    public MyVerticalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //1.初始化
        mScroller = new Scroller(context);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        mScreenHeight = metrics.heightPixels;
    }

    public MyVerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        mCount = getChildCount();
        setMeasuredDimension(widthMeasureSpec,mCount*mScreenHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed){

            //设置子控件
            for (int i = 0; i < mCount; i++){
                getChildAt(i).layout(0,i*mScreenHeight,getWidth(),(i+1)*mScreenHeight);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (scrolling) {
            return super.onTouchEvent(event);
        }
        //相对坐标相对容器的
        int Y = (int) event.getY();
        //绝对坐标
        int rawY = (int) event.getRawY();
        //
        mScrollY = getScrollY();
        int dy=0;
        int y = (int) event.getY();
        obtainVolecity(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //记录开始的位置要和抬起的位置进行比较
                mStrtScrollY=getScrollY();
                mStartY = (int) event.getRawY();



                break;
            case MotionEvent.ACTION_MOVE:

                if (mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                dy = rawY-mStartY;
                //边界的时候应该被拒绝滑动
                if ((dy>0&&mScrollY-dy<=0)||(dy<0&&mScrollY-dy>(mCount-1)*mScreenHeight)){
                    dy = 0;
                }
                scrollBy(0,-dy);
                mStartY = rawY;


                
                /*int dxy = mLastY-y;
                //边界值检查
                int scrollY = getScrollY();
                //已经到达顶端,下拉多少就往上滚多少
                if (dxy<0&&scrollY+dxy<0){
                    dxy = -scrollY;
                }
                //已经到达底部上拉多少就往下滚多少
                if (dxy>0&&scrollY+dxy>getHeight()-mScreenHeight){
                    //dy=getHeight()-mScreenHeight-scrollY;
                    dxy  = 0;
                }
                scrollBy(0,dxy);
                mLastY = y;*/
                
                break;
            case MotionEvent.ACTION_UP:
                //记录结束的位置

                mEndScrollY = getScrollY();
                //手指的位移
                mScrolldy = mEndScrollY-mStrtScrollY;//如果<0手指往下滑如果>0手指往上滑
                Log.i("8888",mScrolldy+"----->"+mScreenHeight);

                if (wantScrollToPre()){//手指往下滑可能加载上一页mScrolldy<0
                    if (-mScrolldy>(mScreenHeight/2)||Math.abs(getVolecity())>600){//加载上一页
                        mScroller.startScroll(0,mEndScrollY,0,-(mScreenHeight+mScrolldy));
                    }else{
                        mScroller.startScroll(0,mEndScrollY,0,-mScrolldy);
                    }
                }else if (wantScrollToNext()){//手指往上滑可能加载下一页mScrolldy>0
                   Log.i("8888",Math.abs(getVolecity())+"");
                    if (mScrolldy>(mScreenHeight/2)||Math.abs(getVolecity())>600){//加载下一页
                        mScroller.startScroll(0,mEndScrollY,0,mScreenHeight-mScrolldy);
                    }
                    else{
                        mScroller.startScroll(0,mEndScrollY,0,-mScrolldy);
                    }
                }
                scrolling=true;
                postInvalidate();
                recycleVolecity();
                break;
        }




        return true;
    }

    @Override
    public void computeScroll() {
        //super.computeScroll();
        if (mScroller.computeScrollOffset()){
            scrollTo(0,mScroller.getCurrY());
            invalidate();
        }else{
            scrolling=false;
            int page = getScrollY()/mScreenHeight;
            if (page!=mPage&&mOnPageChangeListener!=null) {
                mOnPageChangeListener.onPageChange(page);
                mPage = page;
            }
        }

    }

    /**移动到页**//*
    private void moveToPage() {
        //scrollTo(0,mPage*mScreenHeight);
        int dy=0;//移动的位移，-向下+向上(内容)
        if (mScrolldy<0) {//滑到上一页（内容向下）
            dy= -(mScreenHeight+mScrolldy);
        }else if (mScrolldy>0){//滑到下一页（内容向上）
            dy = mScreenHeight-mScrolldy;
        }
        mScroller.startScroll(0,mEndScrollY, 0, dy );
        Log.i("8888",mPage+"--->"+mPage*mScreenHeight);
        postInvalidate();
    }*/

    /**
     *添加回调
     */
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener){
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 回调接口
     */
    public interface OnPageChangeListener{
        public void onPageChange(int currentPage);
    }

    /**
     * 释放资源
     */
    public void recycleVolecity(){
        if (mVelocityTracker!=null){
            mVelocityTracker.recycle();
            mVelocityTracker=null;
        }
    }
    /**
     * 初始化速度检测器
     */
    public void obtainVolecity(MotionEvent event){
        if (mVelocityTracker==null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }
    /**
     * 获取Y加速度
     */
    public int getVolecity(){
        mVelocityTracker.computeCurrentVelocity(1000);
        return (int) mVelocityTracker.getYVelocity();
    }
    /**
     * 是否想到上一页
     */
    public boolean wantScrollToPre(){
        return mScrolldy<0;
    }
    /**
     * 是否想到下一页
     */
    public boolean wantScrollToNext(){
        return mScrolldy>0;
    }
    /**
     * 是否可以到下一页
     */
    public boolean shouldSCrlooTopre(){
        return -mScrolldy>(mScreenHeight/2);
    }
}
