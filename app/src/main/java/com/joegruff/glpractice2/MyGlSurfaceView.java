package com.joegruff.glpractice2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import static android.content.ContentValues.TAG;

/**
 * Created by joe on 1/1/17.
 */

public class MyGlSurfaceView extends GLSurfaceView {
    private static MyRenderer mRenderer;
    private static Context mContex;
    private static boolean mPressingDoubleDown = false;

    //keep some variables between calls to on touch
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousXdiff;
    private float mPreviousYdiff;
    //wont let me declare these in on touch, complains about no initialization
    private float x2;
    private float y2;
    private int mActionIndex;

    public MyGlSurfaceView(Context context) {
        super(context);
        mContex = context;
    }

    public void setRenderer(MyRenderer renderer) {
        mRenderer = renderer;
        super.setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //get the x and y values from first touch
        float x = event.getX(0);
        float y = event.getY(0);

        //different actions for different touches
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //this is the second finger down, use for scaling
            case MotionEvent.ACTION_POINTER_DOWN:
                //get the action event index
                mActionIndex = event.getActionIndex();
                //set the original prevdiffs to calculate future diffs of this instance with
                x2=event.getX(mActionIndex);
                y2=event.getY(mActionIndex);
                mPreviousXdiff=Math.abs(x2-x);
                mPreviousYdiff=Math.abs(y2-y);
                mPressingDoubleDown = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPressingDoubleDown = false;
                //this only makes sure the first finger doesnt cause orientation to shift...
                mPreviousX = x;
                mPreviousY = y;
                break;
            case MotionEvent.ACTION_DOWN:
                //one finger down
                    mPreviousX = x;
                    mPreviousY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPressingDoubleDown) {
                    x2=event.getX(mActionIndex);
                    y2=event.getY(mActionIndex);
                    float newXdiff = Math.abs(x2-x);
                    float newYdiff = Math.abs(y2-y);
                    //scale image but not negative
                    float angle = mRenderer.getZAngle()+(mPreviousXdiff-newXdiff+mPreviousYdiff-newYdiff)/-200;
                    if (angle < 0) {
                        angle = 0;
                    }
                    mRenderer.setZAngle(angle);
                    mPreviousXdiff = newXdiff;
                    mPreviousYdiff = newYdiff;
                } else {
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    //rotate image
                    mRenderer.setXAngle(mRenderer.getXAngle() % 180 + dx / 50);
                    mRenderer.setYAngle(mRenderer.getYAngle() % 180 + dy / 50);
                    Log.d(TAG, "onTouchEvent: " + mRenderer.getXAngle());
                    mPreviousX = x;
                    mPreviousY = y;
                }
                requestRender();
        }
        return true;
    }
}
