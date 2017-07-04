package org.mule.extension.db.internal.domain.connection.sqlserver;

import static org.mule.runtime.api.options.ValueBuilder.newOption;
import org.mule.extension.db.internal.domain.connection.DbConnection;
import org.mule.extension.db.internal.domain.connection.DbConnectionProvider;
import org.mule.extension.db.internal.result.resultset.ListResultSetHandler;
import org.mule.extension.db.internal.result.row.InsensitiveMapRowHandler;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.options.ValueBuilder;
import org.mule.runtime.api.options.Value;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;
import org.mule.runtime.extension.api.values.ValueProvider;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by estebanwasinger on 6/20/17.
 */
public class MsSqlSerververDatabaseNameOptionResolver implements ValueProvider, MuleContextAware {

    private MuleContext muleContext;

    @Override
    public Set<Value> resolve() {
        SqlServerConnectionProvider sqlServerConnectionProvider = new SqlServerConnectionProvider();
        SqlServerConnectionParameters connectionParameters = new SqlServerConnectionParameters();
        connectionParameters.setHost((String) context.getRequiredParameters().get("host"));
        connectionParameters.setPort((Integer) context.getRequiredParameters().get("port"));
        connectionParameters.setPassword((String) context.getRequiredParameters().get("password"));
        connectionParameters.setUser((String) context.getRequiredParameters().get("user"));
        sqlServerConnectionProvider.setConnectionParameters(connectionParameters);

        try {
            Field configName = DbConnectionProvider.class.getDeclaredField("configName");
            configName.setAccessible(true);
            configName.set(sqlServerConnectionProvider, "someName");
            LifecycleUtils.initialiseIfNeeded(sqlServerConnectionProvider, true, muleContext);
            DbConnection connect = sqlServerConnectionProvider.connect();
            System.out.println(connect);
            Statement statement = connect.getJdbcConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("Select Name from Sys.Databases");
//            ResultSet resultSet = statement.executeQuery("SELECT NAME\n" +
//                    "FROM sys.Tables\n" +
//                    "WHERE is_ms_shipped = 'false'");
            ListResultSetHandler listResultSetHandler = new ListResultSetHandler(new InsensitiveMapRowHandler());
            List<Map<String, Object>> maps = listResultSetHandler.processResultSet(null, resultSet);
            return maps.stream()
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .map(value -> ValueBuilder.newValue((String) value).build())
                    .collect(Collectors.toSet());

//            connect.getStatementResultIteratorFactory().
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (InitialisationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptySet();
    }

    @Override
    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }
}
