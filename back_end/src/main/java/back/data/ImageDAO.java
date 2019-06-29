package back.data;

import back.exceptions.JTHDataBaseException;
import back.model.Image;

import java.util.List;

public interface ImageDAO {

    Image getById(long imgId) throws JTHDataBaseException;

    List<Long> getRoomImageIds(long roomId) throws JTHDataBaseException;
}
