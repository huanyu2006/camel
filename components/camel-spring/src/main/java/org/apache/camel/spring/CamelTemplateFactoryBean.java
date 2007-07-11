/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.spring;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.CamelTemplate;
import org.apache.camel.Endpoint;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * A Spring {@link FactoryBean} for creating a new {@link CamelTemplate} instance
 * with a minimum of XML
 *
 * @version $Revision: $
 */
@XmlRootElement(name = "camelTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CamelTemplateFactoryBean implements FactoryBean, InitializingBean, CamelContextAware {
    @XmlAttribute(required = false)
    private String defaultEndpoint;
    @XmlTransient
    private CamelContext camelContext;

    public void afterPropertiesSet() throws Exception {
        if (camelContext == null) {
            throw new IllegalArgumentException("A CamelContext must be injected!");
        }
    }

    public Object getObject() throws Exception {
        CamelContext context = getCamelContext();
        if (defaultEndpoint != null) {
            Endpoint endpoint = context.getEndpoint(defaultEndpoint);
            if (endpoint == null) {
                throw new IllegalArgumentException("No endpoint found for URI: " + defaultEndpoint);
            }
            else {
                return new CamelTemplate(context, endpoint);
            }
        }
        return new CamelTemplate(context);
    }

    public Class getObjectType() {
        return CamelTemplate.class;
    }

    public boolean isSingleton() {
        return true;
    }


    // Properties
    //-------------------------------------------------------------------------
    public CamelContext getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }

    /**
     * Sets the default endpoint URI used by default for sending message exchanges
     */
    public void setDefaultEndpoint(String defaultEndpoint) {
        this.defaultEndpoint = defaultEndpoint;
    }
}
