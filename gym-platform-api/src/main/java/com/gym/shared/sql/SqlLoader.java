package com.gym.shared.sql;

/**
 * Loads the contents of a single SQL file from a resource path. Its SOLE
 * responsibility is to return the SQL text: it does NOT execute, bind
 * parameters or map results - those remain the job of the persistence adapter.
 *
 * <p>Adapters depend on this abstraction rather than a concrete implementation,
 * so the SQL source (classpath, a different cache, a test stub) can be swapped
 * without changing the adapter (DIP).
 */
public interface SqlLoader {

    /**
     * Loads a SQL file from the classpath.
     *
     * @param resourcePath the classpath path to the SQL file,
     *                     e.g. {@code "sql/customer/insert_customer.sql"}
     * @return the SQL file contents
     * @throws IllegalArgumentException if the resource does not exist on the classpath
     */
    String load(String resourcePath);
}
