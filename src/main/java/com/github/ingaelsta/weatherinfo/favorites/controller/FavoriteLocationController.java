package com.github.ingaelsta.weatherinfo.favorites.controller;

import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import com.github.ingaelsta.weatherinfo.favorites.service.FavoriteLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Validated
@RequestMapping("api/v1/outdoor-planner/favorites")
public class FavoriteLocationController {

    private final FavoriteLocationService favoriteLocationService;

    @Autowired
    public FavoriteLocationController (FavoriteLocationService favoriteLocationService) {
        this.favoriteLocationService = favoriteLocationService;
    }

    @GetMapping
    public List<FavoriteLocation> getAllFavorites() {
        return favoriteLocationService.getAllFavorites();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public FavoriteLocation saveFavorite (
            @Validated
            @RequestBody
            FavoriteLocation favoriteLocation) {
        return favoriteLocationService.saveFavorite(favoriteLocation);
    }

    @DeleteMapping
    public void deleteFavoriteById (@RequestParam (value = "id") @NotNull Long id) {
        favoriteLocationService.deleteFavoriteById(id);
    }

}
