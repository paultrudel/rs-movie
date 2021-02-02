package rsmovie.utility;

import lombok.Data;

@Data
public class Tuple<T, S> {

    private T firstElement;
    private S secondElement;
}
