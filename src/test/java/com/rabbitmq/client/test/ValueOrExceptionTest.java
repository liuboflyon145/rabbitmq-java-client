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

package com.rabbitmq.client.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.rabbitmq.utility.ValueOrException;
import com.rabbitmq.utility.SensibleClone;


public class ValueOrExceptionTest extends TestCase {
    public static class InsufficientMagicException extends Exception 
      implements SensibleClone<InsufficientMagicException> {
      /** Default for no check. */
        private static final long serialVersionUID = 1L;

    public InsufficientMagicException(String message) {
        super(message);
      }

      public InsufficientMagicException sensibleClone() {
        return new InsufficientMagicException(getMessage());
      }
    }


    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite("valueOrEx");
        suite.addTestSuite(ValueOrExceptionTest.class);
        return suite;
    }

    public void testStoresValue() throws InsufficientMagicException {
        Integer value = new Integer(3);

        ValueOrException<Integer, InsufficientMagicException> valueOrEx = 
            ValueOrException.makeValue(value);
        
        Integer returnedValue = valueOrEx.getValue();
        assertTrue(returnedValue == value);
    }

    public void testClonesException() {
        InsufficientMagicException exception = 
            new InsufficientMagicException("dummy message");
        ValueOrException<Integer, InsufficientMagicException> valueOrEx 
            = ValueOrException.makeException(exception);

        try {
            valueOrEx.getValue();
            fail("Expected exception");
        } catch(InsufficientMagicException returnedException) {
            assertTrue(returnedException != exception);
            assertEquals(returnedException.getMessage(), exception.getMessage());
            boolean inGetValue = false;
            for(StackTraceElement elt : returnedException.getStackTrace())
              inGetValue |= "getValue".equals(elt.getMethodName());
            assertTrue(inGetValue);
        }
    }
}
