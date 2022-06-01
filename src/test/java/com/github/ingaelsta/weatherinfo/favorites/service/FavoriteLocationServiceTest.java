package com.github.ingaelsta.weatherinfo.favorites.service;

import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.favorites.repository.FavoriteLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteLocationServiceTest {

    private final FavoriteLocationRepository favoriteLocationRepositoryMock = Mockito.mock(FavoriteLocationRepository.class);
    private FavoriteLocationService favoriteLocationServiceMock;

    private final FavoriteLocation location1 = new FavoriteLocation(55.87, 26.52, "Daugavpils");
    private final FavoriteLocation location2 = new FavoriteLocation(-63.81,-57.69,"Watching Antarctic birds");

    @BeforeEach
    void setUp() {
        favoriteLocationServiceMock = new FavoriteLocationService(favoriteLocationRepositoryMock);
        location1.setId(1L);
        location2.setId(2L);
    }

    @Test
    void whenSavingFavorite_thenReturnSavedEntity() {
        when(favoriteLocationRepositoryMock.save(location1))
                .thenReturn(location1);

        FavoriteLocation result = favoriteLocationServiceMock.saveFavorite(location1);

        assertEquals(location1, result);
    }

    @Test
    void WhenRetrievingAllSavedFavorites_thenReturnsListOfFavorites() {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();
        favoriteLocations.add(location1);
        favoriteLocations.add(location2);

        when(favoriteLocationRepositoryMock.findAll())
                .thenReturn(favoriteLocations);

        List<FavoriteLocation> result = favoriteLocationServiceMock.getAllFavorites();

        assertEquals(favoriteLocations, result);
    }

    @Test
    public void WhenNoFavoritesSavedAndRetrievingAllSavedFavorites_thenReturnsEmptyList () {
        List<FavoriteLocation> favoriteLocations = new ArrayList<>();

        when(favoriteLocationRepositoryMock.findAll())
                .thenReturn(favoriteLocations);

        List<FavoriteLocation> result = favoriteLocationServiceMock.getAllFavorites();

        assertEquals(favoriteLocations, result);
    }

    @Test void WhenAttemptingToDeleteActivityById_thenNoExceptionIsThrown() {
        doNothing().when(favoriteLocationRepositoryMock).deleteById(1L);
        favoriteLocationServiceMock.deleteFavoriteById(1L);
        verify(favoriteLocationRepositoryMock).deleteById(1L);
    }

    @Test void WhenAttemptingToDeleteActivityWithoutPassingId_thenIllegalArgumentExceptionIsThrown() {
        doThrow(new IllegalArgumentException()).when(favoriteLocationRepositoryMock).deleteById(null);
        favoriteLocationServiceMock.deleteFavoriteById(1L);
        verify(favoriteLocationRepositoryMock).deleteById(1L);

        assertThrows(IllegalArgumentException.class, () -> favoriteLocationServiceMock.deleteFavoriteById(null));
    }
}