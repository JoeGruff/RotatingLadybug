package com.joegruff.glpractice2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by joe on 1/1/17.
 */

public class MyRenderer implements GLSurfaceView.Renderer {
    private static Context mContext;
    private Ladybug mLadybug;
    private static RenderProgram mProgram;
    private static int mProgramHandler;
    private int mMVPMatrixHandle;
    private int mMVMMatrixHandle;

    private int mHasTextureAttribute;
    private int mMonkeyPositionAttribute;
    private int mMonkeyNormalAttribue;
    private int mMonkeyColorAttribute;
    private int mShellTextureAttribute;
    private int mShellTextureUniform;
    private int mIndexAttribute;
    private int[] mBoneMatrixHandle = new int[4];

    private float mXAngle;


    private float mYAngle;

    public float getZAngle() {
        return mZAngle;
    }

    public void setZAngle(float ZAngle) {
        mZAngle = ZAngle;
    }

    private float mZAngle;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];

    public MyRenderer(Context context) {
        mContext = context;
    }


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mZAngle=1f;
        mXAngle=0f;
        mYAngle=0f;
        GLES20.glClearColor(.1254f,.8549f,.2000f,1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mProgram = new RenderProgram(R.raw.vector_shader,R.raw.fragment_shader,mContext);
        mProgramHandler = mProgram.getProgram();
        mLadybug = new Ladybug(mContext);
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        //used to set rotation matrix, should move to objects area
        final float[] scratch = new float[16];

        Matrix.setLookAtM(mViewMatrix,0,0,0,-5f,0f,0f,0f,0f,1f,0f);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mViewMatrix,0);
        float[] YrotationMatrix = new float[]{1,0,0,0,0,(float)Math.cos(mYAngle),(float)Math.sin(mYAngle)*-1,0,0,(float)Math.sin(mYAngle),(float)Math.cos(mYAngle),0,0,0,0,1};
        float[] XrotationMatrix = new float[]{(float)Math.cos(mXAngle),0,(float)Math.sin(mXAngle),0,0,1,0,0,(float)Math.sin(mXAngle)*-1,0,(float)Math.cos(mXAngle),0,0,0,0,1};
        Matrix.multiplyMM(mRotationMatrix,0,YrotationMatrix,0,XrotationMatrix,0);
        Matrix.scaleM(mRotationMatrix,0,mZAngle,mZAngle,mZAngle);
        Matrix.multiplyMM(scratch,0,mMVPMatrix,0,mRotationMatrix,0);

        mMonkeyPositionAttribute= GLES20.glGetAttribLocation(mProgramHandler,"aPosition");
        mMonkeyNormalAttribue = GLES20.glGetAttribLocation(mProgramHandler, "aNormal");
        mMonkeyColorAttribute = GLES20.glGetAttribLocation(mProgramHandler, "aColor");
        mShellTextureAttribute = GLES20.glGetAttribLocation(mProgramHandler, "aTexCoordinate");
        mHasTextureAttribute = GLES20.glGetAttribLocation(mProgramHandler, "aHasTexture");
        mIndexAttribute = GLES20.glGetAttribLocation(mProgramHandler, "aIndex");



        //light position code
        int lightPos;
        lightPos = GLES20.glGetUniformLocation(mProgramHandler, "uLightPos");
        GLES20.glUniform3fv(lightPos,1,new float[]{0.25f,1.5f,-0.75f},0);

        //temp matrix code
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandler, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false,scratch,0);
        Matrix.multiplyMM(scratch,0,mViewMatrix,0,mRotationMatrix,0);
        mMVMMatrixHandle = GLES20.glGetUniformLocation(mProgramHandler, "uMVMMatrix");
        GLES20.glUniformMatrix4fv(mMVMMatrixHandle,1,false,scratch,0);
        mShellTextureUniform = GLES20.glGetUniformLocation(mProgramHandler, "uTexture");
        for (int i = 0; i < mBoneMatrixHandle.length; i++) {
            mBoneMatrixHandle[i] = GLES20.glGetUniformLocation(mProgramHandler, "uBoneMatrix[" + i + "]");
        }


        GLES20.glUseProgram(mProgramHandler);

        mLadybug.render(mMonkeyPositionAttribute,mMonkeyNormalAttribue,mMonkeyColorAttribute, mShellTextureAttribute, mIndexAttribute, mShellTextureUniform, mBoneMatrixHandle, false);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int i, int i1) {
        final float ratio = (float)i/i1;
        GLES20.glViewport(0,0,i,i1);
        Matrix.frustumM(mProjectionMatrix,0,-ratio,ratio,-1f,1f,1f,10f);
    }

    public float getXAngle(){
        return mXAngle;
    }
    public void setXAngle(float angle) {
        mXAngle = angle;
    }
    public float getYAngle() {
        return mYAngle;
    }
    public void setYAngle(float YAngle) {
        mYAngle = YAngle;
    }
}
