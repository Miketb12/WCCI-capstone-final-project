package com.codename_vp.serverside.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.codename_vp.serverside.Entity.OwnedList;
import com.codename_vp.serverside.Entity.WishList;
import com.codename_vp.serverside.Service.RawgApiService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class RawgApiController {

    @Autowired
    private final RawgApiService rawgService;

    public RawgApiController(RawgApiService rawgApiService) {
        this.rawgService = rawgApiService;
    }

    // Pass RAWG's 404 for a missing game on to the client instead of failing with a 500
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Void> handleRawgError(HttpClientErrorException e) {
        return ResponseEntity.status(e.getStatusCode()).build();
    }

    @GetMapping("/game/detail/{gameId}")
    public ResponseEntity<String> getGameDetailById(@PathVariable Long gameId) {

        return rawgService.getGameDetailById(gameId);
    }

    @GetMapping("/game/search")
    public ResponseEntity<String> searchGameByName(@RequestParam String query) {
        return rawgService.searchGameByName(query);
    }

    @GetMapping("/game/top_games")
    public ResponseEntity<String> getTopGames() {
        return rawgService.getTop10Games();
    }

    @GetMapping("/game/new_games")
    public ResponseEntity<String> getNewReleaseGames() {
        return rawgService.getNewReleaseGames();
    }

    @GetMapping("/game/video/{gameId}")
    public ResponseEntity<String> getGameVideo(@PathVariable int gameId) {
        return rawgService.getTrailerVideo(gameId);
    }

    @PostMapping("user/add-to-wish-list/{gameId}")
    public ResponseEntity<WishList> addToWishList(@PathVariable Long gameId, @RequestParam Long userId) {
        WishList wishList = rawgService.addToWishList(gameId, userId);
        if (wishList != null) {
            System.out.println("Added game to Wish List");
            return ResponseEntity.ok(wishList);

        } else {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response
        }

    }

    @PostMapping("/user/add-to-owned-list/{gameId}")
    public ResponseEntity<OwnedList> addToOwnList(@PathVariable Long gameId, @RequestParam Long userId) {
        OwnedList ownedList = rawgService.addToOwnedList(gameId, userId);
        if (ownedList != null) {
            System.out.println("Added game to Owned List");
            return ResponseEntity.ok(ownedList);

        } else {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response
        }

    }

}