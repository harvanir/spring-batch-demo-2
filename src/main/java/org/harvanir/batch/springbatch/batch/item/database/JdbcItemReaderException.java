package org.harvanir.batch.springbatch.batch.item.database;

/**
 * @author Harvan Irsyadi
 */
public class JdbcItemReaderException extends RuntimeException {

    public JdbcItemReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}