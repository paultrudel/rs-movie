package rsmovie.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rsmovie.dao.RsDAO;

public class JsonTest {

    public static void main(String args[]) {
        RsDAO rsDAO = RsDAO.getInstance();
        Review review = rsDAO.find("1", Review.class);
        try {
            String result = new ObjectMapper().writeValueAsString(review);
            System.out.println(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
