package com.joegruff.glpractice2;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by joe on 1/2/17.
 */

//loads opengl programs
public class RenderProgram {
    private static final String TAG = "RenderProgram";

    private int mProgram, mVertexShader, mPixelShader;

    private String mVertexS,mFragmentS;

    //from strings
    public RenderProgram(String vertexS, String fragmentS) {
        setup(vertexS, fragmentS);
    }

    //from files
    public RenderProgram(int vID, int fID, Context context) {
        StringBuffer vs = new StringBuffer();
        StringBuffer fs = new StringBuffer();

        //read files
        try {
            InputStream inputStream = context.getResources().openRawResource(vID);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String read = in.readLine();
            while (read != null) {
                vs.append(read + "\n");
                read = in.readLine();
            }

            vs.deleteCharAt(vs.length()-1);

            inputStream = context.getResources().openRawResource(fID);
            in = new BufferedReader(new InputStreamReader(inputStream));

            read = in.readLine();
            while (read != null) {
                fs.append(read + "\n");
                read = in.readLine();
            }
            fs.deleteCharAt(fs.length()-1);
        } catch (Exception e) {
            Log.d(TAG, "RenderProgram: couldnt read .glsl file");
        }
        setup(vs.toString(), fs.toString());
    }

    private void setup(String vs, String fs) {
        this.mVertexS = vs;
        this.mFragmentS = fs;

        if (createProgram() != 1){
            throw new RuntimeException("Error at creating shaders");
        }
    }

    private int createProgram() {
        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER,mVertexS);
        if (mVertexShader == 0){
            return 0;
        }

        mPixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER,mFragmentS);
        if (mPixelShader == 0){
            return 0;
        }

        mProgram = GLES20.glCreateProgram();
        if (mProgram != 0) {
            GLES20.glAttachShader(mProgram,mVertexShader);
            GLES20.glAttachShader(mProgram,mPixelShader);
            GLES20.glLinkProgram(mProgram);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram,GLES20.GL_LINK_STATUS,linkStatus,0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "createProgram: Couldnt create \n" + GLES20.glGetProgramInfoLog(mProgram));
                GLES20.glDeleteProgram(mProgram);
                mProgram = 0;
                return 0;
            }
        } else {
            Log.d(TAG, "createProgram: could not create program");
        }
        return 1;
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader,source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compiled,0);
            if (compiled[0] == 0) {
                Log.e(TAG, "loadShader: couldnt compile shader " + shaderType + ":\n" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public int getProgram() {
        return mProgram;
    }
}
