package com.codename_vp.serverside;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.codename_vp.serverside.Entity.Game;
import com.codename_vp.serverside.Entity.OwnedList;
import com.codename_vp.serverside.Entity.Platform;
import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Entity.WishList;
import com.codename_vp.serverside.Repository.GameRepo;
import com.codename_vp.serverside.Repository.OwnedListRepo;
import com.codename_vp.serverside.Repository.UserRepo;
import com.codename_vp.serverside.Repository.WishListRepo;
import com.codename_vp.serverside.Service.GameService;
import com.codename_vp.serverside.Service.WishListService;

@Component
public class Populator implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GameRepo gameRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void run(String... args) throws Exception {

        Set<Platform> platforms = new HashSet<Platform>();
        List<User> users = new ArrayList<>();
        List<Game> games = new ArrayList<>();
        System.out.println("Hello Populator\n");

        User user1 = new User("abc123", passwordEncoder.encode("1234"), "abc123@gmail.com ");
        userRepo.save(user1);
        users.add(0, user1);

        User user2 = new User("def123", passwordEncoder.encode("1234"), "def123@gmail.com ");
        userRepo.save(user2);
        users.add(1, user2);

        User user3 = new User("hijk123", passwordEncoder.encode("1234"), "hijk123@gmail.com");
        userRepo.save(user3);
        users.add(2, user3);

        System.out.println("\n================== GAMES ==================\n");

        Game game1 = new Game(1L, "Assassin's Creed Mirage", null, null, null);
        gameRepo.save(game1);

        Game game2 = new Game(2L, "Witchfire", null, null, null);
        gameRepo.save(game2);
        System.out.println("\n================== Wish List ==================\n");

        for (User user : users) {
            System.out.println("Name: " + user.getUserName() +
                    " Email: " + user.getEmail()
                    + "\nOwned List: " + user.getOwnedLists() + "\nWish List: "
                    + user.getWishLists());

        }
    }
}
