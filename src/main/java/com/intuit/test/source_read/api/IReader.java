package com.intuit.test.source_read.api;

import com.intuit.test.model.entities.IPlayer;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.function.Consumer;

/**
 *  This interface declares functionality to read content from a data source
 * @param <T>
 */
public interface IReader<T> {

    void readContent(Consumer<T> consumer) throws Exception;



}
