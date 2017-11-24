package com.example.helloworld.resources;

import com.example.helloworld.core.PathToPhilosophy;
import com.example.helloworld.db.PathToPhilosophyDAO;
import io.dropwizard.hibernate.UnitOfWork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/wiki-page/{wikiPageTopic}/path-to-philosophy")
@Produces(MediaType.APPLICATION_JSON)
public class PathToPhilosophyResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PathToPhilosophyResource.class);
    private final String WIKIPEDIA_URL = "https://en.wikipedia.org";
    private final Pattern wikiUrlParser = Pattern.compile(String.format("%s/wiki/(.*)/?", WIKIPEDIA_URL));

    private final PathToPhilosophyDAO pathToPhilosophyDAO;

    public PathToPhilosophyResource(PathToPhilosophyDAO pathToPhilosophyDAO) {
        this.pathToPhilosophyDAO = pathToPhilosophyDAO;
    }

    @GET
    @UnitOfWork
    public Optional<PathToPhilosophy> getPhilosophyPath(@PathParam("wikiPageTopic") String wikiPageTopic) {
        return pathToPhilosophyDAO.findByPageTopic(wikiPageTopic);
    }
}