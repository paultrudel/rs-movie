package rsmovie.utility;

import lombok.Data;
import rsmovie.entity.Movie;

import java.util.List;

@Data
public class Recommendations {

    private List<Movie> community;
    private List<Movie> personal;
    private List<Movie> reviewed;
}
