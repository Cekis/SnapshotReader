import swg.WSFile;

import java.io.File;

/**
 * Created by Cekis on 2/18/2017.
 */
public class main {
    public static void main(String[] args)
    {
        if(args != null){
            switch(args.length){
                case 0:
                    File f = new File("snapshot");
                    for(File file : f.listFiles()){
                        WSFile snapshot = new WSFile();
                        snapshot.readFile("snapshot/" + file.getName());
                    }
                    break;
                case 1:
                    WSFile snapshot = new WSFile();
                    snapshot.readFile(args[0]);
                    break;
                default:
                    break;
            }
        }
    }
}
