package com.github.ingaelsta.weatherinfo.favorites.repository;

import com.github.ingaelsta.weatherinfo.favorites.entity.FavoriteLocation;
import org.springframework.data.repository.CrudRepository;

public interface FavoriteLocationRepository extends CrudRepository<FavoriteLocation, Long> {
}
