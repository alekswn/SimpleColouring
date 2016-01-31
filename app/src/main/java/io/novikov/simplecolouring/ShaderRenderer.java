package io.novikov.simplecolouring;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by al on 1/27/16.
 */
public class ShaderRenderer implements GLSurfaceView.Renderer
{
    private final String TAG = "ShaderRenderer";
    private final Context context;
    private final AtomicBoolean isUpdateNeeded;

    private class GLTexture {
        private int mTextureHandle;

        public int getTextureId(){
            return mTextureHandle;
        }

        public void init(){

            int[] mTextureHandles = new int[1];
            GLES20.glGenTextures(1, mTextureHandles, 0);
            mTextureHandle = mTextureHandles[0];


            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureHandle);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }

        public int loadTexture(final Context context, final int resourceId)
        {
            final int[] textureHandle = new int[1];

            GLES20.glGenTextures(1, textureHandle, 0);

            if (textureHandle[0] != 0)
            {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;   // No pre-scaling

                // Read in the resource
                final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

                // Bind to the texture in OpenGL
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle[0]);

                // Set filtering
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

                // Load the bitmap into the bound texture.
                GLUtils.texImage2D(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0, bitmap, 0);

                // Recycle the bitmap, since its data has been loaded into OpenGL.
                bitmap.recycle();
            }

            if (textureHandle[0] == 0)
            {
                throw new RuntimeException("Error loading texture.");
            }

            return textureHandle[0];
        }

    };

    final RenderView renderView;
    final GLTexture  glTexture;
    final Shader     shader;

    public ShaderRenderer(RenderView view) {
        renderView = view;
        glTexture = new GLTexture();
        context = view.getContext().getApplicationContext();
        shader = new PassthroughSurfaceShader();
        isUpdateNeeded = new AtomicBoolean(true);
    }

    public void updateTexture() {
        isUpdateNeeded.lazySet(true);
    }

    /**
     * Called when the surface is created or recreated.
     * <p/>
     * Called when the rendering thread
     * starts and whenever the EGL context is lost. The EGL context will typically
     * be lost when the Android device awakes after going to sleep.
     * <p/>
     * Since this method is called at the beginning of rendering, as well as
     * every time the EGL context is lost, this method is a convenient place to put
     * code to create resources that need to be created when the rendering
     * starts, and that need to be recreated when the EGL context is lost.
     * Textures are an example of a resource that you might want to create
     * here.
     * <p/>
     * Note that when the EGL context is lost, all OpenGL resources associated
     * with that context will be automatically deleted. You do not need to call
     * the corresponding "glDelete" methods such as glDeleteTextures to
     * manually delete these lost resources.
     * <p/>
     *
     * @param gl     the GL interface. Use <code>instanceof</code> to
     *               test if the interface supports GL11 or higher interfaces.
     * @param config the EGLConfig of the created surface. Can be used
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Util.LogDebug(TAG, "onSurfaceCreated()");
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
        shader.init(context);
        //glTexture.init();
        glTexture.loadTexture(context, R.mipmap.ic_launcher);
        renderView.onSurfaceCreated(glTexture.getTextureId());
    }

    /**
     * Called when the surface changed size.
     * <p/>
     * Called after the surface is created and whenever
     * the OpenGL ES surface size changes.
     * <p/>
     * Typically you will set your viewport here. If your camera
     * is fixed then you could also set your projection matrix here:
     * <pre class="prettyprint">
     * void onSurfaceChanged(GL10 gl, int width, int height) {
     * gl.glViewport(0, 0, width, height);
     * // for a fixed camera, set the projection too
     * float ratio = (float) width / height;
     * gl.glMatrixMode(GL10.GL_PROJECTION);
     * gl.glLoadIdentity();
     * gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
     * }
     * </pre>
     *
     * @param gl     the GL interface. Use <code>instanceof</code> to
     *               test if the interface supports GL11 or higher interfaces.
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Util.LogDebug(TAG, "onSurfaceChanged()");
        shader.resize(width, height);
        renderView.onSurfaceChanged(width, height);
    }

    /**
     * Called to draw the current frame.
     * <p/>
     * This method is responsible for drawing the current frame.
     * <p/>
     * The implementation of this method typically looks like this:
     * <pre class="prettyprint">
     * void onDrawFrame(GL10 gl) {
     * gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
     * //... other gl calls to render the scene ...
     * }
     * </pre>
     *
     * @param gl the GL interface. Use <code>instanceof</code> to
     *           test if the interface supports GL11 or higher interfaces.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        //Util.LogDebug(TAG, "onDrawFrame");
        if(isUpdateNeeded.compareAndSet(true, false)) {
            renderView.updateTexture();
            shader.draw(glTexture.getTextureId());
        }
    }

}
