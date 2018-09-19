package src;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_PLUS;

/**
 * Created by Leonardo Lana
 * Github: https://github.com/leonardodlana
 * <p>
 * Copyright 2018 Leonardo Lana
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *  A very simple canvas with a runnable thread to render in real time
 */
public class CanvasPanel extends Canvas implements Runnable {

    private Thread mLiveThread;
    private boolean mIsRunning = false;

    private MainScreen mMainScreen;
    private float mZoom = 1f;
    private int mTranslateX;
    private int mTranslateY;
    private int mLastX;
    private int mLastY;
    private int mKeyPressed;

    public void start() {
        setPreferredSize(getParent().getPreferredSize());

        // Request focus to capture inputs
        requestFocus();

        mMainScreen = new MainScreen(0, 0, getWidth(), getHeight());

        // Create 2 buffers to swap between each other without the user noticing
        // Basically we'll paint the next frame and when it's ready we'll show on the canvas
        // By doing so we create a smooth transition
        // Example:
        // Paint/Render Frame A
        // Show Frame A
        // Paint/Render Frame B
        // Show Frame B
        // Paint/Render Frame A
        // Show Frame A
        // ...
        createBufferStrategy(2);

        // Create a thread that will run until the applet is stopped, this thread will work as a "refresher"
        // and we will be able to "simulate" our panel
        mLiveThread = new Thread(this);
        mLiveThread.start();

        setupInput();
    }

    public void stop() {
        mIsRunning = false;
    }

    private void setupInput() {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mMainScreen.onMouseClick(e.getX(), e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mTranslateX += e.getX() - mLastX;
                mTranslateY += e.getY() - mLastY;
                mLastX = e.getX();
                mLastY = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mLastX = e.getX();
                mLastY = e.getY();
                mMainScreen.onMouseMoved(e.getX(), e.getY());
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                mKeyPressed = e.getKeyCode();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                mKeyPressed = -1;
            }
        });
    }

    private void handleKeyEvent(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1:
                float oldZoom = mZoom;
                mZoom += 0.5f;
                float diffWidth = ((getWidth() * oldZoom) - (getWidth() * mZoom)) * .5f;
                float diffHeight = ((getHeight() * oldZoom) - (getHeight() * mZoom)) * .5f;
                mTranslateX += diffWidth;
                mTranslateY += diffHeight;
                break;
            case KeyEvent.VK_2:
                oldZoom = mZoom;
                mZoom -= 1f;
                if(mZoom < 1)
                    mZoom = 1;

                diffWidth = ((getWidth() * oldZoom) - (getWidth() * mZoom)) * .5f;
                diffHeight = ((getHeight() * oldZoom) - (getHeight() * mZoom)) * .5f;
                mTranslateX += diffWidth;
                mTranslateY += diffHeight;
                break;
        }
    }

    /*
        Runnable
     */

    @Override
    public void run() {
        mIsRunning = true;

        // The buffer strategy handles the swap between buffers
        BufferStrategy bufferStrategy = getBufferStrategy();
        Graphics2D graphics2D = (Graphics2D) bufferStrategy.getDrawGraphics();

        long difftime = 0;
        long oldStepTime = System.currentTimeMillis();

        while (mIsRunning) {
            handleKeyEvent(mKeyPressed);
            mMainScreen.update(difftime, difftime / 1000.f);
            graphics2D.setColor(Color.white);
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
            mMainScreen.draw(graphics2D, mZoom, mTranslateX, mTranslateY);
            bufferStrategy.show();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            difftime = System.currentTimeMillis() - oldStepTime;
            oldStepTime = System.currentTimeMillis();
        }
    }
}
