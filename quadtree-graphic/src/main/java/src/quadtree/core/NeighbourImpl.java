package src.quadtree.core;

/**
 *  Created by Leonardo Lana
 *  Github: https://github.com/leonardodlana
 *
 *  Copyright 2018 Leonardo Lana
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public class NeighbourImpl implements Neighbour {

    private final long mId;
    private final double mLatitude;
    private final double mLongitude;

    public NeighbourImpl(long id, double latitude, double longitude) {
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public double getLatitude() {
        return mLatitude;
    }

    @Override
    public double getLongitude() {
        return mLongitude;
    }
}
