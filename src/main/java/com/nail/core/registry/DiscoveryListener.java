package com.nail.core.registry;

import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionState;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public interface DiscoveryListener {
    enum EvtType {
        /**
         * A child was added to the path
         */
        CHILD_ADDED,
        /**
         * A child's data was changed
         */
        CHILD_UPDATED,
        /**
         * A child was removed from the path
         */
        CHILD_REMOVED,
        /**
         * Called when the connection has changed to {@link ConnectionState#SUSPENDED}
         * <p>
         * This is exposed so that users of the class can be notified of issues that *might* affect normal operation.
         * The PathChildrenCache is written such that listeners are not expected to do anything special on this
         * event, except for those people who want to cause some application-specific logic to fire when this occurs.
         * While the connection is down, the PathChildrenCache will continue to have its state from before it lost
         * the connection and after the connection is restored, the PathChildrenCache will emit normal child events
         * for all of the adds, deletes and updates that happened during the time that it was disconnected.
         */
        CONNECTION_SUSPENDED,
        /**
         * Called when the connection has changed to {@link ConnectionState#RECONNECTED}
         * <p>
         * This is exposed so that users of the class can be notified of issues that *might* affect normal operation.
         * The PathChildrenCache is written such that listeners are not expected to do anything special on this
         * event, except for those people who want to cause some application-specific logic to fire when this occurs.
         * While the connection is down, the PathChildrenCache will continue to have its state from before it lost
         * the connection and after the connection is restored, the PathChildrenCache will emit normal child events
         * for all of the adds, deletes and updates that happened during the time that it was disconnected.
         */
        CONNECTION_RECONNECTED,
        /**
         * Called when the connection has changed to {@link ConnectionState#LOST}
         * <p>
         * This is exposed so that users of the class can be notified of issues that *might* affect normal operation.
         * The PathChildrenCache is written such that listeners are not expected to do anything special on this
         * event, except for those people who want to cause some application-specific logic to fire when this occurs.
         * While the connection is down, the PathChildrenCache will continue to have its state from before it lost
         * the connection and after the connection is restored, the PathChildrenCache will emit normal child events
         * for all of the adds, deletes and updates that happened during the time that it was disconnected.
         */
        CONNECTION_LOST,
        /**
         * Posted when {@link PathChildrenCache#start(PathChildrenCache.StartMode)} is called
         * with {@link PathChildrenCache.StartMode#POST_INITIALIZED_EVENT}. This
         * event signals that the initial cache has been populated.
         */
        INITIALIZED,

        UNKNOWN
    }

    void onChange(String path, byte[] data, EvtType evtType);
}
