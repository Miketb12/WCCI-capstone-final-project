package com.codename_vp.serverside.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.codename_vp.serverside.Entity.Game;
import com.codename_vp.serverside.Entity.OwnedList;
import com.codename_vp.serverside.Entity.Platform;
import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Entity.WishList;
import com.codename_vp.serverside.Repository.GameRepo;
import com.codename_vp.serverside.Repository.PlatformRepo;
import com.codename_vp.serverside.Repository.WishListRepo;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class RawgApiService {
    private final String apiUrl = "https://api.rawg.io/api/";

    @Value("${rawg.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WishListRepo wishListRepo;

    @Autowired
    private PlatformRepo platformRepo;

    @Autowired
    private OwnedListService ownedListService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameRepo gameRepo;

    @Autowired
    private GameService gameService;

    // RAWG repeats the API key in its paging links, so strip it before responding
    private ResponseEntity<String> fetchFromRawg(String url, Object... uriVariables) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
        String body = response.getBody() == null ? "" : response.getBody().replace(apiKey, "");
        return ResponseEntity.status(response.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    public ResponseEntity<String> getGameDetailById(Long game_id) {

        String url = apiUrl + "/games/" + game_id + "?key=" + apiKey;

        return fetchFromRawg(url);
    }

    public ResponseEntity<String> searchGameByName(String query) {
        String url = apiUrl + "/games?key=" + apiKey + "&search={query}";
        return fetchFromRawg(url, query);
    }

    public ResponseEntity<String> getTop10Games() {
        String url = apiUrl + "/games?key=" + apiKey + "&ordering=-rating&page_size=10&metacritic=100";

        return fetchFromRawg(url);
    }

    public ResponseEntity<String> getTrailerVideo(int gameId) {
        String url = apiUrl + "/games/" + gameId + "/movies?key=" + apiKey;
        return fetchFromRawg(url);
    }

    public ResponseEntity<String> getNewReleaseGames() {
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.toString();

        // Construct the "dates" parameter with the start and end date
        String dateRange = "2023-09-01" + "," + formattedDate;

        // Construct the URL with the "dates" parameter
        String url = apiUrl + "/games?key=" + apiKey + "&ordering=-added&page_size=20&dates=" + dateRange;

        return fetchFromRawg(url);
    }

    public OwnedList addToOwnedList(Long gameId, Long userId) {
        ResponseEntity<String> response = getGameDetailById(gameId);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            if (ownedListService.isGameInOwnedList(userId, gameId)) {
                System.out.println("The game is already in your OwnedList.");
                return null; // You might want to return an error response here.
            } else if (wishListService.isGameInWishList(userId, gameId)) {
                Long wishListId = wishListService.getWishListIdByGame(userId, gameId);
                if (wishListId != null) {
                    Game game = gameService.getGameById(gameId);
                    User user = userService.getUserById(userId);
                    OwnedList ownedList = new OwnedList(user, game);

                    this.ownedListService.addToOwnedList(ownedList);
                    System.out.println("You added game " + game.getName() + " to owned list ");
                    user.getOwnedLists().add(ownedList);
                    System.out.println("The game is in your Owned List now");
                    userService.removeFromWishList(userId, wishListId);

                    return ownedList;

                }

            }
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                Game game = new Game();

                game.setId(jsonNode.get("id").asLong());
                game.setName(jsonNode.get("name").asString());
                game.setSlug(jsonNode.get("slug").asString());
                game.setStatus("Owned");
                game.setOfficialSite(jsonNode.get("website").asString());
                game.setDescription(jsonNode.get("description").asString());
                game.setReleased(jsonNode.get("released").asString());
                game.setImgUrl(jsonNode.get("background_image").asString());

                Set<Platform> platformSet = new HashSet<>();
                JsonNode platformsNode = jsonNode.get("platforms");
                if (platformsNode != null && platformsNode.isArray()) {
                    for (JsonNode platformInfo : platformsNode) {
                        JsonNode platformNode = platformInfo.get("platform");

                        if (platformNode != null) {
                            Platform platform = new Platform();
                            platform.setPlatformId(platformNode.get("id").asInt());
                            platform.setPlatformName(platformNode.get("name").asString());
                            platform.setSlug(platformNode.get("slug").asString());

                            platformSet.add(platform);
                            this.platformRepo.save(platform);

                        }
                    }
                }
                game.setPlatforms(platformSet);
                gameRepo.save(game);
                User user = userService.getUserById(userId);

                OwnedList ownedList = new OwnedList(user, game);

                this.ownedListService.addToOwnedList(ownedList);
                System.out.println("You added game " + game.getName() + " to owned list ");
                user.getOwnedLists().add(ownedList);
                System.out.println("The game is in your Owned List now");

                return ownedList;

            } catch (JacksonException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public WishList addToWishList(Long gameId, Long userId) {
        ResponseEntity<String> response = getGameDetailById(gameId);
        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            if (wishListService.isGameInWishList(userId, gameId)) {
                System.out.println("The game is already in your WishList.");
                return null; // You might want to return an error response here.
            } else if (ownedListService.isGameInOwnedList(userId, gameId)) {
                System.out.println("The game is already in your Owned List.");
                return null;
            }

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                Game game = new Game();

                game.setId(jsonNode.get("id").asLong());
                game.setName(jsonNode.get("name").asString());
                game.setSlug(jsonNode.get("slug").asString());
                game.setStatus("Wish");
                game.setOfficialSite(jsonNode.get("website").asString());
                game.setDescription(jsonNode.get("description").asString());
                game.setReleased(jsonNode.get("released").asString());
                game.setImgUrl(jsonNode.get("background_image").asString());

                // Add platforms to the wish list
                Set<Platform> platformSet = new HashSet<>();
                JsonNode platformsNode = jsonNode.get("platforms");
                if (platformsNode != null && platformsNode.isArray()) {
                    for (JsonNode platformInfo : platformsNode) {
                        JsonNode platformNode = platformInfo.get("platform");

                        if (platformNode != null) {
                            Platform platform = new Platform();
                            platform.setPlatformId(platformNode.get("id").asInt());
                            platform.setPlatformName(platformNode.get("name").asString());
                            platform.setSlug(platformNode.get("slug").asString());

                            platformSet.add(platform);
                            this.platformRepo.save(platform);
                        }
                    }
                }
                game.setPlatforms(platformSet);
                gameRepo.save(game);

                User user = userService.getUserById(userId);

                WishList wishList = new WishList(user, game);
                wishList.setUser(user);
                System.out.println("Add game to " + user.getUserName() + "'s wish list!");
                wishListRepo.save(wishList);
                user.getWishLists().add(wishList);

                return wishList;

            } catch (JacksonException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

}