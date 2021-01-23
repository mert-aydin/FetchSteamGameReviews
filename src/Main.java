import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        String[] aaaGameIds = {Parameters.KINGDOM_COME_DELIVERANCE,
                Parameters.ASSASSINS_CREED_ODYSSEY,
                Parameters.FAR_CRY_5,
                Parameters.ARTIFACT,
                Parameters.DARKSIDERS_III,
                Parameters.SHADOW_OF_THE_TOMB_RAIDER,
                Parameters.PATHFINDER_KINGMAKER,
                Parameters.PILLARS_OF_ETERNITY_II_DEADFIRE,
                Parameters.BATTLETECH,
                Parameters.MONSTER_HUNTER_WORLD};

        String[] indieGameIds = {Parameters.RIMWORLD,
                Parameters.DUSK,
                Parameters.DEAD_CELLS,
                Parameters.RETURN_OF_THE_OBRA_DINN,
                Parameters.GRIS,
                Parameters.JUST_SHAPES_BEATS,
                Parameters.THE_FOREST,
                Parameters.NEKOPARA_EXTRA,
                Parameters.EPIC_BATTLE_FANTASY_5,
                Parameters.ZUP_X};

        for (String aaaGameId : aaaGameIds) {
            printResults(new Game(aaaGameId, new ArrayList<>()), "aaa");
        }

        for (String indieGameId : indieGameIds) {
            printResults(new Game(indieGameId, new ArrayList<>()), "indie");
        }

    }

    private static void printResults(Game game, String gameType) throws IOException {

        for (int i = 0; i < Parameters.REVIEW_COUNT; i += 20) {
            try {
                game.getReviewsArrayList().addAll((reviewsInArrayList(fetchReviewsInJson(game.getGameId(), i))));
            } catch (IOException ignored) { }
        }

        Map<String, Integer> map = new HashMap<>();
        for (String s : game.getReviewsArrayList()) {
            Integer c = map.get(s);
            if (c == null)
                c = 0;
            c++;
            map.put(s, c);
        }

        map = MapUtil.sortByValue(map);

        List<String> lines = new ArrayList<>();
        for (int i = map.size() - 1; i >= 0; i--) {
            if (Integer.parseInt(map.values().toArray()[i].toString()) >= Parameters.FREQUENCY_THRESHOLD * Parameters.REVIEW_COUNT) {
                if (!map.keySet().toArray()[i].equals("") && !map.keySet().toArray()[i].equals("\n")) {
                    //System.out.println(map.keySet().toArray()[i] + ": " + map.values().toArray()[i]);
                    lines.add(map.keySet().toArray()[i] + ": " + map.values().toArray()[i]);
                }
            }
        }

        Path file = Paths.get(gameType + "_" + game.getGameId() + ".txt");
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    private static ArrayList<String> reviewsInArrayList(String reviews) {

        JSONObject object = new JSONObject(reviews);
        ArrayList<String> reviewsOfAGame = new ArrayList<>();
        String[] strArr;

        JSONArray arr = object.getJSONArray("reviews");
        for (int i = 0; i < arr.length(); i++) {
            strArr = arr.getJSONObject(i).getString("review").replaceAll("\\p{P}", "").replaceAll("\n", "").replace("☐", "").toLowerCase().split(" ");
            Collections.addAll(reviewsOfAGame, strArr);
        }

        return reviewsOfAGame;
    }

    private static String fetchReviewsInJson(String gameId, int startOffset) throws IOException {

        URLConnection connection = new URL("https://store.steampowered.com/appreviews/" + gameId + "?json=1&start_offset=" + startOffset).openConnection();
        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter("\\Z");
        String content = scanner.next();
        scanner.close();

        return content;
    }

}
