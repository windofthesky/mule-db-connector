package org.mule.extension.db.integration.select;

import org.mule.extension.db.integration.DbArtifactClassLoaderRunnerConfig;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.metadata.resolving.MetadataResult;
import org.mule.runtime.api.options.Value;
import org.mule.runtime.core.api.construct.Flow;
import org.mule.runtime.module.extension.internal.runtime.ExtensionComponent;
import org.mule.runtime.module.extension.internal.runtime.config.ConfigurationProviderToolingAdapter;
import org.junit.Test;

import java.util.Set;

/**
 * Created by estebanwasinger on 6/19/17.
 */
public class SelectMssqlTestCase extends MuleArtifactFunctionalTestCase implements DbArtifactClassLoaderRunnerConfig {

  @Override
  protected String getConfigFile() {
    return "integration/config/sql-server-config.xml";
  }

  @Test
  public void testCase() throws Exception {
    ConfigurationProviderToolingAdapter dbConfig = muleContext.getRegistry().get("dbConfig");
    Set<Value> databaseName = dbConfig.getConnectionValues("databaseName");

    TypedValue<Object> payload = flowRunner("some-test").run().getMessage().getPayload();
    Flow flow = (Flow) getFlowConstruct("some-test");
    ExtensionComponent processor = (ExtensionComponent) flow.getProcessors().get(0);
    MetadataResult metadata = processor.getMetadata();
  }
}
