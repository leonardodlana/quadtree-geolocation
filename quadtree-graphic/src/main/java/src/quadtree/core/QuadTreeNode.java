package src.quadtree.core;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

public class QuadTreeNode {

    /**
     * Represents the whole rectangle of this node
     * ---------
     * |       |
     * |       |
     * |       |
     * ---------
     */
    protected Rectangle2D.Double mBounds;

    /**
     * Represents the top left node of this node
     * ---------
     * | x |   |
     * |---|---|
     * |   |   |
     * ---------
     */
    protected QuadTreeNode mTopLeftNode;

    /**
     * Represents the top right node of this node
     * ---------
     * |   | x |
     * |---|---|
     * |   |   |
     * ---------
     */
    protected QuadTreeNode mTopRightNode;

    /**
     * Represents the bottom left node of this node
     * ---------
     * |   |   |
     * |---|---|
     * | x |   |
     * ---------
     */
    protected QuadTreeNode mBottomLeftNode;

    /**
     * Represents the bottom right node of this node
     * ---------
     * |   |   |
     * |---|---|
     * |   | x |
     * ---------
     */
    protected QuadTreeNode mBottomRightNode;

    /**
     *  List of points of interest A.K.A neighbours inside this node
     *  this list is only filled in the deepest nodes
     */
    protected List<Neighbour> mNeighbours = new ArrayList<>();

    /**
     * Creates a new node
     * @param latitude node's Y start point
     * @param longitude node's X start point
     * @param latitudeRange node's height
     * @param longitudeRange node's width
     */
    public QuadTreeNode(double latitude, double longitude, double latitudeRange, double longitudeRange) {
        mBounds = new Rectangle2D.Double(longitude, latitude, longitudeRange, latitudeRange);
    }

    /**
     * Adds a neighbour in the quadtree.
     * This method will navigate and create nodes if necessary, until the smallest (deepest) node is reached
     * @param neighbour
     */
    public void addNeighbour(Neighbour neighbour, double deepestNodeSize) {
        double halfSize = mBounds.width * .5f;
        if (halfSize < deepestNodeSize) {
            mNeighbours.add(neighbour);
            return;
        }

        QuadTreeNode node = locateAndCreateNodeForPoint(neighbour.getLatitude(), neighbour.getLongitude());
        node.addNeighbour(neighbour, deepestNodeSize);
    }

    /**
     * Removes a neighbour from the quadtree
     * @param id the neighbour's id
     * @return if the neighbour existed and was removed
     */
    public boolean removeNeighbour(long id) {
        for (Neighbour neighbor : mNeighbours) {
            if (id == neighbor.getId()) {
                mNeighbours.remove(neighbor);
                return true;
            }
        }

        if (mTopLeftNode != null) {
            if (mTopLeftNode.removeNeighbour(id))
                return true;
        }

        if (mBottomLeftNode != null) {
            if (mBottomLeftNode.removeNeighbour(id))
                return true;
        }

        if (mTopRightNode != null) {
            if (mTopRightNode.removeNeighbour(id))
                return true;
        }

        if (mBottomRightNode != null) {
            if (mBottomRightNode.removeNeighbour(id))
                return true;
        }

        return false;
    }

    /**
     * Recursively search for neighbours inside the given rectangle
     * @param neighbourSet a set to be filled by this method
     * @param rangeAsRectangle the area of interest
     */
    public void findNeighboursWithinRectangle(Set<Neighbour> neighbourSet, Rectangle2D.Double rangeAsRectangle) {
        boolean end;

        // In case of containing the whole area of interest
        if (mBounds.contains(rangeAsRectangle)) {
            end = true;

            // If end is true, it means that we are on the deepest node
            // otherwise we should keep going deeper

            if (mTopLeftNode != null) {
                mTopLeftNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }

            if (mBottomLeftNode != null) {
                mBottomLeftNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }

            if (mTopRightNode != null) {
                mTopRightNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }

            if (mBottomRightNode != null) {
                mBottomRightNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }


            if (end)
                addNeighbors(true, neighbourSet, rangeAsRectangle);

            return;
        }

        // In case of intersection with the area of interest
        if (mBounds.intersects(rangeAsRectangle)) {
            end = true;

            // If end is true, it means that we are on the deepest node
            // otherwise we should keep going deeper

            if (mTopLeftNode != null) {
                mTopLeftNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }

            if (mBottomLeftNode != null) {
                mBottomLeftNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }

            if (mTopRightNode != null) {
                mTopRightNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }

            if (mBottomRightNode != null) {
                mBottomRightNode.findNeighboursWithinRectangle(neighbourSet, rangeAsRectangle);
                end = false;
            }

            if (end)
                addNeighbors(false, neighbourSet, rangeAsRectangle);
        }
    }

    /**
     * Adds neighbours to the found set
     * @param contains if the rangeAsRectangle is contained inside the node
     * @param neighborSet a set to be filled by this method
     * @param rangeAsRectangle the area of interest
     */
    private void addNeighbors(boolean contains, Set<Neighbour> neighborSet, Rectangle2D.Double rangeAsRectangle) {
        if (contains) {
            neighborSet.addAll(mNeighbours);
            return;
        }

        findAll(neighborSet, rangeAsRectangle);
    }

    /**
     * If the rangeAsRectangle is not contained inside this node we must
     * search for neighbours that are contained inside the rangeAsRectangle
     * @param neighborSet a set to be filled by this method
     * @param rangeAsRectangle the area of interest
     */
    private void findAll(Set<Neighbour> neighborSet, Rectangle2D.Double rangeAsRectangle) {
        for (Neighbour neighbor : mNeighbours) {
            if (rangeAsRectangle.contains(neighbor.getLongitude(), neighbor.getLatitude()))
                neighborSet.add(neighbor);
        }
    }

    /**
     * This methods finds and returns in which of the 4 child nodes the latitude and longitude is located.
     * If the node does not exist, it is created.
     *
     * @param latitude
     * @param longitude
     * @return the node that contains the desired latitude and longitude
     */
    protected QuadTreeNode locateAndCreateNodeForPoint(double latitude, double longitude) {
        double halfWidth = mBounds.width * .5f;
        double halfHeight = mBounds.height * .5f;

        if (longitude < mBounds.x + halfWidth) {
            if (latitude < mBounds.y + halfHeight)
                return mTopLeftNode != null ? mTopLeftNode : (mTopLeftNode = new QuadTreeNode(mBounds.y, mBounds.x, halfHeight, halfWidth));

            return mBottomLeftNode != null ? mBottomLeftNode : (mBottomLeftNode = new QuadTreeNode(mBounds.y + halfHeight, mBounds.x, halfHeight, halfWidth));
        }

        if (latitude < mBounds.y + halfHeight)
            return mTopRightNode != null ? mTopRightNode : (mTopRightNode = new QuadTreeNode(mBounds.y, mBounds.x + halfWidth, halfHeight, halfWidth));

        return mBottomRightNode != null ? mBottomRightNode : (mBottomRightNode = new QuadTreeNode(mBounds.y + halfHeight, mBounds.x + halfWidth, halfHeight, halfWidth));
    }

    protected double getLongitude() {
        return mBounds.x;
    }

    protected double getLatitude() {
        return mBounds.y;
    }

    protected double getWidth() {
        return mBounds.width;
    }

    protected double getHeight() {
        return mBounds.height;
    }


}
