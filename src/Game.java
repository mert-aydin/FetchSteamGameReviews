import java.util.ArrayList;

class Game {

    private String gameId;
    private ArrayList<String> reviewsArrayList;

    Game(String gameId, ArrayList<String> reviewsArrayList) {
        this.gameId = gameId;
        this.reviewsArrayList = reviewsArrayList;
    }

    String getGameId() {
        return gameId;
    }

    ArrayList<String> getReviewsArrayList() {
        return reviewsArrayList;
    }

}
