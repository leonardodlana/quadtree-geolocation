package src.quadtree;

import src.Drawable;
import src.quadtree.core.QuadTree;

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
public class DrawableQuadTree extends QuadTree implements Drawable {

    private final int mDrawableWidth;
    private final int mDrawableHeight;

    public DrawableQuadTree(int drawableWidth, int drawableHeight) {
        super(new DrawableQuadTreeNode(0, 0, TOTAL_Y_DEGREES, TOTAL_X_DEGREES,
                (float)drawableWidth / (float)TOTAL_X_DEGREES, (float)drawableHeight / (float)TOTAL_Y_DEGREES, drawableWidth, drawableHeight));
        mDrawableWidth = drawableWidth;
        mDrawableHeight = drawableHeight;
    }

    @Override
    public void draw(Graphics2D graphics2D, float zoom, int translateX, int translateY) {
        ((DrawableQuadTreeNode) getRootNode()).draw(graphics2D, zoom, translateX, translateY );
    }

    @Override
    public void update(long difftime, float difftimeInSeconds) {
        ((DrawableQuadTreeNode) getRootNode()).update(difftime, difftimeInSeconds);
    }

}
