package com.example.helloworld.resources;

import com.example.helloworld.core.PathToPhilosophy;
import com.example.helloworld.db.PathToPhilosophyDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Optional;

@Path("/path-to-philosophy/{wikiPageTopic}")
@Produces(MediaType.APPLICATION_JSON)
public class PathToPhilosophyResource {
    private final PathToPhilosophyDAO pathToPhilosophyDAO;

    public PathToPhilosophyResource(PathToPhilosophyDAO pathToPhilosophyDAO) {
        this.pathToPhilosophyDAO = pathToPhilosophyDAO;
    }

    @GET
    @UnitOfWork
    public Optional<PathToPhilosophy> getPhilosophyPath(@PathParam("wikiPageTopic") String wikiPageTopic) throws IOException {
        Optional<PathToPhilosophy> pathInDb = pathToPhilosophyDAO.findByPageTopic(wikiPageTopic);
        if (!pathInDb.isPresent()) {
            return pathToPhilosophyDAO.findAndSavePath(wikiPageTopic);
        }
        return Optional.ofNullable(pathInDb.get());
    }
}
