package rsmovie.service;

import rsmovie.entity.User;
import rsmovie.utility.Recommendations;

public interface RecommendationsService {

    public Recommendations fetchRecommendations(User user);
}
