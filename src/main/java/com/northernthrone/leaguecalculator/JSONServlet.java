package com.northernthrone.leaguecalculator;

import com.google.gson.GsonBuilder;
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
        String standingsThreadURL = StandingsParser.parseLeagueBoard(document);
        if (standingsThreadURL == null) {
            return;
        }

        connection = Jsoup.connect(standingsThreadURL);
        try {
            document = connection.get();
        } catch (IOException e) {
            return;
        }
        List<Group> groups = StandingsParser.parseStandingsThread(document);

        String jsonOutput = new GsonBuilder().setPrettyPrinting().create().toJson(groups);
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
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(jsonOutput);
        }

    }
}
