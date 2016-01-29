package io.novikov.simplecolouring;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

public class RenderView extends GLSurfaceView {
    final static String TAG = "RenderView";

    ShaderRenderer mRenderer;
    SurfaceTexture mSurfaceTexture;
    CameraHandler  mCameraHandler;


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

        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//TODO check for resource bloat
    }

    public void onSurfaceCreated(int texID) {
        mSurfaceTexture = new SurfaceTexture(texID);
        mCameraHandler = new CameraHandler(getContext().getApplicationContext(), mSurfaceTexture);
        try {
            mCameraHandler.start();
        } catch ( CameraAccessException e) {
            Util.LogError(TAG, "Exception in CameraHandler : " + e.toString());
        }
    }

    public void onSurfaceChanged( int width, int height) {
        try {
            mCameraHandler.restart();
        } catch (CameraAccessException e) {
            Util.LogError(TAG, "Exception in CameraHandler : " + e.toString());        }
    }
}
