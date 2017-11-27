package com.project.philosophy.db;

import com.project.philosophy.core.PathToPhilosophy;
import com.project.philosophy.find_path.PhilosophyPathFinder;

import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;
import org.jsoup.HttpStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class PathToPhilosophyDAO extends AbstractDAO<PathToPhilosophy> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PathToPhilosophyDAO.class);

    public PathToPhilosophyDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<PathToPhilosophy> findByPageTopic(String pageTopic) {
        return Optional.ofNullable(get(pageTopic));
    }

    public PathToPhilosophy create(PathToPhilosophy pathToPhilosophy) {
        return persist(pathToPhilosophy);
    }

    public Optional<PathToPhilosophy> findAndSavePath(String pageTopic) throws IOException {
        String wikiURL = String.format("%s/wiki/%s", PhilosophyPathFinder.WIKIPEDIA_URL, pageTopic);
        ArrayList<PathToPhilosophy> pathsCalculated;
        try {
            pathsCalculated = PhilosophyPathFinder.getPathToPhilosophy(wikiURL);
        } catch (HttpStatusException e) {
            LOGGER.info(e.toString());
            if (e.getStatusCode() == 404) {
                return Optional.ofNullable(null);
            }
            throw e;
        }
        // TODO: SQL queries can be reduced in this bulk create or update
        for (PathToPhilosophy newPath : pathsCalculated) {
            persist(newPath);
        }
        return Optional.ofNullable(pathsCalculated.get(0));
    }
}