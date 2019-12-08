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
package snapads4j.model.creatives.elements;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class SnapHttpRequestInteractionZone {

    @JsonProperty("interaction_zones")
    private List<InteractionZone> interactionZones;

    public SnapHttpRequestInteractionZone() {
	this.interactionZones = new ArrayList<>();
    }// SnapHttpRequestInteractionZone()

    public void addInteractionZone(InteractionZone zone) {
	this.interactionZones.add(zone);
    }// addInteractionZone()
    
}// SnapHttpRequestInteractionZone
