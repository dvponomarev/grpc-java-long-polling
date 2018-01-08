package com.payneteasy.grpc.longpolling.client;

import com.payneteasy.grpc.longpolling.common.TransportId;
import io.grpc.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.SocketAddress;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class LongPollingClientTransportFactory implements ClientTransportFactory {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollingClientTransportFactory.class);

    private final ExecutorService          executorService;
    private final TransportId              transportId;
    private final URL                      baseUrl;

    private final ScheduledExecutorService timeoutService = SharedResourceHolder.get(GrpcUtil.TIMER_SERVICE);
    private       boolean                  closed         = false;

    public LongPollingClientTransportFactory(ExecutorService aExecutor, TransportId aId, URL aBaseUrl) {
        executorService = aExecutor;
        transportId = aId;
        baseUrl = aBaseUrl;
    }

    @Override
    public ConnectionClientTransport newClientTransport(SocketAddress aAddress, String authority, @Nullable String userAgent, @Nullable ProxyParameters proxy) {
        LOG.trace("newClientTransport(address={}, authority:{}, userAgent:{}, proxy:{})", aAddress, authority, userAgent, proxy);
        return new LongPollingClientTransport(executorService, baseUrl, transportId);
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        LOG.trace("getScheduledExecutorService()");
        return timeoutService;
    }

    @Override
    public void close() {
        LOG.trace("close(). Releasing all resources...");
        if(!closed) {
            SharedResourceHolder.release(GrpcUtil.TIMER_SERVICE, timeoutService);
        }
        closed = true;
    }
}
