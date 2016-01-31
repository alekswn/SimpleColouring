package io.novikov.simplecolouring;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

public class RenderView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener{
    final static String TAG = "RenderView";

    ShaderRenderer mRenderer;
    SurfaceTexture mSurfaceTexture;
    CameraHandler mCameraHandler;


    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     *
     * @param context
     * @param attrs
     */
    public RenderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRenderer = new ShaderRenderer(this);

        //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);

        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void onSurfaceCreated(int texID) {
        Util.LogDebug(TAG, "onSurfaceCreated() " + texID);
        mSurfaceTexture = new SurfaceTexture(texID);

        mCameraHandler = new CameraHandler(getContext().getApplicationContext(), mSurfaceTexture);
        try {
            mCameraHandler.start();
        } catch (CameraAccessException e) {
            Util.LogError(TAG, "Exception in CameraHandler : " + e.toString());
        }

        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    public void onSurfaceChanged(int width, int height) {
        Util.LogDebug(TAG, "onSurfaceChanged()");
/*
        try {
            mCameraHandler.restart();
        } catch (CameraAccessException e) {
            Util.LogError(TAG, "Exception in CameraHandler : " + e.toString());
        }
     */
    }

    public synchronized void updateTexture() {
        //Util.LogDebug(TAG, "Updating texture");
        mSurfaceTexture.updateTexImage();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        //Util.LogDebug(TAG, "New frame avalable");
        mRenderer.updateTexture();
        //requestRender();
    }
/*
    @Override
    public void onPause() {
        super.onPause();
        mRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
    }
*/
}