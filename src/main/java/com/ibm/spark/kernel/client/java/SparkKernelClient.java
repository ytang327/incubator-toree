package com.ibm.spark.kernel.client.java;

import scala.concurrent.Future;
import scala.runtime.AbstractFunction0;
import scala.runtime.BoxedUnit;

/**
 * Java adapter for {@link com.ibm.spark.client.SparkKernelClient}
 */
public class SparkKernelClient {

    protected com.ibm.spark.client.SparkKernelClient client;

    public SparkKernelClient(com.ibm.spark.client.SparkKernelClient client) {
        this.client = client;
    }

    /**
     * Pings the Spark Kernel.
     * @param failure callback on failure
     */
    public void heartbeat(EmptyFunction failure) {
        this.client.heartbeat(wrap(failure));
    }

    /**
     * Sends code to the Spark Kernel for execution.
     * @param code code to run
     */
    public Future<Object> submit(String code) {
        return this.client.submit(code);
    }

    private AbstractFunction0<BoxedUnit> wrap(final EmptyFunction func) {
        return new AbstractFunction0<BoxedUnit>() {
            @Override
            public BoxedUnit apply() {
                func.invoke();
                return BoxedUnit.UNIT;
            }
        };
    }
}