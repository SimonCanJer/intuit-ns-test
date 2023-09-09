package com.intuit.test.model.dao.observer.file;

import com.intuit.test.model.dao.api.ISubscribe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;

/**
 * This bean is dedicated to schedule data source file observation and triggering subscribers when changed
 * normally it must work on docker volumes
 */
public class FsObserver implements ISubscribe<String> {
    static private final int seconds = 120;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Path path;
    Instant lastChange;

    public FsObserver(String fileName) {
        File f = new File(fileName);

        path = f.toPath();
    }

    Instant readFileTime() {
        try {
            BasicFileAttributes fatr = Files.readAttributes(path,
                    BasicFileAttributes.class);
            return fatr.creationTime().toInstant();
        } catch (IOException e) {
            return null;
        }

    }

    @Override
    public void subscribe(BiConsumer<String, String> sink) {

    }
}
