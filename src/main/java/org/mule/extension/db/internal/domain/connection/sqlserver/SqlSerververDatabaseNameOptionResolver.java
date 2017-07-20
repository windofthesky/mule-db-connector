package org.mule.extension.db.internal.domain.connection.sqlserver;

import static org.mule.runtime.extension.api.values.ValueBuilder.getValuesFor;
import org.mule.extension.db.internal.domain.connection.DbConnection;
import org.mule.extension.db.internal.domain.connection.DbConnectionProvider;
import org.mule.extension.db.internal.result.resultset.ListResultSetHandler;
import org.mule.extension.db.internal.result.row.InsensitiveMapRowHandler;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.value.Value;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlSerververDatabaseNameOptionResolver implements ValueProvider, MuleContextAware {

  private MuleContext muleContext;

  @Parameter
  private String host;

  @Parameter
  Integer port;

  @Parameter
  String password;

  @Parameter
  String user;

  @Override
  public Set<Value> resolve() throws ValueResolvingException {
    SqlServerConnectionProvider sqlServerConnectionProvider = new SqlServerConnectionProvider();
    SqlServerConnectionParameters connectionParameters = new SqlServerConnectionParameters();
    connectionParameters.setHost(host);
    connectionParameters.setPort(port);
    connectionParameters.setPassword(password);
    connectionParameters.setUser(user);
    sqlServerConnectionProvider.setConnectionParameters(connectionParameters);

    try {
      Field configName = DbConnectionProvider.class.getDeclaredField("configName");
      configName.setAccessible(true);
      configName.set(sqlServerConnectionProvider, "someName");
      LifecycleUtils.initialiseIfNeeded(sqlServerConnectionProvider, true, muleContext);
      DbConnection connect = sqlServerConnectionProvider.connect();
      Statement statement = connect.getJdbcConnection().createStatement();
      ResultSet resultSet = statement.executeQuery("Select name from Sys.Databases where database_id > 4");
      ListResultSetHandler listResultSetHandler = new ListResultSetHandler(new InsensitiveMapRowHandler());
      List<Map<String, Object>> maps = listResultSetHandler.processResultSet(null, resultSet);

      return getValuesFor(maps.stream()
          .map(Map::values)
          .flatMap(Collection::stream)
          .map(Object::toString));

    } catch (ConnectionException | InitialisationException | NoSuchFieldException | IllegalAccessException | SQLException e) {
      throw new ValueResolvingException("An error occurred resolving the Data", "MGATO", e);
    }
  }

  @Override
  public void setMuleContext(MuleContext muleContext) {
    this.muleContext = muleContext;
  }
}
