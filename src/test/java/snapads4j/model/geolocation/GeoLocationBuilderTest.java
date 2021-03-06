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
package snapads4j.model.geolocation;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import snapads4j.enums.OperationEnum;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationBuilderTest {

    @Test
    public void test_geolocation_builder_1() {
        GeoLocation geo = new GeoLocation.Builder().setOperation(OperationEnum.EXCLUDE).build();
        Assertions.assertThat(geo).isNotNull();
        Assertions.assertThat(geo.getOperation()).isEqualTo(OperationEnum.EXCLUDE);
    }// test_geolocation_builder_1()

    @Test
    public void test_geolocation_builder_2() {
        List<Integer> metroIds = new ArrayList<>();
        metroIds.add(1);
        GeoLocation geo = new GeoLocation.Builder().setMetroIds(metroIds).build();
        Assertions.assertThat(geo).isNotNull();
        Assertions.assertThat(geo.getMetroIds()).isNotNull();
        Assertions.assertThat(geo.getMetroIds()).isNotEmpty();
        Assertions.assertThat(geo.getMetroIds().get(0)).isEqualTo(1);
    }// test_geolocation_builder_2()

    @Test
    public void test_geolocation_builder_3() {
        List<Integer> postalCodes = new ArrayList<>();
        postalCodes.add(13);
        GeoLocation geo = new GeoLocation.Builder().setPostalCodes(postalCodes).build();
        Assertions.assertThat(geo).isNotNull();
        Assertions.assertThat(geo.getPostalCodes()).isNotNull();
        Assertions.assertThat(geo.getPostalCodes()).isNotEmpty();
        Assertions.assertThat(geo.getPostalCodes().get(0)).isEqualTo(13);
    }// test_geolocation_builder_3()

    @Test
    public void test_geolocation_builder_4() {
        GeoLocation geo = new GeoLocation.Builder().setCountryCode("us").build();
        Assertions.assertThat(geo).isNotNull();
        Assertions.assertThat(geo.getCountryCode()).isEqualTo("us");
    }// test_geolocation_builder_4()

    @Test
    public void test_geolocation_builder_5() {
        GeoLocation geo = new GeoLocation.Builder().setOperation(OperationEnum.EXCLUDE).build();
        Assertions.assertThat(geo).isNotNull();
        Assertions.assertThat(geo.toString()).isNotEmpty();
    }// test_geolocation_builder_5()
}// GeoLocationBuilderTest
