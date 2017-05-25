package org.williams.demo.tune;

import org.junit.Test;

import java.io.File;
import java.lang.IllegalArgumentException;

/**
 * Unit test for simple App.
 */

public class AppTest {
  //TODO: finish this test and add more tests
  @Test
  public void testApp() {
    File file = new File("target/test-classes/org/williams/demo/tune/test.json");
    String[] args = {file.getAbsolutePath(), "/tmp/output.csv"};
    App.main(args);
  }

  //this test works.
  @Test(expected = IllegalArgumentException.class)
  public void testReadJsonArray() {
    App app = new App();
    File file = new File("target/test-classes/org/williams/demo/tune/object.json");
    String jsonObject = file.getAbsolutePath();
    app.readJSONArray(app.readJSON(jsonObject));
  }
}
