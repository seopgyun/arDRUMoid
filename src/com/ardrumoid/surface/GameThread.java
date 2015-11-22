package com.ardrumoid.surface;

import java.util.ArrayList;
import java.util.Scanner;

import com.ardrumoid.MainApp;
import com.ardrumoid.MusicPlayActivity;
import com.ardrumoid.R;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	private static final String LOG_TAG = "GameThread";
	final int noteWidth = (int) ((MusicPlayActivity.deviceWidth * (1.0 / 7.0)));
	final int Snare_dX = (int) (MusicPlayActivity.deviceWidth * (9.0 / 21.0));
	final int Symbal_dX = (int) (MusicPlayActivity.deviceWidth * (1.0 / 21.0));
	final int Hihat_dX = (int) (MusicPlayActivity.deviceWidth * (5.0 / 21.0));
	final int Tom_dX = (int) (MusicPlayActivity.deviceWidth * (13.0 / 21.0));
	final int Kick_dX = (int) (MusicPlayActivity.deviceWidth * (17.0 / 21.0));
	public int Instrument = 0;
	public int FirstDY;
	public static Paint mPaint;
	public static Canvas mCanvas;
	public static ArrayList<SnareNote>[] note = new ArrayList[5];

	private SurfaceHolder holder;
	private boolean isLoop;
	private double spd = 10;

	public GameThread(SurfaceHolder _holder) {
		this.holder = _holder;
		this.isLoop = true;

		Log.d(LOG_TAG, "width " + MusicPlayActivity.deviceWidth + " // height "
				+ MusicPlayActivity.deviceHeight);
	}

	public void setLoop(boolean _isLoop) {
		this.isLoop = _isLoop;
	}

	@Override
	public void run() {
		super.run();
		int deviceWidth = MusicPlayActivity.deviceWidth;
		int deviceHeight = MusicPlayActivity.deviceHeight;

		mCanvas = null;
		mPaint = new Paint();

		mCanvas = holder.lockCanvas(null);
		mPaint.setColor(Color.WHITE);
		mCanvas.drawRect(0, 0, deviceWidth, deviceHeight, mPaint);
		createJudge(deviceWidth, deviceHeight);
		holder.unlockCanvasAndPost(mCanvas);

		SnareNote testNote = new SnareNote();

		long oldTime = System.currentTimeMillis();
		while (testNote.dY < (deviceHeight * 0.8) - 25) {
			try {
				mCanvas = holder.lockCanvas(null);
				synchronized (holder) {
					mPaint.setColor(Color.TRANSPARENT);
					testNote.moveNote();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} finally {
				if (mCanvas != null) {
					holder.unlockCanvasAndPost(mCanvas);
				}
			}
		}
		long curTime = System.currentTimeMillis();

		Scanner sc = new Scanner(MainApp.getContext().getResources()
				.openRawResource(R.raw.up_above_note_hard_hihat));
		// Toast.makeText(MainApplication.getContext(), sc.next().toString(),
		// Toast.LENGTH_SHORT).show();

		note[0] = new ArrayList<SnareNote>(); // Hi-Hat
		note[1] = new ArrayList<SnareNote>(); // Snare
		note[2] = new ArrayList<SnareNote>(); // Symbals
		note[3] = new ArrayList<SnareNote>(); // Tom
		note[4] = new ArrayList<SnareNote>(); // Kick

		/* spd px씩 초기노트가 판정노트까지 이동한 시간. */
		long gapTime = curTime - oldTime;
		/* pxTime : 1px당 이동하는 속력 (spd px) */
		double pxTime = ((deviceHeight * 0.8 - 25) / gapTime) + MainApp.syncTime;

		int firstNoteTime = sc.nextInt(); // 첫 번쨰 노트의 시작 ms.
		int secondNoteTime = sc.nextInt(); // 두 번쨰 노트의 시작 ms.

		int interval, preInterval;
		double base = (secondNoteTime - firstNoteTime) * pxTime;
		double noteType;

		/* 첫 번째 노트 */
		SnareNote n = new SnareNote();
		interval = (int) (firstNoteTime * pxTime);
		FirstDY = (int) (deviceHeight * 0.8) - interval;
		n.dY = (int) (deviceHeight * 0.8) - interval;
		preInterval = n.dY;

		note[0].add(n);

		/* 두 번째 노트 */
		n = new SnareNote();
		interval = (int) (secondNoteTime * pxTime);
		n.dY = (int) (deviceHeight * 0.8) - interval;
		preInterval = n.dY;
		note[0].add(n);

		noteType = sc.nextDouble(); // 첫 번째 노트
		int ins = 0;
		int baseInterval = (int) (base * (noteType / 4));
		Instrument = Hihat_dX;
		while (sc.hasNext()) {
			noteType = sc.nextDouble();
			if (noteType > 0) {

				n = new SnareNote();

				interval = (int) (baseInterval * (4.0 / (int) noteType));
				if (noteType % 1 == 0.5) {
					interval = (int) (interval * 1.5);
				}

				n.dY = preInterval - interval;
				preInterval = n.dY;

				note[ins].add(n);
			} else if (noteType < 0) {
				interval = (int) (baseInterval * (4.0 / (int) Math.abs(noteType)));
				if (Math.abs(noteType) % 1 == 0.5) {
					interval = (int) (interval * 1.5);
				}
				preInterval -= interval;
			} else {
				ins++;
				preInterval = FirstDY;
				noteType = sc.nextDouble(); // 처음으로 나올 노트 음표

				preInterval += (int) (baseInterval * (4.0 / (int) Math.abs(noteType)));

			}
		}

		sc.close();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			MusicPlayActivity.mp.start();
		}

		while (isLoop) {
			mCanvas = holder.lockCanvas(null);

			try {
				synchronized (holder) {
					mPaint.setColor(0xff66ccff);
					mCanvas.drawRect(0, 0, deviceWidth, deviceHeight, mPaint);
					createJudge(deviceWidth, deviceHeight);
					mPaint.setColor(Color.RED);

					/*
					 * 1 : 완쪽 위치 2 : 윗변 위치 3 : 오른쪽 위치 4 : 밑변 위치 ( Top가 0일때를 기준으로
					 * 길이 지정. ex ) top == 100이라면 200을 해줘야 100 크기의 이미지가 보임.
					 */
					for (int instru = 0; instru < 5; instru++) {
						if (instru == 0) {
							Instrument = Hihat_dX;
						} else if (instru == 1) {
							Instrument = Snare_dX;

						} else if (instru == 2) {
							Instrument = Symbal_dX;

						} else if (instru == 3) {
							Instrument = Tom_dX;

						} else if (instru == 4) {
							Instrument = Kick_dX;

						}
						for (int i = 0; i < note[instru].size(); i++) {
							if (note[instru].get(i).dY <= deviceHeight) {
								note[instru].get(i).moveNote();
							}
						}
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} finally {
				if (mCanvas != null) {
					holder.unlockCanvasAndPost(mCanvas);
				}
			}

		}

	}

	private void createJudge(int width, int height) {
		mPaint.setColor(0xff90d11f);
		mCanvas.drawRect(0, (int) (height * 0.7), width, (int) (height * 0.9), mPaint);

		mPaint.setColor(0xff666666);
		mCanvas.drawRect(Snare_dX, (int) (height * 0.8) - 25, Snare_dX + noteWidth,
				(int) (height * 0.8) + 25, GameThread.mPaint);
		mCanvas.drawRect(Symbal_dX, (int) (height * 0.8) - 25, Symbal_dX + noteWidth,
				(int) (height * 0.8) + 25, GameThread.mPaint);
		mCanvas.drawRect(Hihat_dX, (int) (height * 0.8) - 25, Hihat_dX + noteWidth,
				(int) (height * 0.8) + 25, GameThread.mPaint);
		mCanvas.drawRect(Tom_dX, (int) (height * 0.8) - 25, Tom_dX + noteWidth,
				(int) (height * 0.8) + 25, GameThread.mPaint);
		mCanvas.drawRect(Kick_dX, (int) (height * 0.8) - 25, Kick_dX + noteWidth,
				(int) (height * 0.8) + 25, GameThread.mPaint);

	}

	public class SnareNote {
		public int dY = -50;

		public void moveNote() {
			if (dY < MusicPlayActivity.deviceHeight) {
				dY += spd;
				GameThread.mCanvas.drawRect(Instrument, dY, Instrument + noteWidth,
						dY + 50, GameThread.mPaint);
				// Log.d(LOG_TAG, "TT " + dY);
			}
		}

	}
}
