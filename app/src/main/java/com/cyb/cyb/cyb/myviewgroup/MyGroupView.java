package com.cyb.cyb.cyb.myviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.getSize;

/**
 * Created by user on 2017/2/7.
 */

public class MyGroupView extends ViewGroup {


    public MyGroupView(Context context) {
        super(context);
    }

    public MyGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //为了支持margin直接使用系统的MarginLayoutParams//指定了MyViewgroup的LayoutParams
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }
    //在onMeasure中计算childview测量值以及模式，并设置自己的宽和高
    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得此viewgroup上级容器为其推荐的宽和高以及测量模式
       int widthMode =   MeasureSpec.getMode(widthMeasureSpec);
       int widthSize =   getSize(widthMeasureSpec);
       int heightMode = MeasureSpec.getMode(heightMeasureSpec);
       int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //计算出所有childview的宽和高
        measureChildren(widthMeasureSpec,heightMeasureSpec);


        /**如果worp_content设置的宽和高**/
        int width = 0;
        int height = 0;

        int cCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;
        //用于计算左边两个childview的高度
        int lHeight = 0;
        //用于计算右边两个childview的高度，最终高度取大的那个
        int rHeight = 0;

        //用于计算上边两个childview的宽度
        int tWidth = 0;
        //用于计算下边两个childview的宽度，最终取较大的那个
        int bWidth = 0;

        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0;i<cCount;i++){
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();
            //上边两个childview
            if (i==0||i==1){
                tWidth+=cWidth+cParams.leftMargin+cParams.rightMargin;
            }
            //下边两个childview
            if (i==2||i==3){
                bWidth+=cWidth+cParams.leftMargin+cParams.rightMargin;
            }
            //左边两个childview
            if (i==0||i==2) {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }
            //右边两个
            if (i==1||i==3){
            rHeight+=cHeight+cParams.bottomMargin+cParams.topMargin;
            }
        }

        width = Math.max(tWidth,bWidth);
        height=Math.max(lHeight,rHeight);
        /**
        * 如果是wrap_content设置为我们计算的值
        * 否则：直接设置为父容器计算的值
        */
        setMeasuredDimension((widthMode==MeasureSpec.EXACTLY?widthSize:width),(heightMode==MeasureSpec.EXACTLY?heightSize:height));
    }

    //onlayout方法是对所有的childview进行定位
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount =  getChildCount();
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;
        for (int i=0;i<cCount;i++){
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            int cl=0,ct=0,cr=0,cb=0;
            switch(i){
                case 0:
                    cl = cParams.leftMargin;
                    ct = cParams.topMargin;
                    break;
                case 1:
                    cl = getWidth()-cWidth-cParams.rightMargin;
                    ct = cParams.topMargin;
                    break;
                case 2:
                    cl = cParams.leftMargin;
                    ct = getHeight()-cParams.bottomMargin-cHeight;
                    break;
                case 3:
                    cl = getWidth()-cWidth-cParams.rightMargin;
                    ct = getHeight()-cParams.bottomMargin-cHeight;
                    break;
            }
            cr = cl+cWidth;
            cb = ct+cHeight;
            childView.layout(cl,ct,cr,cb);

        }

    }
}
