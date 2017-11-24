package com.example.helloworld.db;

import com.example.helloworld.core.PathToPhilosophy;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.util.Optional;

public class PathToPhilosophyDAO extends AbstractDAO<PathToPhilosophy> {

    public PathToPhilosophyDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<PathToPhilosophy> findByPageTopic(String pageTopic) {
        return Optional.ofNullable(get(pageTopic));
    }

    public PathToPhilosophy create(PathToPhilosophy pathToPhilosophy) {
        return persist(pathToPhilosophy);
    }
}