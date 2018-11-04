package sapphire.demo;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import sapphire.app.SapphireObject;
import sapphire.policy.cache.explicitcaching.ExplicitCacher;
import sapphire.policy.checkpoint.explicitcheckpoint.ExplicitCheckpointer;
import sapphire.policy.mobility.explicitmigration.ExplicitMigrator;
import sapphire.policy.mobility.explicitmigration.MigrationException;
import sapphire.policy.serializability.LockingTransaction;
import sapphire.policy.serializability.NoTransactionStartedException;
import sapphire.policy.serializability.TransactionAlreadyStartedException;
import sapphire.policy.serializability.TransactionException;

/**
 * A simple key value class for integration tests.
 *
 * Note: Make <code>KVStore</code> implementing
 * <code>SapphireObject</> interface in order to
 * backward compatible with annotation based sapphire
 * object specification.
 *
 * We can remove <code>SapphireObject</code> interface
 * after we completely deprecate annotation based
 * specification.
 */
public class KVStore
        implements LockingTransaction,
                ExplicitCacher,
                ExplicitCheckpointer,
                ExplicitMigrator,
                SapphireObject {
    private Map<String, Serializable> kvStore = new HashMap<>();

    public void set(String key, Serializable value) {
        this.kvStore.put(key, value);
    }

    public Serializable get(String key) {
        return this.kvStore.get(key);
    }

    @Override
    public void startTransaction(long timeoutMillisec)
            throws TransactionAlreadyStartedException, TransactionException {}

    @Override
    public void startTransaction()
            throws TransactionAlreadyStartedException, TransactionException {}

    @Override
    public void commitTransaction() throws NoTransactionStartedException, TransactionException {}

    @Override
    public void rollbackTransaction() throws NoTransactionStartedException, TransactionException {}

    @Override
    public void pull() {}

    @Override
    public void push() {}

    @Override
    public void saveCheckpoint() throws Exception {}

    @Override
    public void restoreCheckpoint() throws Exception {}

    @Override
    public void migrateObject(InetSocketAddress destinationAddr) throws MigrationException {}
}
