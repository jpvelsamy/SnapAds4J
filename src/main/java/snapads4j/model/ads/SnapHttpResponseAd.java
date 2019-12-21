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

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import snapads4j.model.SnapHttpResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
@NoArgsConstructor
public class SnapHttpResponseAd extends SnapHttpResponse {

    private List<SnapInnerAd> ads;

    public Optional<Ad> getSpecificAd() {
        return (CollectionUtils.isNotEmpty(ads) && ads.get(0) != null) ? Optional.of(ads.get(0).getAd())
                : Optional.empty();
    }// getSpecificAd()

    public List<Ad> getAllAd() {
        return ads.stream().map(SnapInnerAd::getAd).collect(Collectors.toList());
    }// getAllAd()
}// SnapHttpResponseAd
