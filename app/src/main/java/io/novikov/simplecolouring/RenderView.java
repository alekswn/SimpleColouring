package io.novikov.simplecolouring;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

public class RenderView extends GLSurfaceView {
    ShaderRenderer mRenderer;
    SurfaceTexture mSurfaceTexture;


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

        mSurfaceTexture.setDefaultBufferSize(100, 100);
        Surface surface = new Surface(mSurfaceTexture);


    }

    public void onSurfaceChanged( int width, int height) {
        //TODO
    }
}
