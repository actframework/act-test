package act.test.util;

import act.test.TestBase;
import model.morphia.Address;
import model.morphia.Contact;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class YamlLoaderTest extends TestBase {

    private YamlLoader loader;

    @Before
    public void prepare() {
        loader = new YamlLoader();
    }

    @Test
    public void testLoad() throws Exception {
        Map<Class, List> repo = loader.load(getClass().getResource("/data-simple.yaml"), "model", null);
        List<Contact> list = repo.get(Contact.class);
        Contact c1 = list.get(0);
        eq("Tom", c1.getFirstName());
        eq("White", c1.getLastName());
        Address addr1 = c1.getAddress();
        eq("4 Park St", addr1.getStreet());
    }

}
