package com.northernthrone.leaguecalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StandingsParser {

    public static class SeasonBoardAttrs {

        int seasonNumber;
        String boardURL;

        public SeasonBoardAttrs(int seasonNumber, String boardURL) {
            this.seasonNumber = seasonNumber;
            this.boardURL = boardURL;
        }
    }

    private static float fixResult(float input) {
        return new Float(Math.round(input * 4)) / 4;
    }

    private static Match parseMatch(String input) {
        Pattern pattern = Pattern.compile("(?<p1>.*?) - (?<p2>.*?): (?<score>[0-9]*[,\\.]?[0-9]*) - .*");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Match match = new Match();
            match.setP1(matcher.group("p1"));
            match.setP2(matcher.group("p2"));
            match.setActualResult(fixResult(Float.parseFloat(matcher.group("score").replace(',', '.'))));
            return match;
        } else {
            return null;
        }
    }

    public static SeasonBoardAttrs parseLeagueBoard(Document document) {
        String groupName = "SEASONNUMBER";
        String patternString = "Season (?<" + groupName + ">[0-9]+) - Standings";
        Elements elements = document.getElementsMatchingOwnText(patternString);
        if (elements.isEmpty()) {
            return null;
        }
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(elements.get(0).ownText());
        if (matcher.matches()) {
            return new SeasonBoardAttrs(Integer.parseInt(matcher.group(groupName)), elements.get(0).attr("href"));
        }
        return null;
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
                    groups.get(groups.size() - 1).getPlayers().add(new Player(((Element) playerNameTag).text()));
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
