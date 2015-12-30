package com.northernthrone.leaguecalculator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@WebServlet(urlPatterns = {"/JSONServlet"})
public class JSONServlet extends HttpServlet {

    private static final String leagueBoardURL = "http://forum.dominionstrategy.com/index.php?board=60.0";
    private static final String seasonsFileName = "/seasons.json";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection connection;
        Document document;

        connection = Jsoup.connect(leagueBoardURL);
        try {
            document = connection.get();
        } catch (IOException e) {
            return;
        }

        StandingsParser.SeasonBoardAttrs boardAttrs = StandingsParser.parseLeagueBoard(document);
        if (boardAttrs == null) {
            return;
        }
        connection = Jsoup.connect(boardAttrs.boardURL);
        try {
            document = connection.get();
        } catch (IOException e) {
            return;
        }
        List<Group> groups = StandingsParser.parseStandingsThread(document);
        List<Season> seasons;
        Season currentSeason = new Season(boardAttrs.seasonNumber, groups);
        try (BufferedReader br = new BufferedReader(new FileReader(seasonsFileName))) {
            seasons = new Gson().fromJson(br, new TypeToken<List<Season>>() {
            }.getType());
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
        if (seasons.get(0).getSeasonNumber() == currentSeason.getSeasonNumber()) {
            seasons.set(0, currentSeason);
            System.out.println("Replacing");
        } else {
            seasons.add(0, currentSeason);
            System.out.println("Adding");
        }
        String seasonsJson = new GsonBuilder().setPrettyPrinting().create().toJson(seasons);

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.println(seasonsJson);
        }

        //System.out.println(jsonOutput);
        /*
         for (Group group : groups) {
         System.out.println(group.getName());
         for (String player : group.getPlayers()) {
         System.out.println("\t" + player);
         }
         System.out.println("\t----------------------------------------");
         for (Match match : group.getMatches()) {
         System.out.println("\t" + match.getP1() + " " + match.getResult() + " - " + (6 - match.getResult()) + " " + match.getP2());
         }
         }
         */
    }
}
