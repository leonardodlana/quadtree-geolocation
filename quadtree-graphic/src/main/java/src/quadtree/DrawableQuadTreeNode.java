package src.quadtree;

import src.Drawable;
import src.quadtree.core.Neighbour;
import src.quadtree.core.QuadTreeNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;

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
public class DrawableQuadTreeNode extends QuadTreeNode implements Drawable {

    private static final Color BLACK = new Color(0, 0, 0, 50);
    private float mDrawScaleX;
    private float mDrawScaleY;
    private Rectangle2D mDrawBounds;

    /**
     * Creates a new node
     *
     * @param latitude       node's Y start point
     * @param longitude      node's X start point
     * @param latitudeRange  node's height
     * @param longitudeRange node's width
     * @param drawScaleX
     * @param drawScaleY
     */
    public DrawableQuadTreeNode(double latitude, double longitude, double latitudeRange, double longitudeRange,
                                float drawScaleX, float drawScaleY, int drawWidth, int drawHeight) {
        super(latitude, longitude, latitudeRange, longitudeRange);
        mDrawScaleX = drawScaleX;
        mDrawScaleY = drawScaleY;
        mDrawBounds = new Rectangle2D.Double();
        mDrawBounds.setRect(0, 0, drawWidth, drawHeight);
    }

    @Override
    public void draw(Graphics2D graphics2D, float zoom, int translateX, int translateY) {
        graphics2D.setColor(BLACK);
        graphics2D.drawRect(translateX + (int) (getLongitude() * mDrawScaleX * zoom), translateY + (int) (getLatitude() * mDrawScaleY * zoom),
                (int) (getWidth() * mDrawScaleX * zoom), (int) (getHeight() * mDrawScaleY * zoom));

        if (mTopLeftNode != null)
            ((DrawableQuadTreeNode) mTopLeftNode).draw(graphics2D, zoom, translateX, translateY);

        if (mTopRightNode != null)
            ((DrawableQuadTreeNode) mTopRightNode).draw(graphics2D, zoom, translateX, translateY);

        if (mBottomLeftNode != null)
            ((DrawableQuadTreeNode) mBottomLeftNode).draw(graphics2D, zoom, translateX, translateY);

        if (mBottomRightNode != null)
            ((DrawableQuadTreeNode) mBottomRightNode).draw(graphics2D, zoom, translateX, translateY);

        graphics2D.setColor(Color.black);
        Neighbour neighbour;
        if(zoom > 13) {
            final int size = mNeighbours.size();
            for (int i = 0; i < size; i++) {
                neighbour = mNeighbours.get(i);
                if (!mDrawBounds.contains(translateX + (int) (neighbour.getLongitude() * mDrawScaleX * zoom), translateY + (int) (neighbour.getLatitude() * mDrawScaleY * zoom)))
                    continue;
                graphics2D.fillRect(translateX + (int) (neighbour.getLongitude() * mDrawScaleX * zoom), translateY + (int) (neighbour.getLatitude() * mDrawScaleY * zoom),
                        (int) (0.025 * mDrawScaleX * zoom), (int) (0.025 * mDrawScaleY * zoom));
            }
        }
    }

    @Override
    public void update(long difftime, float difftimeInSeconds) {

    }

    @Override
    protected QuadTreeNode locateAndCreateNodeForPoint(double latitude, double longitude) {
        double halfWidth = mBounds.width * .5f;
        double halfHeight = mBounds.height * .5f;

        if (longitude < mBounds.x + halfWidth) {
            if (latitude < mBounds.y + halfHeight)
                return mTopLeftNode != null ? mTopLeftNode : (mTopLeftNode = new DrawableQuadTreeNode(mBounds.y, mBounds.x, halfHeight, halfWidth, mDrawScaleX, mDrawScaleY, (int) mDrawBounds.getWidth(), (int) mDrawBounds.getHeight()));

            return mBottomLeftNode != null ? mBottomLeftNode : (mBottomLeftNode = new DrawableQuadTreeNode(mBounds.y + halfHeight, mBounds.x, halfHeight, halfWidth, mDrawScaleX, mDrawScaleY, (int) mDrawBounds.getWidth(), (int) mDrawBounds.getHeight()));

        }

        if (latitude < mBounds.y + halfHeight)
            return mTopRightNode != null ? mTopRightNode : (mTopRightNode = new DrawableQuadTreeNode(mBounds.y, mBounds.x + halfWidth, halfHeight, halfWidth, mDrawScaleX, mDrawScaleY, (int) mDrawBounds.getWidth(), (int) mDrawBounds.getHeight()));


        return mBottomRightNode != null ? mBottomRightNode : (mBottomRightNode = new DrawableQuadTreeNode(mBounds.y + halfHeight, mBounds.x + halfWidth, halfHeight, halfWidth, mDrawScaleX, mDrawScaleY, (int) mDrawBounds.getWidth(), (int) mDrawBounds.getHeight()));

    }
}
