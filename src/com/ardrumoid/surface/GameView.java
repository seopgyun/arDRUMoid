package com.ardrumoid.surface;

import com.ardrumoid.data.PlayMusicData;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String LOG_TAG = "GameView";
	
	private SurfaceHolder mHolder;
	private GameThread mThread;
    private PlayMusicData mData;
	
	public GameView(Context context, PlayMusicData data) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		this.mData = data;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(LOG_TAG, "surfaceCreated");
		mThread = new GameThread(mHolder);
		mThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(LOG_TAG, "surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(LOG_TAG, "surfaceDestroyed");
        
		mThread.setLoop(false);

        try {
        	mThread.join();
        } catch (Exception e) {
            e.getStackTrace();
        }
		
	}

}
