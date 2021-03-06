/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.db.internal.resolver.query;

import static java.lang.String.format;
import static java.util.stream.Collectors.toCollection;
import org.mule.extension.db.api.param.OutputParameter;
import org.mule.extension.db.api.param.ParameterType;
import org.mule.extension.db.api.param.StoredProcedureCall;
import org.mule.extension.db.internal.DbConnector;
import org.mule.extension.db.internal.domain.connection.DbConnection;
import org.mule.extension.db.internal.domain.param.DefaultInOutQueryParam;
import org.mule.extension.db.internal.domain.param.DefaultInputQueryParam;
import org.mule.extension.db.internal.domain.param.DefaultOutputQueryParam;
import org.mule.extension.db.internal.domain.param.QueryParam;
import org.mule.extension.db.internal.domain.query.QueryTemplate;
import org.mule.extension.db.internal.domain.type.DbType;
import org.mule.extension.db.internal.domain.type.DbTypeManager;
import org.mule.extension.db.internal.domain.type.DynamicDbType;
import org.mule.runtime.api.util.Reference;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class StoredProcedureQueryResolver extends ParameterizedQueryResolver<StoredProcedureCall> {

  @Override
  protected QueryTemplate createQueryTemplate(StoredProcedureCall call, DbConnector connector, DbConnection connection) {
    QueryTemplate queryTemplate = super.createQueryTemplate(call, connector, connection);

    return new QueryTemplate(queryTemplate.getSqlText(),
                             queryTemplate.getType(),
                             resolveParamTypes(queryTemplate, call, connector, connection),
                             queryTemplate.isDynamic());
  }

  private List<QueryParam> resolveParamTypes(QueryTemplate queryTemplate, StoredProcedureCall call, DbConnector connector,
                                             DbConnection connection) {
    DbTypeManager typeManager = createTypeManager(connector, connection);
    return queryTemplate.getParams().stream().map(param -> {
      String paramName = param.getName();

      Optional<OutputParameter> outputParameter = call.getOutputParameter(paramName);
      if (outputParameter.isPresent()) {
        final ParameterType parameterType = outputParameter.get();
        DbType type = parameterType.getDbType() != null ? parameterType.getDbType() : param.getType();
        if (type instanceof DynamicDbType) {
          type = typeManager.lookup(connection, type.getName());
        }

        return new DefaultOutputQueryParam(param.getIndex(), type, paramName);
      }

      Optional<Reference<Object>> parameterValue = call.getInputParameter(paramName);
      if (parameterValue.isPresent()) {
        return new DefaultInputQueryParam(param.getIndex(), param.getType(), parameterValue.get().get(), paramName);
      }

      parameterValue = call.getInOutParameter(paramName);
      if (parameterValue.isPresent()) {
        return new DefaultInOutQueryParam(param.getIndex(), param.getType(), paramName, parameterValue.get().get());
      }

      throw new IllegalArgumentException(format("Parameter '%s' was not bound for query '%s'", paramName, call.getSql()));
    }).collect(toCollection(LinkedList::new));
  }

  @Override
  protected Optional<Reference<Object>> getInputParameter(StoredProcedureCall statementDefinition, String parameterName) {
    Optional<Reference<Object>> value = super.getInputParameter(statementDefinition, parameterName);
    if (!value.isPresent()) {
      value = statementDefinition.getInOutParameter(parameterName);
    }

    return value;
  }
}
