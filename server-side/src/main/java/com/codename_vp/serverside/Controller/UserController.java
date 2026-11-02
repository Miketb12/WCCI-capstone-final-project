package com.codename_vp.serverside.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Service.OwnedListService;
import com.codename_vp.serverside.Service.UserService;
import com.codename_vp.serverside.Service.WishListService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    WishListService wishListService;

    @Autowired
    OwnedListService ownedListService;

    @GetMapping("/user/id/{id}")
    public User getUserById(@PathVariable Long id) {
        return this.userService.getUserById(id);
    }

    @GetMapping("/user/username/{username}")
    public User getUserByUserName(@PathVariable String username) {
        return this.userService.getUserByUserName(username);
    }


    @PostMapping("/user/id/{userId}/add-game-to-wish-list/{gameId}")
    public User addGameToWishList(@PathVariable Long userId, @PathVariable Long gameId) {
        return userService.addGameToWishList(userId, gameId);
    }

    @DeleteMapping("/user/{userId}/wishlist/remove/{wishListId}")
    public User removeFromWishList(@PathVariable Long userId, @PathVariable Long wishListId) {
        return userService.removeFromWishList(userId, wishListId);
    }

    @DeleteMapping("/user/{userId}/ownedlist/remove/{ownedListId}")
    public User removeFromOwnedList(@PathVariable Long userId, @PathVariable Long ownedListId) {
        return userService.removeFromOwnedList(userId, ownedListId);
    }

    @PostMapping("/user/{userId}/moveToOwnedList/{wishListId}")
    public User moveFromWishListToOwnedList(@PathVariable Long userId, @PathVariable Long wishListId) {
        return userService.moveFromWishListToOwnedList(userId, wishListId);
    }

    @GetMapping("/user/check-in-lists/{userId}/{gameId}")
    public ResponseEntity<Map<String, Boolean>> checkGameInLists(
            @PathVariable Long gameId, @PathVariable Long userId) {
        boolean isInWishList = wishListService.isGameInWishList(userId, gameId);
        boolean isInOwnedList = ownedListService.isGameInOwnedList(userId, gameId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isInWishList", isInWishList);
        response.put("isInOwnedList", isInOwnedList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
