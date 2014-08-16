/*
 * Copyright (C) 2014 4jiokiSoft
 *
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.yojiokisoft.whacmole.app;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    private Button[][] mButton = new Button[5][4]; // [行][列]
    private Button mMoleButton;
    private TextView mScoreText;
    private int mScore;
    private TextView mTimeText;
    private int mGameTime = 60;
    private Timer mTimer = null;
    private Handler mHandler = new Handler();
    private RelativeLayout mGameOver;
    private SoundPool mSound;
    private int[] mSoundId;
    private Timer mMoleTimer = null;
    private TextView mReplayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSoundId = new int[2];
        mSound = new SoundPool(mSoundId.length, AudioManager.STREAM_MUSIC, 0);
        mSoundId[0] = mSound.load(getApplicationContext(), R.raw.middle_punch1, 0);
        mSoundId[1] = mSound.load(getApplicationContext(), R.raw.swing1, 0);

        mMoleButton = null;
        mScore = 0;
        mScoreText = (TextView) findViewById(R.id.scoreText);
        mTimeText = (TextView) findViewById(R.id.timeText);
        mGameOver = (RelativeLayout) findViewById(R.id.game_over_container);
        mGameOver.setOnClickListener(mGameOverClicked);
        mReplayText = (TextView) findViewById(R.id.replay_text);

        mButton[0][0] = (Button) findViewById(R.id.Button1A);
        mButton[0][1] = (Button) findViewById(R.id.Button1B);
        mButton[0][2] = (Button) findViewById(R.id.Button1C);
        mButton[0][3] = (Button) findViewById(R.id.Button1D);

        mButton[1][0] = (Button) findViewById(R.id.Button2A);
        mButton[1][1] = (Button) findViewById(R.id.Button2B);
        mButton[1][2] = (Button) findViewById(R.id.Button2C);
        mButton[1][3] = (Button) findViewById(R.id.Button2D);

        mButton[2][0] = (Button) findViewById(R.id.Button3A);
        mButton[2][1] = (Button) findViewById(R.id.Button3B);
        mButton[2][2] = (Button) findViewById(R.id.Button3C);
        mButton[2][3] = (Button) findViewById(R.id.Button3D);

        mButton[3][0] = (Button) findViewById(R.id.Button4A);
        mButton[3][1] = (Button) findViewById(R.id.Button4B);
        mButton[3][2] = (Button) findViewById(R.id.Button4C);
        mButton[3][3] = (Button) findViewById(R.id.Button4D);

        mButton[4][0] = (Button) findViewById(R.id.Button5A);
        mButton[4][1] = (Button) findViewById(R.id.Button5B);
        mButton[4][2] = (Button) findViewById(R.id.Button5C);
        mButton[4][3] = (Button) findViewById(R.id.Button5D);

        for (int row = 0; row < mButton.length; row++) {
            for (int col = 0; col < mButton[1].length; col++) {
                mButton[row][col].setOnClickListener(mButtonClicked);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    private void gameStart() {
        mReplayText.setVisibility(View.INVISIBLE);
        printMole();

        if (mTimer != null) {
            return;
        }
        mTimer = new Timer(true);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mGameTime--;
                        if (mGameTime <= 0) {
                            gameOver();
                        }
                        mTimeText.setText(Integer.toString(mGameTime));
                    }
                });
            }
        }, 1000, 1000);
    }

    private void gameOver() {
        mTimer.cancel();
        mTimer = null;
        mMoleTimer.cancel();
        mMoleTimer = null;
        mGameOver.setVisibility(View.VISIBLE);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mReplayText.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSound.release();
    }

    private void printMole() {
        if (mMoleButton != null) {
            mMoleButton.setBackgroundResource(R.drawable.bg_button);
        }
        int row = (int) (Math.random() * 5);
        int col = (int) (Math.random() * 4);
        mButton[row][col].setBackgroundResource(R.drawable.mole);
        mMoleButton = mButton[row][col];

        if (mMoleTimer != null) {
            mMoleTimer.cancel();
        }
        long delay = (long) (Math.random() * (1000 - 100) + 100);
        mMoleTimer = new Timer(true);
        mMoleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        printMole();
                    }
                });
            }
        }, delay);
    }

    private View.OnClickListener mButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view != mMoleButton) {
                mSound.play(mSoundId[1], 1.0F, 1.0F, 0, 0, 1.0F);
                return;
            }
            mSound.play(mSoundId[0], 1.0F, 1.0F, 0, 0, 1.0F);
            mScore++;
            String strScore = String.valueOf(mScore);
            mScoreText.setText(strScore);
            printMole();
        }
    };

    private View.OnClickListener mGameOverClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mReplayText.getVisibility() != View.VISIBLE) {
                return;
            }
            mGameOver.setVisibility(View.INVISIBLE);
            mScore = 0;
            String strScore = String.valueOf(mScore);
            mScoreText.setText(strScore);
            mGameTime = 60;
            gameStart();
        }
    };
}
