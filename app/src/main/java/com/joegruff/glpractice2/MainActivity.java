package com.joegruff.glpractice2;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by joe on 1/1/17.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyRenderer renderer = new MyRenderer(this);
        MyGlSurfaceView surfaceView = new MyGlSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(renderer);
        setContentView(surfaceView);
    }
}
