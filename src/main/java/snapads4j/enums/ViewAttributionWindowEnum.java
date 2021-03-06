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

public enum ViewAttributionWindowEnum {
    @JsonProperty("ONE_HOUR")
    ONE_HOUR,
    @JsonProperty("THREE_HOUR")
    THREE_HOUR,
    @JsonProperty("SIX_HOUR")
    SIX_HOUR,
    @JsonProperty("1_DAY")
    ONE_DAY,
    @JsonProperty("7_DAY")
    SEVEN_DAY,
    @JsonProperty("28_DAY")
    TWENTY_EIGHT_DAY
}// ViewAttributionWindowEnum
