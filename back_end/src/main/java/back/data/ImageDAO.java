package back.data;

import back.exceptions.JTHDataBaseException;
import back.model.Image;

import java.util.List;

public interface ImageDAO {

    Image getById(long imgId) throws JTHDataBaseException;

    String getRandomImageUrl(int roomId) throws JTHDataBaseException;

    List<Long> getRoomImageIds(int roomId) throws JTHDataBaseException;

    int getRoomIdForImage(long imgId) throws JTHDataBaseException;

    void addImage(int roomId, String url) throws JTHDataBaseException;

    void deleteImage(long imgId) throws JTHDataBaseException;

}
