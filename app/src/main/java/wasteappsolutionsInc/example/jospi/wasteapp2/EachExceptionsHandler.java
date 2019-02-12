package wasteappsolutionsInc.example.jospi.wasteapp2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public interface EachExceptionsHandler {
    void handleIOException(IOException e);
    void handleMalformedURLException(MalformedURLException e);
    void handleProtocolException(ProtocolException e);
    void handleUnsupportedEncodingException(UnsupportedEncodingException e);
}
