package act.test.util;

import act.test.TestBase;
import model.morphia.Address;
import model.morphia.Contact;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
