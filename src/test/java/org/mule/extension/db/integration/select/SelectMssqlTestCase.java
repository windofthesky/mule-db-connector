package org.mule.extension.db.integration.select;

import org.mule.extension.db.integration.DbArtifactClassLoaderRunnerConfig;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.value.Value;
import org.mule.runtime.module.extension.internal.runtime.config.ConfigurationProviderToolingAdapter;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.util.TestConnectivityUtils;
import com.google.gson.GsonBuilder;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

/**
 * Created by estebanwasinger on 6/19/17.
 */
public class SelectMssqlTestCase extends MuleArtifactFunctionalTestCase implements DbArtifactClassLoaderRunnerConfig {

  @Rule
  public SystemProperty systemProperty = TestConnectivityUtils.disableAutomaticTestConnectivity();

  @Override
  protected String getConfigFile() {
    return "integration/config/sql-server-config.xml";
  }

  @Test
  public void testCase() throws Exception {
    ConfigurationProviderToolingAdapter dbConfig = muleContext.getRegistry().get("dbConfig");
    Set<Value> databaseName = dbConfig.getConnectionValues("databaseName");
    System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(databaseName));
  }
}
