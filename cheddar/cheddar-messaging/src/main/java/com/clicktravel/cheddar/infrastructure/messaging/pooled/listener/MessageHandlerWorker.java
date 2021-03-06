/*
 * Copyright 2014 Click Travel Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.clicktravel.cheddar.infrastructure.messaging.pooled.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clicktravel.cheddar.infrastructure.messaging.Message;
import com.clicktravel.cheddar.infrastructure.messaging.MessageHandler;

public class MessageHandlerWorker<T extends Message> implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final T message;
    private final MessageHandler<T> messageHandler;
    private final PooledMessageListener<T> pooledMessageListener;

    public MessageHandlerWorker(final PooledMessageListener<T> pooledMessageListener, final T message,
            final MessageHandler<T> messageHandler) {
        this.message = message;
        this.messageHandler = messageHandler;
        this.pooledMessageListener = pooledMessageListener;
    }

    @Override
    public void run() {
        try {
            messageHandler.handle(message);
        } catch (final Exception e) {
            logger.error("Error handling message: " + message, e);
        } finally {
            try {
                pooledMessageListener.completeMessageProcessing(message);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
