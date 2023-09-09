package com.intuit.test.spring.file_source.scheduler;

import com.intuit.test.model.dao.api.ISubscribe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.function.BiConsumer;

@Component
@EnableScheduling
public class SourceObserver implements ISubscribe {

    Instant last;

    @Value("${observer.chron}")
    String chron;

    @Value("${data.source.file}")
    String file;
    BiConsumer<Object,Object> sink;

    Path path;
    SourceObserver(){

    }

    @PostConstruct()
    void onInit(){
        File f= new File(file);

        path = f.toPath();

        last= readFileTime();
    }
    public
    @Scheduled(cron ="${observer.chron:*/10 * * * * *}")
    void scheduled(){
        Instant next=readFileTime();
        if(last!=null){
            if(next.isAfter(last)){
               if(sink!=null){
                   sink.accept(file,null);
               }
            }
        }

    }
    private Instant readFileTime(){
        try {
            BasicFileAttributes fatr = Files.readAttributes(path,
                    BasicFileAttributes.class);
            return fatr.lastModifiedTime().toInstant();
        } catch (IOException e) {
            return null;
        }

    }


    @Override
    public void subscribe(BiConsumer sink) {
        this.sink=sink;

    }
}
