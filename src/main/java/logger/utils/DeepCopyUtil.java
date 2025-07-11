package logger.utils;

import java.io.*;

public class DeepCopyUtil {

    public static <T> T deepCopy(T orignal) throws IOException, ClassNotFoundException {
        //serializing-  write object  into a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(orignal);
        oos.flush();

        //reading and recreating the object from byte array
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        T copy =  (T) ois.readObject();

        baos.close();
        oos.close();
        bais.close();
        ois.close();

        return copy;
    }
}
