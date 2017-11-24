package com.example.helloworld.core;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "path_to_philosophy")
public class PathToPhilosophy {
    @Id
    @Column(name = "page_topic")
    private String pageTopic;

    @Column(name = "reaches_philosophy", nullable = false)
    private Boolean reachesPhilosophy;

    @Column(name = "path", nullable = true)
    private String path;

    public PathToPhilosophy() {
    }

    public PathToPhilosophy(String pageTopic, Boolean reachesPhilosophy, String path) {
        this.pageTopic = pageTopic;
        this.reachesPhilosophy = reachesPhilosophy;
        this.path = path;
    }

    public String getPageTopic() {
        return pageTopic;
    }

    public void setPageTopic(String pageTopic) {
        this.pageTopic = pageTopic;
    }

    public Boolean getReachesPhilosophy() {
        return reachesPhilosophy;
    }

    public void setReachesPhilosophy(Boolean reachesPhilosophy) {
        this.reachesPhilosophy = reachesPhilosophy;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PathToPhilosophy)) {
            return false;
        }

        final PathToPhilosophy that = (PathToPhilosophy) o;

        return Objects.equals(this.pageTopic, that.pageTopic);
    }
}