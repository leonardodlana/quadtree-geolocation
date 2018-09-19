package src;

import java.awt.*;

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
public abstract class BaseObject implements Drawable {

    private int mPositionX;
    private int mPositionY;
    private int mWidth;
    private int mHeight;

    public BaseObject(int x, int y, int width, int height) {
        mPositionX = x;
        mPositionY = y;
        mWidth = width;
        mHeight = height;
    }

    public int getX() {
        return mPositionX;
    }

    public int getY() {
        return mPositionY;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

}
