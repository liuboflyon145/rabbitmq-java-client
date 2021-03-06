// Copyright (c) 2007-Present Pivotal Software, Inc.  All rights reserved.
//
// This software, the RabbitMQ Java client library, is triple-licensed under the
// Mozilla Public License 1.1 ("MPL"), the GNU General Public License version 2
// ("GPL") and the Apache License version 2 ("ASL"). For the MPL, please see
// LICENSE-MPL-RabbitMQ. For the GPL, please see LICENSE-GPL2.  For the ASL,
// please see LICENSE-APACHE2.
//
// This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
// either express or implied. See the LICENSE file for specific language governing
// rights and limitations of this software.
//
// If you have any questions regarding licensing, please contact us at
// info@rabbitmq.com.

package com.rabbitmq.client.test.functional;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.test.BrokerTestCase;

import java.io.IOException;

public class DefaultExchange extends BrokerTestCase {
    String queueName;

    @Override
    protected void createResources() throws IOException {
        queueName = channel.queueDeclare().getQueue();
    }

    // See bug 22101: publish and declare are the only operations
    // permitted on the default exchange

    public void testDefaultExchangePublish() throws IOException {
        basicPublishVolatile("", queueName); // Implicit binding
        assertDelivered(queueName, 1);
    }

    public void testBindToDefaultExchange() throws IOException {
        try {
            channel.queueBind(queueName, "", "foobar");
            fail();
        } catch (IOException ioe) {
            checkShutdownSignal(AMQP.ACCESS_REFUSED, ioe);
        }
    }

    public void testUnbindFromDefaultExchange() throws IOException {
        try {
            channel.queueUnbind(queueName, "", queueName);
            fail();
        } catch (IOException ioe) {
            checkShutdownSignal(AMQP.ACCESS_REFUSED, ioe);
        }
    }

    public void testDeclareDefaultExchange() throws IOException {
        try {
            channel.exchangeDeclare("", "direct", true);
            fail();
        } catch (IOException ioe) {
            checkShutdownSignal(AMQP.ACCESS_REFUSED, ioe);
        }
    }

    public void testDeleteDefaultExchange() throws IOException {
        try {
            channel.exchangeDelete("");
            fail();
        } catch (IOException ioe) {
            checkShutdownSignal(AMQP.ACCESS_REFUSED, ioe);
        }
    }
}
