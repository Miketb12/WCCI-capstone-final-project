package com.codename_vp.serverside.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codename_vp.serverside.Entity.Game;
import com.codename_vp.serverside.Entity.OwnedList;
import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Entity.WishList;
import com.codename_vp.serverside.Service.GameService;
import com.codename_vp.serverside.Service.OwnedListService;
import com.codename_vp.serverside.Service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class OwnedListController {

    @Autowired
    OwnedListService ownedListService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;



    @PostMapping("/user/{userId}/ownedlist/add/{gameId}")
    public ResponseEntity<OwnedList> addToOwnedList(@PathVariable Long userId, @PathVariable Long gameId) {
        User user = userService.getUserById(userId);
        Game game = gameService.getGameById(gameId);

        if (user != null && game != null) {
            OwnedList ownedList = ownedListService.addToOwnedList(user, game);
            return ResponseEntity.ok(ownedList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/user/{userId}/ownedlist")
    public ResponseEntity<List<OwnedList>> getWishListByUserId(@PathVariable Long userId) {
        List<OwnedList> ownedList = ownedListService.getOwnedListByUserId(userId);
        return ResponseEntity.ok(ownedList);
    }

}
