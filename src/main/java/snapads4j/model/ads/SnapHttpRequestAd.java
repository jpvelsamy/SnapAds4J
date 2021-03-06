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
package snapads4j.model.ads;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * SnapHttpRequestAd
 *
 * @author Yassine AZIMANI
 */
@Getter
public class SnapHttpRequestAd {

    /**
     * Array Ad json
     */
    private final List<Ad> ads;

    /**
     * Build SnapHttpRequestAd
     */
    public SnapHttpRequestAd() {
        this.ads = new ArrayList<>();
    }// SnapHttpRequestAd()

    /**
     * Add Ad to request HTTP
     * @param ad adAccount
     */
    public void addAd(Ad ad) {
        this.ads.add(ad);
    }// addAd()
}// SnapHttpRequestAd
