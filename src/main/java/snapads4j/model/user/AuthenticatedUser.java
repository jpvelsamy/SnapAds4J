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
package snapads4j.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import snapads4j.model.AbstractSnapModel;

/**
 * Authenticated user informations
 *
 * @author Yassine
 */
@Getter
@Setter
@ToString
public class AuthenticatedUser extends AbstractSnapModel {

    /**
     * Email
     */
    private String email;

    /**
     * Organization ID
     */
    @JsonProperty("organization_id")
    private String organizationId;

    /**
     * Display name
     */
    @JsonProperty("display_name")
    private String displayName;
} // AuthenticatedUser
