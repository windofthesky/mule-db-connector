package org.mule.extension.db.internal.domain.connection.sqlserver;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.mule.extension.db.internal.domain.connection.DbConnectionProvider.DRIVER_FILE_NAME_PATTERN;
import static org.mule.extension.db.internal.domain.connection.sqlserver.SqlServerConnectionProvider.DRIVER_CLASS_NAME;
import static org.mule.runtime.extension.api.annotation.param.ParameterGroup.CONNECTION;
import org.mule.extension.db.api.exception.connection.DbError;
import org.mule.extension.db.internal.domain.connection.DataSourceConfig;
import org.mule.extension.db.internal.domain.connection.DbConnectionProvider;
import org.mule.runtime.api.meta.ExternalLibraryType;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.ExternalLib;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

@DisplayName("SQL Server Connection")
@Alias("mssql")
@ExternalLib(name = "Microsoft SQL Server Driver",
    description = "A JDBC driver which supports connecting to an Microsoft SQL Server Database",
    nameRegexpMatcher = DRIVER_FILE_NAME_PATTERN, requiredClassName = DRIVER_CLASS_NAME, type = ExternalLibraryType.JAR)
public class SqlServerConnectionProvider extends DbConnectionProvider {

  static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

  @ParameterGroup(name = CONNECTION)
  private SqlServerConnectionParameters connectionParameters;

  @Override
  public Optional<DataSource> getDataSource() {
    return empty();
  }

  @Override
  public Optional<DataSourceConfig> getDataSourceConfig() {
    return ofNullable(connectionParameters);
  }

  @Override
  protected Optional<DbError> getDbVendorErrorType(SQLException e) {
    return super.getDbVendorErrorType(e);
  }

  public void setConnectionParameters(SqlServerConnectionParameters connectionParameters) {
    this.connectionParameters = connectionParameters;
  }
}
