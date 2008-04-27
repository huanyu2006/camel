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
package org.apache.camel.component.mail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.FlagTerm;

import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link org.apache.camel.Consumer Consumer} which consumes messages from JavaMail using a
 * {@link javax.mail.Transport Transport} and dispatches them to the {@link Processor}
 *
 * @version $Revision$
 */
public class MailConsumer extends ScheduledPollConsumer<MailExchange> implements MessageCountListener {
    private static final transient Log LOG = LogFactory.getLog(MailConsumer.class);
    private final MailEndpoint endpoint;
    private final Folder folder;

    public MailConsumer(MailEndpoint endpoint, Processor processor, Folder folder) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.folder = folder;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        folder.addMessageCountListener(this);
    }

    @Override
    protected void doStop() throws Exception {
        folder.removeMessageCountListener(this);
        if (folder.isOpen()) {
            folder.close(true);
        }
        super.doStop();
    }

    public void messagesAdded(MessageCountEvent event) {
        Message[] messages = event.getMessages();
        for (Message message : messages) {
            try {
                if (!message.getFlags().contains(Flags.Flag.DELETED)) {
                    processMessage(message);
                    flagMessageProcessed(message);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Skipping message as it was flagged as DELETED: " + message);
                    }
                }
            } catch (MessagingException e) {
                handleException(e);
            }
        }
    }

    public void messagesRemoved(MessageCountEvent event) {
        Message[] messages = event.getMessages();
        for (Message message : messages) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removed message number " + message.getMessageNumber());
            }
        }
    }

    protected void poll() throws Exception {
        ensureFolderIsOpen();

        try {
            int count = folder.getMessageCount();
            if (count > 0) {
                Message[] messages;

                // TODO: add unit test for this new property and add it to wiki documentation
                // should we process all messages or only unseen messages
                if (endpoint.getConfiguration().isProcessOnlyUnseenMessages()) {
                    messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                } else {
                    messages = folder.getMessages();
                }

                MessageCountEvent event = new MessageCountEvent(folder, MessageCountEvent.ADDED, true, messages);
                messagesAdded(event);
            } else if (count == -1) {
                throw new MessagingException("Folder: " + folder.getFullName() + " is closed");
            }
        } finally {
            // need to ensure we release resources
            if (folder.isOpen()) {
                folder.close(true);
            }
        }
    }

    protected void processMessage(Message message) {
        try {
            MailExchange exchange = endpoint.createExchange(message);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing message " + message);
            }
            getProcessor().process(exchange);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    protected void ensureFolderIsOpen() throws MessagingException {
        if (!folder.isOpen()) {
            folder.open(Folder.READ_WRITE);
        }
    }

    protected void flagMessageProcessed(Message message) throws MessagingException {
        if (endpoint.getConfiguration().isDeleteProcessedMessages()) {
            message.setFlag(Flags.Flag.DELETED, true);
        } else {
            message.setFlag(Flags.Flag.SEEN, true);
        }
    }
}
