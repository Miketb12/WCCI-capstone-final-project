package com.codename_vp.serverside.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codename_vp.serverside.Entity.Game;
import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Entity.WishList;
import com.codename_vp.serverside.Service.GameService;
import com.codename_vp.serverside.Service.UserService;
import com.codename_vp.serverside.Service.WishListService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class WishListController {

    @Autowired
    private WishListService wishListService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    // Endpoint to get wishlist items for a specific user
    @GetMapping("/user/{userId}/wishlist")
    public ResponseEntity<List<WishList>> getWishListByUserId(@PathVariable Long userId) {
        List<WishList> wishList = wishListService.getWishListByUserId(userId);
        return ResponseEntity.ok(wishList);
    }

    @PostMapping("/wishlist/add/{userId}/{gameId}")
    public ResponseEntity<WishList> addToWishList(@PathVariable Long userId, @PathVariable Long gameId) {
        User user = userService.getUserById(userId);
        Game game = gameService.getGameById(gameId);

        if (user != null && game != null) {
            WishList wishList = wishListService.addToWishList(user, game);
            return ResponseEntity.ok(wishList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
