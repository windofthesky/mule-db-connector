/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.db.internal.result.statement;

import org.mule.extension.db.internal.domain.autogeneratedkey.AutoGenerateKeysStrategy;
import org.mule.extension.db.internal.domain.connection.DbConnection;
import org.mule.extension.db.internal.domain.query.QueryTemplate;

import java.sql.Statement;

/**
 * Creates {@link StatementResultIterator} to process a {@link Statement} execution result
 */
public interface StatementResultIteratorFactory {

  StatementResultIterator create(DbConnection connection, Statement statement, QueryTemplate queryTemplate,
                                 AutoGenerateKeysStrategy autoGenerateKeysStrategy);
}
