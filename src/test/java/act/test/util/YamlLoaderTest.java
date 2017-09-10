package act.test.util;

/*-
 * #%L
 * ACT TEST
 * %%
 * Copyright (C) 2015 - 2017 ActFramework
 * %%
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
 * #L%
 */

import model.morphia.Address;
import model.morphia.Contact;
import org.junit.Test;

import java.util.Map;

public class YamlLoaderTest extends ActTestBase {

    private YamlLoader loader;

    @Override
    protected void setup() {
        loader = new YamlLoader();
    }

    @Test
    public void testLoad() throws Exception {
        Map<String, Object> repo = loader.load(getClass().getResource("/data-simple.yaml"), "model", null);
        Contact c1 = (Contact) repo.get("tom");
        eq("Tom", c1.getFirstName());
        eq("White", c1.getLastName());
        Address addr1 = c1.getAddress();
        eq("4 Park St", addr1.getStreet());
    }


}
