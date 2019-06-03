package back.util;


import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImgFetch {
    public static ByteArrayRepresentation fetch(String urlstr) throws IOException {
        URL url = new URL(urlstr);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf))){
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        return new ByteArrayRepresentation(response, MediaType.IMAGE_JPEG) ;
    }
}
