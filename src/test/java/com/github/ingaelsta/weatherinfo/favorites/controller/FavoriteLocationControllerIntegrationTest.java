package com.github.ingaelsta.weatherinfo.favorites.controller;

import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.favorites.service.FavoriteLocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteLocationController.class)
class FavoriteLocationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteLocationService favoriteLocationServiceMock;

    private static final Double latitude = 55.87;
    private static final Double longitude = 26.52;
    private static final String locationName = "Daugavpils";


    private final FavoriteLocation location1 = new FavoriteLocation(latitude, longitude, locationName);
    private final FavoriteLocation location2 = new FavoriteLocation(-63.81,-57.69,"Watching Antarctic birds");

    private static final String URL = "/api/v1/outdoor-planner/favorites";

    private static class TestException extends RuntimeException {
        TestException(String message) {
            super(message);
        }
    }

    //post
    @Test
    public void When_ValidParameters_Then_saveFavoriteReturnsSavedEntity() throws Exception {

        String requestBody =
                (String.format("{\"latitude\": %s,\"longitude\": %s,\"locationName\":\"%s\"}",
                        latitude, longitude, locationName));

        when(favoriteLocationServiceMock.saveFavorite(location1))
                .thenReturn(location1);

        this.mockMvc
                .perform(post(URL)
                        .content(requestBody)
                        .header("Content-Type", "application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(locationName)));
    }

    @Test
    public void When_InvalidCoordinates_Then_saveFavoriteReturnsBadRequest()  throws Exception{

        String requestBody =
                (String.format("{\"latitude\": %s,\"longitude\": %s,\"locationName\":\"%s\"}",
                        latitude, 555, locationName));

        this.mockMvc
                .perform(post(URL)
                        .content(requestBody)
                        .header("Content-Type", "application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Longitude must be less")));
    }

    //get
    @Test
    public void When_NonemptyFavoriteListRetrieved_Then_getAllFavoritesReturnsListOfFavorites() throws Exception {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();
        favoriteLocations.add(location1);
        favoriteLocations.add(location2);

        when(favoriteLocationServiceMock.getAllFavorites())
                .thenReturn(favoriteLocations);

        this.mockMvc
                .perform(get((URL)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(latitude.toString())));
    }

    @Test
    public void When_EmptyFavoriteListRetrieved_Then_getAllFavoritesReturnsEmptyList () throws Exception {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();

        when(favoriteLocationServiceMock.getAllFavorites())
                .thenReturn(favoriteLocations);

        this.mockMvc
                .perform(get((URL)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    public void When_ServiceReturnsUnexpectedException_Then_getAllFavoritesReturnsServerErrorWithNoExplicitDetails () throws Exception {

        when(favoriteLocationServiceMock.getAllFavorites())
                .thenThrow(new TestException("placeholder") {});

        this.mockMvc
                .perform(get((URL)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(not(containsString("placeholder"))))
                .andExpect(content().string(containsString("A server error has occurred")));
    }

    //delete
    @Test
    public void When_IdPassed_Then_deleteActivityByIdCallsService() throws Exception {
        doNothing().when(favoriteLocationServiceMock).deleteFavoriteById(1L);

        this.mockMvc
                .perform(delete((String.format("%s?id=1", URL))))
                .andDo(print())
                .andExpect(status().isOk());

        verify(favoriteLocationServiceMock).deleteFavoriteById(1L);
    }

    @Test
    public void When_NoIdPassed_Then_deleteByIdReturnsBadRequest() throws Exception {
        this.mockMvc
                .perform(delete((URL)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("is not present")));
    }
}