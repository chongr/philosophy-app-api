package com.project.philosophy.find_path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.project.philosophy.core.PathToPhilosophy;
import com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhilosophyPathFinder {
    public static final String WIKIPEDIA_URL = "https://en.wikipedia.org";
    private static final Pattern wikiUrlParser = Pattern.compile(String.format("%s/wiki/(.*)/?", WIKIPEDIA_URL));
    private static final Logger LOGGER = LoggerFactory.getLogger(PhilosophyPathFinder.class);

    public PhilosophyPathFinder() {  
    }

    /*
    * removes all text within parenthases (within the html not including in tags) since those will not be examined for the next url
    * @param htmlElement Element to remove html parenthases
    * @returns an Element with the text in parenthases removed
    */
    private static String removeTextWithinParenthases(String htmlText) {
        int parenSeens = 0;
        int htmlTag = 0;
        StringBuilder htmlNoParenthasesText = new StringBuilder();
        for (int i = 0; i < htmlText.length(); i++) {
            char c = htmlText.charAt(i);
            if (c == '<') {
                htmlTag += 1;
            }
            else if (c == '>') {
                htmlTag -= 1;
            }
            else if (c == '(' && htmlTag == 0) {
                parenSeens += 1;
            }
            else if (c == ')' && htmlTag == 0) {
                parenSeens -= 1;
                continue;
            }
            if (parenSeens == 0) {
                htmlNoParenthasesText.append(c);
            }
        }
        return htmlNoParenthasesText.toString();
    }

    /*
    * removes all text within italic html tags (<i>) since those will not be examined for the next url
    * @param htmlElement Element to remove italics
    * @returns an Element with text in italic tags removed
    */
    private static String removeItalicText(String htmlText) {
        int iTagSeen = 0;
        int textLength = htmlText.length();
        StringBuilder htmlNoItalicsText = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            char c = htmlText.charAt(i);
            int nextCheckIndex = (i + 3 > textLength) ? textLength : i + 3;
            int prevCheckIndex = (i - 4 < 0) ? 0 : i - 4;
            if (htmlText.substring(i, nextCheckIndex).equals("<i>")) {
                iTagSeen += 1;
            }
            else if (htmlText.substring(prevCheckIndex, i).equals("</i>")) {
                iTagSeen -= 1;
            }
            if (iTagSeen == 0) {
                htmlNoItalicsText.append(c);
            }
        }
        return htmlNoItalicsText.toString();
    }

    /*
    * removes all text within italics or parenthases since those will not be examined for the next url
    * @param htmlElement Element to remove html with parenthases or italics
    * @returns an Element with th
    */
    private static Document removeTextWithinParenthasesAndItalics(Element htmlElement) {
        String rawHtmlText = htmlElement.toString();
        String rawHtmlNoParenthasesText = removeTextWithinParenthases(rawHtmlText);
        String rawHtmlCleaned = removeItalicText(rawHtmlNoParenthasesText);
        return Jsoup.parse(rawHtmlCleaned);
    }

    /*
    * find first link to philosophy based on rules in a set of html elements https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
    * @param htmlElements Elements ordered on the page to search for the next link
    * @returns a String of the next wikipedia url, null if none is found
    */
    private static String findFirstLinkInHtmlElements(Elements htmlElements) {
        for (Element htmlElement : htmlElements) {
            Document htmlInterestedIn = removeTextWithinParenthasesAndItalics(htmlElement);
            Elements anchorTags = htmlInterestedIn.select("a");
            if (anchorTags.size() > 0) {
                for (Element anchorTag : anchorTags) {
                    String link = anchorTag.attr("href");
                    // check that the link does not jump to another spot in the page (#)
                    // also skip links with https or http, since they lead to another site
                    if (link.charAt(0) != '#' && (!link.contains("http://") && !link.contains("https://"))) {
                        return String.format("%s%s", WIKIPEDIA_URL, link);
                    }
                }
            }
        }
        // no link found in given elements
        return null;
    }

    /*
    * get next url to philosophy based on rules https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
    * @returns a String of the next wikipedia url, null if none is found
    */
    public static String getNextURL(String currentURL) throws IOException {
        Document doc = Jsoup.connect(currentURL).get();
        // Check that main text div exists
        Elements mainTextDiv = doc.select("div.mw-parser-output");
        if (mainTextDiv == null) {
            return null;
        }
        Elements mainTextPTags = doc.select("div.mw-parser-output > p");
        String link = findFirstLinkInHtmlElements(mainTextPTags);
        if (link != null) {
            return link;
        }
        // Some pages only have li elements, search those if none in <p> tags
        Elements mainTextLiTags = doc.select("div.mw-parser-output > p");
        link = findFirstLinkInHtmlElements(mainTextLiTags);
        return link;
    }

    public static ArrayList<PathToPhilosophy> getPathToPhilosophy(String initialURL) throws IOException {
        ArrayList<PathToPhilosophy> philosophyPaths = new ArrayList<PathToPhilosophy>();
        ArrayList<String> linksBeenToOrdered = new ArrayList<String>();
        Set<String> linksSeen = new HashSet<String>();
        Boolean getsToPhilosophy = true;
        String nextLink = initialURL;

        while (!nextLink.equals("https://en.wikipedia.org/wiki/Philosophy")) {
            linksBeenToOrdered.add(nextLink);
            linksSeen.add(nextLink);
            nextLink = getNextURL(nextLink);
            if (nextLink == null || linksSeen.contains(nextLink)) {
                getsToPhilosophy = false;
                break;
            }
        }

        if (getsToPhilosophy) {
            int linksBeenToLength = linksBeenToOrdered.size();
            for (int i = 0; i < linksBeenToLength; i++) {
                String wikiLink = linksBeenToOrdered.get(i);
                LOGGER.info("makes it" + wikiLink);
                Matcher parseWikiURL = wikiUrlParser.matcher(wikiLink);
                parseWikiURL.matches();
                String pageTopic = parseWikiURL.group(1);
                String pathToPhilosophy = Joiner.on(',').join(linksBeenToOrdered.subList(i, linksBeenToLength));
                PathToPhilosophy newPath = new PathToPhilosophy(pageTopic, getsToPhilosophy, pathToPhilosophy);
                philosophyPaths.add(newPath);
            }
        } else {
            for (String linkBeenTo : linksBeenToOrdered) {
                LOGGER.info("doesn't makes it" + linkBeenTo);
                Matcher parseWikiURL = wikiUrlParser.matcher(linkBeenTo);
                parseWikiURL.matches();
                String pageTopic = parseWikiURL.group(1);
                PathToPhilosophy newPath = new PathToPhilosophy(pageTopic, getsToPhilosophy, null);
                philosophyPaths.add(newPath);
            }
        }

        return philosophyPaths;
    }
}