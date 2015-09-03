package com.northernthrone.leaguecalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StandingsParser {

    private static Match parseMatch(String input) {
        Pattern pattern = Pattern.compile("(?<p1>.*?) - (?<p2>.*?): (?<score>[0-9]*,?[0-9]*) - .*");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Match match = new Match();
            match.setP1(matcher.group("p1"));
            match.setP2(matcher.group("p2"));
            match.setActualResult(Float.parseFloat(matcher.group("score").replace(',', '.')));
            return match;
        } else {
            return null;
        }
    }

    public static String parseLeagueBoard(Document document) {
        Elements elements = document.getElementsMatchingOwnText("Season [0-9]+ - Standings");
        if (elements.isEmpty()) {
            return null;
        }
        return elements.get(0).attr("href");
    }

    public static List<Group> parseStandingsThread(Document document) {
        Elements posts = document.getElementsByAttributeValue("class", "post");

        Element originalPost = (Element) ((Element) posts.toArray()[0]).getElementsByAttributeValue("class", "inner").toArray()[0];
        Object[] postChildren = originalPost.children().toArray();

        List<Group> groups = new ArrayList<>();
        for (Object postChildren1 : postChildren) {
            Element element = (Element) postChildren1;
            String tagName = element.tag().getName();
            if (tagName.equals("strong")) {
                groups.add(new Group(((Element) element.getElementsByTag("a").toArray()[0]).text()));
                continue;
            }
            if (tagName.equals("table")) {
                ((Element) element.getElementsByTag("a").toArray()[0]).text();

                for (Object playerNameTag : element.getElementsByTag("a").toArray()) {
                    groups.get(groups.size() - 1).getPlayers().add(((Element) playerNameTag).text());
                }
            }
            if (tagName.equals("span")) {
                Match match = parseMatch(element.text());
                if (match != null) {
                    groups.get(groups.size() - 1).getMatches().add(match);
                }
            }
        }
        return groups;
    }

}
