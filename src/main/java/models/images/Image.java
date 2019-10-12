package models.images;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;

public class Image implements Externalizable {


    private static final long serialVersionUID = -4827971603953121306L;

    private File image;

    private URL url;

    public Image() {
    }

    public Image(File imageFolder, URL url, String id){

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
