package com.example.helloworld.db;

import com.example.helloworld.core.PathToPhilosophy;
import com.example.helloworld.find_path.PhilosophyPathFinder;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.io.IOException;
import java.util.ArrayList;
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

    public PathToPhilosophy findAndSavePath(String pageTopic) throws IOException {
        String wikiURL = String.format("%s/wiki/%s", PhilosophyPathFinder.WIKIPEDIA_URL, pageTopic);
        ArrayList<PathToPhilosophy> pathsCalculated = PhilosophyPathFinder.getPathToPhilosophy(wikiURL);
        for (PathToPhilosophy newPath : pathsCalculated) {
            persist(newPath);
        }
        return pathsCalculated.get(0);
    }
}