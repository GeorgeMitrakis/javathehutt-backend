package back.data;

import back.data.jdbc.DataAccess;
import back.exceptions.JTHDataBaseException;
import back.data.ImageDAO;
import back.model.Image;

import java.util.List;

public class ImageDAOImpl implements ImageDAO {
    private final DataAccess dataAccess;

    public ImageDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public Image getById(long imgId) throws JTHDataBaseException {
        return dataAccess.getImageById(imgId);
    }

    @Override
    public List<Long> getRoomImageIds(long roomId) throws JTHDataBaseException {
        return dataAccess.getRoomImageIds(roomId);
    }
}
