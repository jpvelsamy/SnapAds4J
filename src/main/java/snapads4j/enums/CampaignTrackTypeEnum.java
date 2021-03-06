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
package snapads4j.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CampaignTrackTypeEnum {
    @JsonProperty("APP_INSTALL")
    APP_INSTALL,
    @JsonProperty("DEEP_LINK")
    DEEP_LINK,
    @JsonProperty("STORY")
    STORY,
    @JsonProperty("LENS_APP_INSTALL")
    LENS_APP_INSTALL,
    @JsonProperty("LENS_DEEP_LINK")
    LENS_DEEP_LINK
} // MeasurementSpecEnum
