package com.github.ingaelsta.weatherinfo.favorites.service;

import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.favorites.repository.FavoriteLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteLocationService {

    private final FavoriteLocationRepository favoriteLocationRepository;

    public FavoriteLocationService (FavoriteLocationRepository favoriteLocationRepository) {
        this.favoriteLocationRepository = favoriteLocationRepository;
    }

    public FavoriteLocation saveFavorite (FavoriteLocation location) {
        // todo: most likely favorite names should not be overlapping if used from a drop-down, need to add a check
        return favoriteLocationRepository.save(location);
    }

    public List<FavoriteLocation> getAllFavorites() {
        return (List<FavoriteLocation>) favoriteLocationRepository.findAll();
    }

    public void deleteFavoriteById(Long id) {
        favoriteLocationRepository.deleteById(id);
    }
}
