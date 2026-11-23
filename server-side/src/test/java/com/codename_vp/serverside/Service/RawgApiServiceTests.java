package com.codename_vp.serverside.Service;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.codename_vp.serverside.Entity.OwnedList;
import com.codename_vp.serverside.Entity.WishList;
import com.codename_vp.serverside.Repository.UserRepo;

// Fakes the RAWG API, so these tests never call the real one.
// Uses seed user hijk123 and made-up game ids, since the database is shared between tests.
@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-test-secret-test-secret-1234",
        "RAWG_API_KEY=test-key"
})
@AutoConfigureMockMvc
class RawgApiServiceTests {

    @Autowired
    private RawgApiService rawgApiService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private OwnedListService ownedListService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MockMvc mockMvc;

    private ClientHttpRequestFactory realRequestFactory;
    private MockRestServiceServer rawg;
    private Long userId;

    @BeforeEach
    void fakeRawg() {
        realRequestFactory = restTemplate.getRequestFactory();
        rawg = MockRestServiceServer.bindTo(restTemplate).build();
        userId = userRepo.findByUserName("hijk123").orElseThrow().getId();
    }

    @AfterEach
    void restoreRawg() {
        rawg.verify();
        restTemplate.setRequestFactory(realRequestFactory);
    }

    private void expectGameDetails(long gameId, int calls) {
        String json = """
                {"id": %d, "name": "Test Game %d", "slug": "test-game-%d", "website": "https://example.com",
                 "description": "A test game", "released": "2024-01-01", "background_image": "https://example.com/img.jpg",
                 "platforms": [{"platform": {"id": 4, "name": "PC", "slug": "pc"}},
                               {"platform": {"id": 187, "name": "PlayStation 5", "slug": "playstation5"}}]}
                """.formatted(gameId, gameId, gameId);
        rawg.expect(times(calls), requestTo(containsString("/games/" + gameId + "?key=test-key")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    @Test
    void apiKeyIsRemovedFromResponses() {
        rawg.expect(requestTo(containsString("key=test-key")))
                .andRespond(withSuccess("""
                        {"next": "https://api.rawg.io/api/games?key=test-key&page=2", "results": []}
                        """, MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = rawgApiService.getTop10Games();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().contains("test-key"));
    }

    @Test
    void searchTextIsEncoded() {
        rawg.expect(requestTo(containsString("search=zelda%20%26%20link")))
                .andRespond(withSuccess("{\"results\": []}", MediaType.APPLICATION_JSON));

        rawgApiService.searchGameByName("zelda & link");
    }

    @Test
    void searchTextCannotAddQueryParameters() {
        rawg.expect(requestTo(not(containsString("&page_size=40"))))
                .andRespond(withSuccess("{\"results\": []}", MediaType.APPLICATION_JSON));

        rawgApiService.searchGameByName("zelda&page_size=40");
    }

    @Test
    void addToWishListSavesGameAndPlatforms() {
        expectGameDetails(990001, 1);

        WishList wishList = rawgApiService.addToWishList(990001L, userId);

        assertNotNull(wishList);
        assertEquals("Test Game 990001", wishList.getGame().getName());
        assertEquals("Wish", wishList.getGame().getStatus());
        assertEquals(2, wishList.getGame().getPlatforms().size());
        assertTrue(wishListService.isGameInWishList(userId, 990001L));
    }

    @Test
    void sameGameCannotBeWishedTwice() {
        expectGameDetails(990002, 2);

        assertNotNull(rawgApiService.addToWishList(990002L, userId));
        assertNull(rawgApiService.addToWishList(990002L, userId));
    }

    @Test
    void buyingAWishedGameMovesItToOwned() {
        expectGameDetails(990003, 2);

        rawgApiService.addToWishList(990003L, userId);
        OwnedList owned = rawgApiService.addToOwnedList(990003L, userId);

        assertNotNull(owned);
        assertTrue(ownedListService.isGameInOwnedList(userId, 990003L));
        assertFalse(wishListService.isGameInWishList(userId, 990003L));
    }

    @Test
    void ownedGameCannotBeWished() {
        expectGameDetails(990004, 2);

        assertNotNull(rawgApiService.addToOwnedList(990004L, userId));
        assertNull(rawgApiService.addToWishList(990004L, userId));
    }

    @Test
    void missingGameAtRawgReturns404() throws Exception {
        rawg.expect(requestTo(containsString("/games/990005?key=test-key")))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/game/detail/990005"))
                .andExpect(status().isNotFound());
    }
}
