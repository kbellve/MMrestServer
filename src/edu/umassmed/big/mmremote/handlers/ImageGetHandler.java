package edu.umassmed.big.mmremote.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import edu.umassmed.big.mmremote.mmKNIME;
import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.imageio.ImageIO;


/**
 *  Handle GET (retrieval) requests. These should never modify anything, only
 *  provide information. This abstract base class should be extended by 
 *  classes that provide retrieval functionality.
 * 
 *  @author Matthijs
 */
public class ImageGetHandler implements HttpHandler  {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    	mmKNIME.core.logMessage("µmKNIME: Handling Get Image request.");        
       
        ImagePlus imagePlus   = mmKNIME.si.getSnapLiveManager().getDisplay().getImagePlus();
        
       	BufferedImage image         = imagePlus.getBufferedImage();
        
     // Encode image into desired encoding, write it to byte-stream.
        ByteArrayOutputStream out   = new ByteArrayOutputStream(1000);
        ImageIO.write(image, "PNG", out);
        out.flush();
        
        exchange.getResponseHeaders().set("Content-Type", "image/png");       
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, out.size());
        
        OutputStream os = exchange.getResponseBody();
        os.write(out.toByteArray());
        os.flush();
        
        out.close();
        os.close();
        exchange.close();
        
    }
}