package com.intuit.test.model.dao.in_mem;

import com.intuit.test.model.dao.api.IDao;
import com.intuit.test.model.dao.api.ISubscribe;
import com.intuit.test.model.entities.IPlayer;
import com.intuit.test.source_read.cvs.csvBean.Player;
import com.intuit.test.source_read.api.IReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DAO which keeping data in memory as map(id->player) and
 * simply returning object as a key of the map.
 * Additionally, the class handles notification about a changes in data source and
 * reloads data in backgounds and then safely replaces old map by a new data
 */
@Slf4j
@ComponentScan("com.intuit.test.model.dao.observer.file")
public class MemoryDao implements IDao {
    /**
     * atomic container, which can content can be changed in background of
     */
    private final IReader<IPlayer> reader;
    /**
     * reference to pplayers map which can be replaced dynamically
     */
    private final AtomicReference<Map<String, IPlayer>> atomicPlayers = new AtomicReference<>();

    /**
     * Parametric constructor, which  accepts reader and sibsciber interfaces
     * @param reader
     * @param subscriber
     */
    public MemoryDao(IReader<IPlayer> reader, ISubscribe<String> subscriber) {
        this.reader = reader;
        try {
            fillMap(new ConcurrentHashMap<>());
            log.info("in mem mapping data readen from file succesfully,  mapped= {} entries", atomicPlayers.get().size());
            if (subscriber != null) {

                subscriber.subscribe((s, v) -> {
                    Thread t= new Thread(()->{
                        fillMap(new ConcurrentHashMap<>());
                    });
                    t.start();

                });
            }
        } catch (Exception e) {
            log.error("fatal error: cannot read file");
            throw new RuntimeException(e);
        }

    }

    /**
     * implementer of interface methods.
     * returns a copy of values of mapes
     *
     * @return: the values announced
     */
    @Override
    public Flux<IPlayer> allPlayers() {

        return Flux.create((fs -> {
            Iterator<IPlayer> it = atomicPlayers.get().values().stream().toList().iterator();
            while (it.hasNext() && !fs.isCancelled()) {
                fs.next(it.next());
            }
            fs.complete();
        }));
    }

    /**
     * returns  Player by id
     *
     * @param id the id announced
     * @return Either a {@link Player} object if found, or null if not.
     */
    @Override
    public IPlayer get(String id) {
        return atomicPlayers.get().get(id);
    }


    private void fillMap(Map<String, IPlayer> map) {

        try {
            reader.readContent((p) -> {
                map.put(p.getPlayerID(), p);
            });
            atomicPlayers.set(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
