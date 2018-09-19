package src;

import src.quadtree.DrawableQuadTree;
import src.quadtree.core.Neighbour;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static src.quadtree.core.QuadTree.TOTAL_X_DEGREES;
import static src.quadtree.core.QuadTree.TOTAL_Y_DEGREES;

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
 */
public class MainScreen extends Screen {

    private final Color COLOR_MENU = new Color(0,0,0,200);
    private final float mScaleDrawToLongitude;
    private final float mScaleDrawToLatitude;
    private final float mDrawScaleX;
    private final float mDrawScaleY;
    private int mTranslationX;
    private int mTranslationY;

    private BufferedImage mImageWorldMap;
    private DrawableQuadTree mQuadTree;
    private int mNeighboursCount = 0;
    private Set<Neighbour> mSelectedNeighbours = new HashSet<>();
    private float mZoom;
    private int mMouseX;
    private int mMouseY;
    private int mSearchCount = 1;
    private long mSearchTimeTotal = 0;

    public MainScreen(int x, int y, int width, int height) {
        super(x, y, width, height);
        mScaleDrawToLongitude = (float) width / (float) TOTAL_X_DEGREES;
        mScaleDrawToLatitude = (float) height / (float) TOTAL_Y_DEGREES;
        mDrawScaleX = (float) width / (float) TOTAL_X_DEGREES;
        mDrawScaleY = (float) height / (float) TOTAL_Y_DEGREES;
        mQuadTree = new DrawableQuadTree(width, height);

        Random random = new Random();

        new Thread(() -> {
            int mDelayCount = 1;
            for (int i = 0; i < 10000000; i++) {
                mNeighboursCount++;
                mQuadTree.addNeighbour(i, random.nextDouble() * 180 - 90, random.nextDouble() * 360 - 180);
                if (i % mDelayCount == 0) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (i % 10000 == 0) {
                    mDelayCount += 50;
                }
            }
        }).start();

        try {
            // this is a poor loading, we should load in background
            mImageWorldMap = ImageIO.read(getClass().getResource("/worldmap.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D graphics2D, float zoom, int translateX, int translateY) {
        super.draw(graphics2D, zoom, translateX, translateY);

        mZoom = zoom;
        mTranslationX = translateX;
        mTranslationY = translateY;

        Composite composite = graphics2D.getComposite();
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));
        graphics2D.drawImage(mImageWorldMap, translateX, translateY, (int) (mImageWorldMap.getWidth() * zoom), (int) (mImageWorldMap.getHeight() * zoom), null);
        graphics2D.setComposite(composite);
        mQuadTree.draw(graphics2D, zoom, translateX, translateY);

        graphics2D.setColor(Color.RED);
        for (Neighbour neighbour : mSelectedNeighbours) {
            graphics2D.fillRect(translateX + (int) (neighbour.getLongitude() * mDrawScaleX * zoom), translateY + (int) (neighbour.getLatitude() * mDrawScaleY * zoom),
                    (int) (0.025 * mDrawScaleX * zoom), (int) (0.025 * mDrawScaleY * zoom));
        }

        graphics2D.setColor(COLOR_MENU);
        graphics2D.fillRect(0, 0, 235, 85);
        graphics2D.setColor(Color.magenta);
        graphics2D.drawString("Press: 1 for zoom in | 2 for zoom out", 10, 15);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString("Points of interest: " + mNeighboursCount, 10, 30);
        graphics2D.setColor(Color.RED);
        graphics2D.drawString("Points in area: " + mSelectedNeighbours.size(), 10, 45);
        graphics2D.drawString("Search area in KM: " + 100, 10, 60);
        graphics2D.setColor(Color.ORANGE);
        graphics2D.drawString("Searchs: " + mSearchCount + " AVG time(ms): " + (mSearchTimeTotal / mSearchCount), 10, 75);

    }

    @Override
    public void update(long difftime, float difftimeInSeconds) {
        super.update(difftime, difftimeInSeconds);
        mQuadTree.update(difftime, difftimeInSeconds);
    }

    @Override
    public void onMouseMoved(int x, int y) {
        super.onMouseMoved(x, y);

        mMouseX = x;
        mMouseY = y;

        float xz = (x - mTranslationX) / mZoom;
        float yz = (y - mTranslationY) / mZoom;

        float latitude = (yz / mScaleDrawToLatitude) - 90;
        float longitude = (xz / mScaleDrawToLongitude) - 180;
        try {
            long time = System.currentTimeMillis();
            mSelectedNeighbours = mQuadTree.findNeighbours(latitude, longitude, 100);
            mSearchCount++;
            mSearchTimeTotal = (System.currentTimeMillis() - time);
        } catch (Exception e) {

        }
    }

    @Override
    public void onMouseClick(int x, int y) {
        super.onMouseClick(x, y);

    }
}
