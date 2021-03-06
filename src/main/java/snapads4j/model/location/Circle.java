/*
 * Copyright 2019 Yassine AZIMANI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package snapads4j.model.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import snapads4j.enums.ProximityUnitEnum;

/**
 * Circle
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Circle {

    /**
     * Latitude
     */
    private double latitude;

    /**
     * Longitude
     */
    private double longitude;

    /**
     * Radius
     */
    private int radius;

    /**
     * Unit used
     */
    private ProximityUnitEnum unit;

    /**
     * Build a circle
     * @param latitude latitude
     * @param longitude longitude
     * @param radius radius
     */
    public Circle(double latitude, double longitude, int radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    } // Circle()
} // Circle
