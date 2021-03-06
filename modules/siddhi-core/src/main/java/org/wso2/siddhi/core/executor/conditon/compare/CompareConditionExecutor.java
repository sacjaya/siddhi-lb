/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.siddhi.core.executor.conditon.compare;

import org.wso2.siddhi.core.event.AtomicEvent;
import org.wso2.siddhi.core.executor.conditon.ConditionExecutor;
import org.wso2.siddhi.core.executor.conditon.compare.equal.EqualCompareConditionExecutor;
import org.wso2.siddhi.core.executor.conditon.compare.greater_than.GreaterThenCompareConditionExecutor;
import org.wso2.siddhi.core.executor.conditon.compare.greater_than_equal.GreaterThenEqualCompareConditionExecutor;
import org.wso2.siddhi.core.executor.conditon.compare.less_than.LessThenCompareConditionExecutor;
import org.wso2.siddhi.core.executor.conditon.compare.less_than_equal.LessThenEqualCompareConditionExecutor;
import org.wso2.siddhi.core.executor.conditon.compare.not_equal.NotEqualCompareConditionExecutor;
import org.wso2.siddhi.core.executor.expression.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.expression.ExpressionExecutor;
import org.wso2.siddhi.core.executor.expression.VariableExpressionExecutor;

public abstract class CompareConditionExecutor implements ConditionExecutor {

    public ExpressionExecutor leftExpressionExecutor;
    public ExpressionExecutor rightExpressionExecutor;

    public CompareConditionExecutor(ExpressionExecutor leftExpressionExecutor,
                                    ExpressionExecutor rightExpressionExecutor) {
        this.leftExpressionExecutor = leftExpressionExecutor;
        this.rightExpressionExecutor = rightExpressionExecutor;
    }


    public boolean execute(AtomicEvent event) {
        Object left = leftExpressionExecutor.execute(event);
        Object right = rightExpressionExecutor.execute(event);
        return !(left == null || right == null) && process(left, right);
    }

    protected abstract boolean process(Object left, Object right);

    @Override
    public String constructFilterQuery(AtomicEvent newEvent, int level) {
        String left;
        String right;
        boolean interchange = false;
        if (leftExpressionExecutor instanceof ConstantExpressionExecutor) {
            Object obj = leftExpressionExecutor.execute(newEvent);
            if (obj instanceof String) {
                left = "'" + obj.toString() + "'";
            } else {
                left = obj.toString();
            }
            interchange = true;
        } else if (leftExpressionExecutor instanceof VariableExpressionExecutor) {
            left = ((VariableExpressionExecutor) leftExpressionExecutor).constructFilterQuery(newEvent, level);
            if (left.startsWith("'")) {
                interchange = true;
            }
        } else {
            return "*";
        }
        if (rightExpressionExecutor instanceof ConstantExpressionExecutor) {
            Object obj = leftExpressionExecutor.execute(newEvent);
            if (obj instanceof String) {
                right = "'" + obj.toString() + "'";
            } else {
                right = obj.toString();
            }
            if (interchange) {
                return "*";
            }
        } else if (rightExpressionExecutor instanceof VariableExpressionExecutor) {
            right = ((VariableExpressionExecutor) rightExpressionExecutor).constructFilterQuery(newEvent, level);
            if (right.startsWith("'") && interchange) {
                return "*";
            }
        } else {
            return "*";
        }
        if (this instanceof EqualCompareConditionExecutor) {
            if (interchange) {
                return "(" + right + " == " + left + ")";
            } else {
                return "(" + left + " == " + right + ")";
            }
        } else if (this instanceof GreaterThenCompareConditionExecutor) {
            if (interchange) {
                return "(" + right + " < " + left + ")";
            } else {
                return "(" + left + " > " + right + ")";
            }
        } else if (this instanceof GreaterThenEqualCompareConditionExecutor) {
            if (interchange) {
                return new StringBuilder().append("(").append(right).append(" <= ").append(left).append(")").toString();
            } else {
                return new StringBuilder().append("(").append(left).append(" >= ").append(right).append(")").toString();
            }
        } else if (this instanceof LessThenCompareConditionExecutor) {
            if (interchange) {
                return new StringBuilder().append("(").append(right).append(" > ").append(left).append(")").toString();
            } else {
                return new StringBuilder().append("(").append(left).append(" < ").append(right).append(")").toString();
            }
        } else if (this instanceof LessThenEqualCompareConditionExecutor) {
            if (interchange) {
                return new StringBuilder().append("(").append(right).append(" >= ").append(left).append(")").toString();
            } else {
                return new StringBuilder().append("(").append(left).append(" <= ").append(right).append(")").toString();
            }
        } else if (this instanceof NotEqualCompareConditionExecutor) {
            if (interchange) {
                return "(" + right + " != " + left + ")";
            } else {
                return "(" + left + " != " + right + ")";
            }
        } else {
            return "*";
        }


    }
}
