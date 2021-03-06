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
package snapads4j.model.thirdparty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwipeTrackingUrl extends TrackingUrl {

    public static class Builder {

        private final SwipeTrackingUrl instance;

        public Builder() {
            this.instance = new SwipeTrackingUrl();
        }// Builder()

        public Builder setExpandedTrackingUrl(String expandedTrackingUrl) {
            this.instance.setExpandedTrackingUrl(expandedTrackingUrl);
            return this;
        }// setExpandedTrackingUrl()

        public Builder setTrackingUrl(String trackingUrl) {
            this.instance.setTrackingUrl(trackingUrl);
            return this;
        }// setTrackingUrl()

        public Builder setTrackingUrlMetadata(String trackingUrlMetadata) {
            this.instance.setTrackingUrlMetadata(trackingUrlMetadata);
            return this;
        }// setTrackingUrlMetadata()

        public SwipeTrackingUrl build() {
            return this.instance;
        }// build()
    }// Builder

}// SwipeTrackingUrl
