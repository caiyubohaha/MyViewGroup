package com.cyb.cyb.cyb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by user on 2017/2/8.
 */

public class VerticalLinearLayout extends ViewGroup {
    /**
     *屏幕的高度
     */
    private int mScreenHeight;
    /**
     * 手指按下时的getScrollY
     */
    private int mScrollStart;

    /**
     * 手指抬起来的getScrollY
     */
    private int mScrollEnd;

    /**
     *记录移动时的Y
     */
    private int mLastY;

    /**
     *滚动的辅助类
     */
    private Scroller mScroller;

    /**
     *是否在滚动
     */
    private boolean isScrolling;

    /**
     *加速检测
     * */
    private VelocityTracker mVelocityTracker;

    /**
     *记录当前页
     */
    private int currentPage=0;
    private OnPageChangeListener mOnPageChangeListener;
    public VerticalLinearLayout(Context context) {
        super(context);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * 获得屏幕的高
         */
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        //初始化
        mScroller = new Scroller(context);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i=0;i<count;i++){
            View child = getChildAt(i);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed){
            int childcount = getChildCount();
            MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            lp.height = mScreenHeight*childcount;
            setLayoutParams(lp);

            for (int i=0;i<childcount;i++){
                View child = getChildAt(i);
                if (child.getVisibility()!=View.GONE){
                    child.layout(l,i*mScreenHeight,r,(i+1)*mScreenHeight);//调用每个子布局的layout
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
       if (isScrolling){//如果正在滚动，调用父类的onTouchEvent
           return super.onTouchEvent(event);
       }
        int action = event.getAction();
        int y = (int) event.getY();
        obtainVelocity(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mScrollStart = getScrollY();
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                int dy = mLastY-y;
                //边界值检查
                int scrollY = getScrollY();
                //已经到达顶端,下拉多少就往上滚多少
                if (dy<0&&scrollY+dy<0){
                    dy=-scrollY;
                }
                //已经到达底部上拉多少就往下滚多少
                if (dy>0&&scrollY+dy>getHeight()-mScreenHeight){
                    dy=getHeight()-mScreenHeight-scrollY;
                }
                scrollBy(0,dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                mScrollEnd = getScrollY();
                int dScrlooY=mScrollEnd-mScrollStart;
                if (wantScrollToNext())//手指上滑到下一页
                {
                    if (shouldScrollToNext()){
                        mScroller.startScroll(0,getScrollY(),0,mScreenHeight-dScrlooY);
                    }else{
                        mScroller.startScroll(0,getScrollY(),0,-dScrlooY);
                    }
                }

                if (wantScrollToPre())//手指下滑到上一页
                {
                    if (shouldScrollToPre()){
                        mScroller.startScroll(0,getScrollY(),0,-mScreenHeight-dScrlooY);
                    }else{
                        mScroller.startScroll(0,getScrollY(),0,-dScrlooY);
                    }
                }
                isScrolling = true;
                postInvalidate();
                recycleVelocity();
                break;
        }
        return true;
    }

    /**
     * 是否想到上一页
     */
    private boolean wantScrollToPre(){
        return mScrollEnd<mScrollStart;
    }
    /**
     * 根据滚动距离判断是否能够滚动到上一页
     */
    private boolean shouldScrollToPre(){
        return mScrollStart-mScrollEnd>mScreenHeight/2||Math.abs(getVelocity())>600;
    }
    /**
     * 是否想要到到下一页
     */
    private boolean wantScrollToNext(){
        //return mScrollEnd-mScrollStart>mScreenHeight/2||Math.abs(getVelocity())>600;
        return mScrollStart<mScrollEnd;
    }

    /**
     * 是否可以到下一页
     */
    private boolean shouldScrollToNext(){
        return mScrollEnd-mScrollStart>mScreenHeight/2||Math.abs(getVelocity())>600;
    }

    /**
     * 获取Y方向的加速度
     * @return
     */
    private int getVelocity(){
        mVelocityTracker.computeCurrentVelocity(1000);
        return (int) mVelocityTracker.getYVelocity();
    }
    /**
     * 释放资源
     */
    private void recycleVelocity(){
        if (mVelocityTracker!=null){
            mVelocityTracker.recycle();
            mVelocityTracker=null;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()){
            scrollTo(0,mScroller.getCurrY());
            postInvalidate();
        }else{
            int position = getScrollY()/mScreenHeight;
            if (position!=currentPage){
                if (mOnPageChangeListener!=null) {
                    currentPage = position;
                    mOnPageChangeListener.onPageChange(currentPage);
                }
            }
            isScrolling=false;
        }
    }


    /**
     * 初始化加速度检测器
     */
    private void obtainVelocity(MotionEvent event){
        if (mVelocityTracker==null){
            mVelocityTracker=VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 设置回调接口
     * @param onPageChangeListener
     */
    public void setmOnPageChangeListener(OnPageChangeListener onPageChangeListener){
        mOnPageChangeListener =  onPageChangeListener;
    }


    /**
     * 回调接口
     */
    public interface OnPageChangeListener{
        void onPageChange(int currentPage);
    }
}
