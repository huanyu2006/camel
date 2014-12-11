/**
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
package org.apache.camel.commands.jolokia;

import org.jolokia.client.J4pClient;
import org.jolokia.client.J4pClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.util.ObjectHelper.isNotEmpty;

/**
 * A factory to create a {@link org.jolokia.client.J4pClient} jolokia client that connects to a remote JVM.
 */
public final class JolokiaClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(JolokiaClientFactory.class);

    private JolokiaClientFactory() {
    }

    public static J4pClient createJolokiaClient(String jolokiaUrl, String username, String password) {
        LOG.info("Creating jolokia client at URL: {}", jolokiaUrl);

        J4pClientBuilder builder = J4pClient.url(jolokiaUrl);
        if (isNotEmpty(username)) {
            builder = builder.user(username);
        }
        if (isNotEmpty(password)) {
            builder = builder.password(password);
        }
        return builder.build();
    }

}
