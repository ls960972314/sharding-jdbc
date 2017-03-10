/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.sql.context.LimitContext;
import com.alibaba.druid.sql.context.SelectSQLContext;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.lexer.Token;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.ParserUnsupportedException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;

import java.util.List;

public class SQLServerExprParser extends SQLExprParser {
    
    public SQLServerExprParser(final ShardingRule shardingRule, final List<Object> parameters, final String sql) {
        super(shardingRule, parameters, new SQLServerLexer(sql));
        getLexer().nextToken();
    }
    
    public SQLServerTop parseTop() {
        // TODO
//        if (getLexer().equalToken(Token.TOP)) {
//            SQLServerTop top = new SQLServerTop();
//            getLexer().nextToken();
//            
//            boolean paren = false;
//            if (getLexer().equalToken(Token.LEFT_PAREN)) {
//                paren = true;
//                getLexer().nextToken();
//            }
//            
//            top.setExpr(primary());
//            
//            if (paren) {
//                getLexer().accept(Token.RIGHT_PAREN);
//            }
//            
//            if (getLexer().equalToken(Token.PERCENT)) {
//                getLexer().nextToken();
//                top.setPercent(true);
//            }
//            
//            return top;
//        }
        return null;
    }
    
    protected void skipOutput() {
        if (getLexer().equalToken(Token.OUTPUT)) {
            throw new ParserUnsupportedException(Token.OUTPUT);
        }
    }
    
    public void parseOffset(final SelectSQLContext sqlContext) {
        getLexer().nextToken();
        int offset;
        int offsetIndex = -1;
        if (getLexer().equalToken(Token.LITERAL_INT)) {
            offset = Integer.parseInt(getLexer().getLiterals());
        } else if (getLexer().equalToken(Token.QUESTION)) {
            offsetIndex = getParametersIndex();
            offset = (int) getParameters().get(offsetIndex);
            setParametersIndex(offsetIndex + 1);
        } else {
            throw new ParserException(getLexer());
        }
        getLexer().nextToken();
        LimitContext limitContext;
        if (getLexer().skipIfEqual(Token.FETCH)) {
            getLexer().nextToken();
            int rowCount;
            int rowCountIndex = -1;
            getLexer().nextToken();
            if (getLexer().equalToken(Token.LITERAL_INT)) {
                rowCount = Integer.parseInt(getLexer().getLiterals());
            } else if (getLexer().equalToken(Token.QUESTION)) {
                rowCountIndex = getParametersIndex();
                rowCount = (int) getParameters().get(rowCountIndex);
                setParametersIndex(rowCountIndex + 1);
            } else {
                throw new ParserException(getLexer());
            }
            getLexer().nextToken();
            getLexer().nextToken();
            limitContext = new LimitContext(offset, rowCount, offsetIndex, rowCountIndex);
        } else {
            limitContext = new LimitContext(offset, offsetIndex);
        }
        sqlContext.setLimitContext(limitContext);
    }
}