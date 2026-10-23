package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

/**
 * CSV mapper for {@link VideoGame}: platform, genre and age rating.
 */
public class VideoGameCsvMapper implements ProductCsvMapper {

    @Override
    public String typeCode() {
        return "VIDEOGAME";
    }

    @Override
    public boolean supports(Product product) {
        return product instanceof VideoGame;
    }

    @Override
    public String[] specificColumnNames() {
        return new String[] {"platform", "genre", "ageRating"};
    }

    @Override
    public String[] toSpecificColumns(Product product) {
        VideoGame vg = (VideoGame) product;
        return new String[] {vg.getPlatform(), vg.getGenre(), vg.getAgeRating()};
    }

    @Override
    public Product fromColumns(String id, String title, double price, int stock, String[] specific) {
        return new VideoGame(specific[0], specific[1], specific[2], id, title, price, stock);
    }
}
