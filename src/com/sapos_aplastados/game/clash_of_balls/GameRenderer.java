/*
 * Copyright (C) 2012-2013 Hans Hardmeier <hanshardmeier@gmail.com>
 * Copyright (C) 2012-2013 Andrin Jenal
 * Copyright (C) 2012-2013 Beat Küng <beat-kueng@gmx.net>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */
package com.sapos_aplastados.game.clash_of_balls;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.sapos_aplastados.game.clash_of_balls.MainActivity.LoadViewTask;
import com.sapos_aplastados.game.clash_of_balls.ShaderManager.ShaderType;
import com.sapos_aplastados.game.clash_of_balls.game.RenderHelper;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;


public class GameRenderer implements GLSurfaceView.Renderer {

    private static final String LOG_TAG = "GameRenderer";
    
    private Context m_activity_context;
    
    private int m_width; //screen output dimension
    private int m_height;
    
    private UIHandler m_ui_handler;
    private ShaderManager m_shader_manager;
    private RenderHelper m_renderer;
    
    private long m_last_time=0;
    
    private LoadViewTask m_progress_view;
    
    public GameRenderer(Context activity_context, LoadViewTask progress_view) {
    	m_activity_context = activity_context;
    	m_progress_view = progress_view;
    }
    
    public void onDestroy() {
    	if(m_ui_handler!=null) m_ui_handler.onDestroy();
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        //init GL
        
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        
        // culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        
        //we want clockwise triangles
        GLES20.glFrontFace(GLES20.GL_CW);

        // No depth testing
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }
    
    private void init() throws Exception {
    	if(m_width == 0 || m_height == 0) 
    		throw new Exception("width or height is 0");
    	
    	m_last_time = SystemClock.elapsedRealtime(); //or: nanoTime()
    	
    	m_ui_handler = new UIHandler(m_width, m_height
    			, m_activity_context, m_progress_view);
    	m_shader_manager = new ShaderManager(m_activity_context);
    	m_renderer = new RenderHelper(m_shader_manager, (float)m_width
    			, (float)m_height);
    	m_renderer.useOrthoProjection();
    	
    	
    	//make sure a shader is loaded: use default shader
    	m_shader_manager.useShader(ShaderType.TypeDefault);
    }
    
    //handle back button (called from another thread!)
    public boolean onBackPressed() {
    	return m_ui_handler.onBackPressed();
    }
    

    public void onDrawFrame(GL10 unused) {

    	try {
    		if(m_ui_handler==null) init();

    		/* Move the game or menu */
    		long time = SystemClock.elapsedRealtime(); //or: nanoTime()
    		float elapsed_time = (float)(time - m_last_time) / 1000.f; 
    		m_ui_handler.move(elapsed_time);
    		m_renderer.move(elapsed_time);
    		m_last_time = time;


    		/* Render the scene */

    		// Draw background color
    		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    		m_renderer.modelMatSetIdentity();
    		m_ui_handler.draw(m_renderer);

    	} catch(Exception e) {
    		Log.e(LOG_TAG, "Exception in renderer");
    		e.printStackTrace();
    		System.exit(-1);
    	}
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.d(LOG_TAG, "onSurfaceChanged: w="+width+", h="+height);
        
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
    	// but we disable screen rotation thus it happens only on startup
        GLES20.glViewport(0, 0, width, height);
        m_width=width;
        m_height=height;
        
        if(m_ui_handler != null) {
	        m_ui_handler.onSurfaceChanged(width, height);
	        m_renderer.onSurfaceChanged(width, height);
	        m_shader_manager.onSurfaceChanged(width, height);
	        m_shader_manager.useShader(ShaderType.TypeDefault);
        }

    }
    
    public void handleTouchInput(float x, float y, int event) {
    	//Note: y axis is mirrored
    	m_ui_handler.onTouchEvent(x, m_height-y, event);
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(LOG_TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}

