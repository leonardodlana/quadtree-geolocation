package src;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
public class Screen extends BaseObject {

    private List<BaseObject> mDrawableList = new ArrayList<>();

    public Screen(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(Graphics2D graphics2D, float zoom, int translateX, int translateY) {
        for (BaseObject drawable : mDrawableList) {
            drawable.draw(graphics2D, zoom, translateX, translateY);
        }
    }

    @Override
    public void update(long difftime, float difftimeInSeconds) {
        for (BaseObject drawable : mDrawableList) {
            drawable.update(difftime, difftimeInSeconds);
        }
    }

    protected void addDrawable(BaseObject drawable) {
        mDrawableList.add(drawable);
    }

    protected void removeDrawable(BaseObject drawable) {
        mDrawableList.remove(drawable);
    }

    public void onMouseMoved(int x, int y) {

    }

    public void onMouseClick(int x, int y) {

    }
}
