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
public interface Drawable {

    abstract void draw(Graphics2D graphics2D, float zoom, int translateX, int translateY);

    /**
     * Update is called each time a frame is about to be rendered.
     * For example: if we are rendering at 60 FPS (Frames Per Second)
     * this method will be called 60 times per second
     *
     * @param difftime the difference in milliseconds from the last update
     */
    abstract void update(long difftime, float difftimeInSeconds);

}
