package com.cyb.cyb.cyb.myviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 2017/2/7.
 */

public class MyFlowlayout extends ViewGroup{


    public MyFlowlayout(Context context) {
        super(context);
    }

    public MyFlowlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFlowlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //
        int width = 0;int height = 0;
        int linewidth=0;int lineheight = 0;


        int cCount = getChildCount();
        for(int i=0;i<cCount;i++){
            View child = getChildAt(i);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            MarginLayoutParams cParams = (MarginLayoutParams) child.getLayoutParams();
            int cWidth = child.getMeasuredWidth()+cParams.leftMargin+cParams.rightMargin;
            int cHeight  = child.getMeasuredHeight()+cParams.topMargin+cParams.bottomMargin;

            if (linewidth+cWidth>widthSize){//如果超出了宽度//换行
                //1.总高度加上上一行的高度
                height+=lineheight;//高度相加
                width = Math.max(width,linewidth);//宽度取较大值
                //2.宽度=子控件宽度
                linewidth=cWidth;
                lineheight=cHeight;
            }else{//没超出宽度
                linewidth+=cWidth;//行宽相加
                lineheight=Math.max(lineheight,cHeight);
            }

            if (i==(cCount-1)){
                width = Math.max(width,lineheight);
                height+=lineheight;
            }
            setMeasuredDimension((widthMode==MeasureSpec.EXACTLY?widthSize:width),(heightMode==MeasureSpec.EXACTLY?heightSize:height));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("8888","onLayout");
        int cCount = getChildCount();
        int width = getWidth();
        int leftwidth=0;
        int topHeight=0;
        int lineHeight=0;
        for (int i=0;i<cCount;i++){
            int cl=0,cr=0,ct=0,cb=0;
            View child = getChildAt(i);
            MarginLayoutParams cParams = (MarginLayoutParams) child.getLayoutParams();
            int cWidth = child.getMeasuredWidth()+cParams.leftMargin+cParams.rightMargin;
            int cHeight = child.getMeasuredHeight()+cParams.topMargin+cParams.bottomMargin;
            Log.i("8888",leftwidth+cWidth+"");
            if (leftwidth+cWidth>width){//需要换行
                Log.i("8888",i+"换行");
                topHeight+=lineHeight;//计算上方高度
                leftwidth=0;
                lineHeight=cHeight;//
                cl=leftwidth+cParams.leftMargin;
                ct=topHeight+cParams.topMargin;
                leftwidth=cWidth;
            }else{//不需要换行
                Log.i("8888",i+"不换行");
                cl = leftwidth+cParams.leftMargin;
                ct = topHeight+cParams.topMargin;
                lineHeight=Math.max(lineHeight,cHeight);//计算行
                leftwidth+=cWidth;
            }

            cr=cl+child.getMeasuredWidth();
            cb=ct+child.getMeasuredHeight();
            child.layout(cl,ct,cr,cb);
        }

    }

}
