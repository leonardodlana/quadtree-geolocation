# Quadtree for geolocation

Quadtree is a tree data structure that has 4 "children", often called nodes, each of these nodes have 4 more nodes within and so on, until the specified granularity is achieved.



For optimization purpose, the children are only created when necessary, for example, in the image below we can see the representation of the quadtree.

![Quadtree depth](https://poppicture-57876.firebaseapp.com/quadtree/quadtree-depth.png)

![Quadtree gif](https://poppicture-57876.firebaseapp.com/quadtree/quadtree-mouse.gif)

This structure is beautiful in so many ways, there's a lot of usages that can really improve applications. The most common usages are:

* Optimization of rendering on games 
* Dynamic lighting effect on games
* Geolocation
* Image compression
* A.I. Path finding

For this example, we'll use the quadtree for optimizing Geolocation. Imagine that we have an app that shows the user other users around them, or pictures around them.

The traditional way would be comparing the distance between User A and B. Using latitude and longitude of both points, we could calculate the distance in degrees and then convert it to KMs or Miles. So far so good.

![Distance between User A and B](https://poppicture-57876.firebaseapp.com/quadtree/step1.png)

As we know, the formula to calculate the distance between two points is:

![Distance between points formula](https://poppicture-57876.firebaseapp.com/quadtree/dbtpformula.png)

The problem starts here, SQRT functions are CPU expensive, imagine that our backend would need to do this for each request against all users on the database. 


![Distance between User A and others](https://poppicture-57876.firebaseapp.com/quadtree/step2.png)


We could even add some filters to help, maybe compare only the same country, or city. Even so, it's not sustainable.
Fortunately, we can represent our planet in a 2D map, using latitude and longitude.


![World map in 2D latitude and longitude](http://cse.ssl.berkeley.edu/segwayEd/lessons/search_ice_snow/worldmapL.gif)


Longitude -180 to 180  
Latitude -90 to 90

Great! Now we know that we can create a quadtree to represent the whole world. First we need to define the size of the deepest node/child, let's say the deepest node will have 100km * 100km. Then we'll add 1 million points of interest. To be able to show the insertion, i had to slow down the gif. 

![World Quadtree insertion gif](https://poppicture-57876.firebaseapp.com/quadtree/world-quadtree.gif)

Now, we are going to use the mouse pointer as the center of our search, the area is going to be 100km, therefore, any point of interest within 100km of the mouse pointer will be selected.

![World Quadtree insertion gif](https://poppicture-57876.firebaseapp.com/quadtree/world-quadtree-search.gif)

Using the example above, it does not take even a millisecond to get the search results. Running the same example with 10 million points we got the same results.

![World Quadtree insertion gif](https://poppicture-57876.firebaseapp.com/quadtree/world-quadtree-10kk-search.gif)

There's no limitation to the depth of a quadtree, we could set the size of the last node to 1km * 1km if we wanted to, that would save a lot of process power and time.

## The code part

First of all, we need to understand which parameters our node needs. Since the quadtree is a 2D structure the most important parameters are:

```java
public class Node {
    private double x, y, width, height;
}
```

With these parameters we have a rectangle, since we are using for geolocation, let's change the name of the parameters.

```java
public class Node {
    private double mLatitude;
    private double mLongitude;
    private double mWidth;
    private double mHeight;
}
```

Later, we are going to need to check if a point or range is within or intersecting our node, rather than make these calculations on our own, we can simply change these 4 params into one.

```java
public class Node {
    private Rectangle2D.Double mBounds;
}
```

Now, we need to declare each possible child/node.

```java
public class Node {
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
     * Creates a new node
     * @param latitude node's Y start point
     * @param longitude node's X start point
     * @param latitudeRange node's height
     * @param longitudeRange node's width
     */
    public QuadTreeNode(double latitude, double longitude, double latitudeRange, double longitudeRange) {
        mBounds = new Rectangle2D.Double(longitude, latitude, longitudeRange, latitudeRange);
    }
}
```
Now we need a list of points of interest.

```java
    /**
     *  List of points of interest A.K.A neighbours inside this node
     *  this list is only filled in the deepest nodes
     */
    protected List<Neighbour> mNeighbours = new ArrayList<>();
```

As explained before, nodes are only created when it's necessary. When a point of interest is added to a node, we have to navigate to the deepest node possible and then add the point of interest. The add method is described below:

```java
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
     * This methods finds and returns in which of the 4 child nodes the latitude and longitude is located.
     * If the node does not exists, it is created.
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
```

We are almost done with our quadtree node, we just need a method to locate points of interest. The perfect scenario would be where our point of search + range are completely contained inside a node, but we are developers, we have to be ready for the worst case scenario. We can divide our locate method in 2 parts:

1- Navigate until not fully contained  
2- Use intersection  

![Quadtree find example](https://poppicture-57876.firebaseapp.com/quadtree/quadtree-find-explanation.png)

Our search area is represented by the red rectangle, the navigation steps are these:

* The blue area "1" **fully contains** our search area.  
* The top right child of area "1" represented by area "2" **fully contains** our search area.  
* The bottom left child of area "2" represented by area "3" **fully contains** our search area.  
* None of the children from area "3" fully contains our search area, but, they **intersect**
    When intersection occurs, we will have to descend in every node that intersects  
* Finally when we reach each deepest node, if fully contained we add all the points of interest, otherwise we check if the search area contains each point of interest.  

```java
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
```

All right, our node is ready, full class below:

```java
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
```
Now that our quadtree node is done, we need a "wrapper", the class that will have the root quadtree node.

```java
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
```

```java
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

public class QuadTreeConstants {

    public static final double QUADTREE_LAST_NODE_SIZE_IN_KM = 100;

    public static final double QUADTREE_LAST_NODE_SIZE_IN_DEGREE = kmToDegree(QUADTREE_LAST_NODE_SIZE_IN_KM);

    public static final float ONE_DEGREE_IN_KM = 111.f;

    public static double kmToDegree(double km) {
        return km / ONE_DEGREE_IN_KM;
    }

}
```

Well, that's it folks, i hope you like this data structure as much as i do.
If you have any questions or feedbacks, please let me know.

See ya!
