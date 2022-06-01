package com.github.ingaelsta.weatherinfo.favorites.controller;

import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.favorites.service.FavoriteLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteLocationControllerUnitTest {

    private final FavoriteLocationService favoriteLocationServiceMock = Mockito.mock(FavoriteLocationService.class);
    private FavoriteLocationController favoriteLocationControllerMock;

    private final FavoriteLocation location1 = new FavoriteLocation(55.87, 26.52, "Daugavpils");
    private final FavoriteLocation location2 = new FavoriteLocation(-63.81,-57.69,"Watching Antarctic birds");


    @BeforeEach
    void setup() {
        favoriteLocationControllerMock = new FavoriteLocationController(favoriteLocationServiceMock);
        location1.setId(1L);
        location2.setId(2L);
    }

    @Test
    void When_SavingFavoriteSuccessful_Then_saveFavoriteReturnsSavedEntity() {
        when(favoriteLocationServiceMock.saveFavorite(location1))
                .thenReturn(location1);

        FavoriteLocation result = favoriteLocationControllerMock.saveFavorite(location1);

        assertEquals(location1, result);
    }

    @Test
    void When_NonemptyFavoriteListIsRetrieved_Then_getAllFavoritesReturnsListOfFavorites() {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();
        favoriteLocations.add(location1);
        favoriteLocations.add(location2);

        when(favoriteLocationServiceMock.getAllFavorites())
                .thenReturn(favoriteLocations);

        List<FavoriteLocation> result = favoriteLocationControllerMock.getAllFavorites();

        assertEquals(favoriteLocations, result);
    }

    @Test
    public void When_EmptyFavoriteListIsRetrieved_Then_getAllFavoritesReturnsEmptyList () {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();

        when(favoriteLocationServiceMock.getAllFavorites())
                .thenReturn(favoriteLocations);

        List<FavoriteLocation> result = favoriteLocationControllerMock.getAllFavorites();

        assertEquals(favoriteLocations, result);
    }

    @Test void When_IdIsLong_Then_deleteFavoriteByIdCallsFavoriteLocationService() {
        doNothing().when(favoriteLocationServiceMock).deleteFavoriteById(1L);
        favoriteLocationControllerMock.deleteFavoriteById(1L);
        verify(favoriteLocationServiceMock).deleteFavoriteById(1L);
    }

    @Test void When_IdIsNull_Then_deleteFavoriteByIdThrowsIllegalArgumentException() {
        doThrow(new IllegalArgumentException()).when(favoriteLocationServiceMock).deleteFavoriteById(null);

        assertThrows(IllegalArgumentException.class, () -> favoriteLocationServiceMock.deleteFavoriteById(null));
    }

}