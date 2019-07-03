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
    public String getRandomImageUrl() throws JTHDataBaseException{
        return dataAccess.getRandomImageUrl();
    }

    @Override
    public List<Long> getRoomImageIds(int roomId) throws JTHDataBaseException {
        return dataAccess.getRoomImageIds(roomId);
    }

    @Override
    public int getRoomIdForImage(long imgId) throws JTHDataBaseException {
        return dataAccess.getRoomIdForImage(imgId);
    }

    @Override
    public void addImage(int roomId, String url) throws JTHDataBaseException {
        dataAccess.addImage(roomId, url);
    }

    @Override
    public void deleteImage(long imgId) throws JTHDataBaseException {
        dataAccess.deleteImage(imgId);
    }
}
