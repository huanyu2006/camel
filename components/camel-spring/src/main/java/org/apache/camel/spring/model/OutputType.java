/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.spring.model;

import org.apache.camel.spring.model.language.ExpressionType;
import org.apache.camel.spring.model.language.LanguageExpression;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * A useful base class for output types
 *
 * @version $Revision: 1.1 $
 */
@XmlType(name="outputType")
public abstract class OutputType extends ProcessorType {
    protected List<ProcessorType> outputs = new ArrayList<ProcessorType>();
    private List<InterceptorRef> interceptors = new ArrayList<InterceptorRef>();

    @XmlElementRef
    public List<ProcessorType> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ProcessorType> outputs) {
        this.outputs = outputs;
    }

    @XmlElement(required = false)
    public List<InterceptorRef> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<InterceptorRef> interceptors) {
        this.interceptors = interceptors;
    }


    // Fluent API
    //-------------------------------------------------------------------------
/*    public FilterType filter(ExpressionType expression) {
        FilterType filter = new FilterType();
        filter.setExpression(expression);
        getOutputs().add(filter);
        return filter;
    }

    public FilterType filter(String language, String expression) {
        return filter(new LanguageExpression(language, expression));
    }

    public OutputType to(String uri) {
        ToType to = new ToType();
        to.setUri(uri);
        getOutputs().add(to);
        return this;
    }*/
}
