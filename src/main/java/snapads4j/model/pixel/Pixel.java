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
package snapads4j.model.pixel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import snapads4j.enums.StatusEnum;
import snapads4j.model.AbstractSnapModel;

import javax.validation.constraints.NotEmpty;

/**
 * Snap Pixel
 *
 * @see {https://developers.snapchat.com/api/docs/#snap-pixel}
 * @author Yassine AZIMANI
 */
@Getter
@Setter
@ToString
@JsonInclude(Include.NON_EMPTY)
public class Pixel extends AbstractSnapModel {

    /**
     * Effective Status
     */
    @JsonProperty("effective_status")
    private String effectiveStatus;

    /**
     * Name
     */
    @NotEmpty(message = "Pixel name parameter is required")
    private String name;

    /**
     * Ad Account ID
     */
    @JsonProperty("ad_account_id")
    @NotEmpty(message = "Ad Account ID parameter is required")
    private String adAccountId;

    /**
     * Pixel status
     */
    private StatusEnum status;

    /**
     * code pixel in javascript
     */
    @JsonProperty("pixel_javascript")
    private String pixelJavascript;

}// Pixel
