package src.quadtree.core;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
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
 *
 * ============================================================================
 *
 * This class is the wrapper between the "view" and the quadtree data structure.
 *
 * Few notes/explanations:
 *
 * The {@link Rectangle2D.Double} does not support negative bounds. Having this limitation
 * in mind we need to normalize the latitude and longitude.
 *
 * But why TOTAL_X_DEGREES is 360 and TOTAL_Y_DEGREES is 180?
 *
 * That's the 2d representation of the world in latitude/longitude degrees.
 * @see <a href="http://fortunedream.info/wp-content/uploads/2018/01/latitude-map-of-the-world-maps-lines-longitude-and-interactive-locator.jpg">Globe 2D Projection</a>
 *
 */

public class QuadTree {

    public static final int TOTAL_X_DEGREES = 360; // -180 to 180 - longitude
    public static final int TOTAL_Y_DEGREES = 180; // -90 to 90   - latitude
    private static final int NORMALIZE_X = 180;
    private static final int NORMALIZE_Y = 90;

    private QuadTreeNode mRootNode;

    public QuadTree() {
        mRootNode = new QuadTreeNode(0, 0, TOTAL_Y_DEGREES, TOTAL_X_DEGREES);
    }

    public QuadTree(QuadTreeNode rootNode) {
        mRootNode = rootNode;
    }

    public synchronized void addNeighbour(long id, double latitude, double longitude) {
        Neighbour neighbour = new NeighbourImpl(id, normalizeLatitude(latitude),
                normalizeLongitude(longitude));
        mRootNode.addNeighbour(neighbour, QuadTreeConstants.QUADTREE_LAST_NODE_SIZE_IN_DEGREE);
    }

    public void removeNeighbour(long id) {
        mRootNode.removeNeighbour(id);
    }

    public Set<Neighbour> findNeighbours(double latitude, double longitude, double rangeInKm) {
        Set<Neighbour> neighbourSet = new HashSet<>();
        double rangeInDegrees = QuadTreeConstants.kmToDegree(rangeInKm);
        Rectangle2D.Double areaOfInterest = getRangeAsRectangle(normalizeLatitude(latitude), normalizeLongitude(longitude), rangeInDegrees);
        mRootNode.findNeighboursWithinRectangle(neighbourSet, areaOfInterest);
        return neighbourSet;
    }

    public Set<Long> findNeighboursIds(double latitude, double longitude, double rangeInKm) {
        Set<Neighbour> neighbourSet = findNeighbours(latitude, longitude, rangeInKm);
        Set<Long> neighboursIds = new HashSet<>();

        for(Neighbour neighbour : neighbourSet)
            neighboursIds.add(neighbour.getId());

        return neighboursIds;
    }

    protected QuadTreeNode getRootNode() {
        return mRootNode;
    }

    private double normalizeLatitude(double latitude) {
        return latitude + NORMALIZE_Y;
    }

    private double normalizeLongitude(double longitude) {
        return longitude + NORMALIZE_X;
    }

    private Rectangle2D.Double getRangeAsRectangle(double latitude, double longitude, double range) {
        /*
           We need to centralize the point and have the range on every direction
         */
        return new Rectangle2D.Double(Math.max(longitude - range, 0),
                Math.max(latitude - range, 0),
                Math.min(range * 2, TOTAL_X_DEGREES),
                Math.min(range * 2, TOTAL_Y_DEGREES));
    }

}
