package swg;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cekis on 2/18/2017.
 */
public class WSFile {
    private List<WSNode> _nodes;
    private String[] _types;
    private String _fileName;
    private String _areaName;

    public List<WSNode> getNodes() {
        if(_nodes == null){
            _nodes = new ArrayList<>();
        }
        return _nodes;
    }

    public void setNodes(List<WSNode> _nodes) {
        this._nodes = _nodes;
    }

    public String[] getTypes() {
        return _types;
    }

    public void setTypes(String[] _types) {
        this._types = _types;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        this._fileName = fileName;
    }

    public String getAreaName() {
        return _areaName;
    }

    public void setAreaName(String area) {
        this._areaName = area;
    }

    public void readFile(String filename){
        try{
            _areaName = filename.replaceAll("snapshot/","").replaceAll(".ws","");
            parseFile(new File(filename));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void parseFile(File file) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bi = null;
        try {
            byte[] FORM = new byte[4];
            byte[] WSNPFORM = new byte[8];
            byte[] t0001FORM = new byte[8];
            byte[] lengthBuff = new byte[4];
            byte[] OTNL = new byte[4];

            fis = new FileInputStream(file);
            bi = new BufferedInputStream(fis);
            bi.read(FORM);
            bi.read(new byte[4]);  // length of form
            bi.read(WSNPFORM);
            bi.read(new byte[4]);  // length of wsnpform
            bi.read(t0001FORM);
            bi.read(lengthBuff);  // length of t0001form

            int buffLength = ByteBuffer.wrap(lengthBuff).getInt();

            byte[] nodesBuffer = new byte[buffLength];

            bi.read(nodesBuffer);

            bi.read(OTNL); // OTNL
            bi.read(new byte[4]); // length of OTNL
            bi.read(new byte[4]); // count (string)

            List<String> listTypes = new ArrayList<>();
            String type = "";
            while(bi.available() > 0){
                byte[] ba = new byte[1];
                bi.read(ba);
                while(ba[0] != 0){
                    type += (char) ba[0];
                    bi.read(ba);
                }
                listTypes.add(type);
                type = "";
            }
            _types = listTypes.toArray(new String[listTypes.size()]);

            parseNodes(nodesBuffer);

            //System.out.println("objid\tcontainer\tserver_template_crc\tcell_index\tpx\tpy\tpz\tqw\tqx\tqy\tqz\tscripts\tobjvars");
            //System.out.println("i\ti\th\ti\tf\tf\tf\tf\tf\tf\tf\ts\tp");
            //printNodes(_nodes);
            //saveNodesToFiles(_nodes);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(bi != null){
                bi.close();
            }
            if(fis != null){
                fis.close();
            }
        }
    }
    private void parseNodes(byte[] data) throws IOException {
        int remaining = 0;
        byte[] FORM = new byte[4];
        byte[] formBuffer = new byte[4];
        byte[] NODEFORM = new byte[8];
        byte[] t0000DATA = new byte[8];
        byte[] value = new byte[4];
        WSNode tempNode = null;

        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));

        try {
            while (remaining < data.length) {
                // header data
                bis.read(FORM);
                bis.read(formBuffer);  // length
                if(WSFile.IsAlpha(formBuffer)){
                    bis.read(new byte[4]);
                    remaining += 4;
                }
                bis.read(NODEFORM);
                bis.read(new byte[4]);  // length
                bis.read(t0000DATA);
                bis.read(new byte[4]);  // length

                remaining += 32;  // total of header data

                // start reading actual node
                WSNode node = new WSNode();
                bis.read(value);
                node.setId(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt());
                bis.read(value);
                node.setParentId(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt());
                bis.read(value);
                node.setObjIndex(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt());
                node.setTemplate(getTypes()[node.getObjIndex()]);
                bis.read(value); // no idea what this value is supposed to be.
                bis.read(value);
                node.setObjW(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setObjX(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setObjY(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setObjZ(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setX(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setY(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setZ(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setType(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat());
                bis.read(value);
                node.setPOBCRC(ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt());

                // 13 * 4 is 13 values read * 4 bytes each.
                remaining += (13 * 4);

                // add the node to our list of nodes.
                if(node.getParentId() == 0) {
                    getNodes().add(node);
                }
                else{
                    tempNode = FindNodeById(node.getParentId());
                    if(tempNode == null){
                        throw new Exception("Parent Node " + node.getParentId() + " could not be found!");
                    }
                    tempNode.getNodes().add(node);
                }
            }
        }
        catch(Exception e){
            System.out.println("EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            bis.close();
        }
    }
    // IsAlpha identifies if this is the first node or not.  If so, we need to offset the read 4 bytes to make sure stuff lines up.
    private static boolean IsAlpha(byte[] data) {
        for (byte character : data) {
            if ((((character <= 0x2f) || (character >= 0x3a)) && ((character <= 0x40) || (character >= 0x5c))) && (character != 0x20)) {
                return false;
            }
        }
        return true;
    }
    private WSNode FindNodeById(int parentId){
        if(getNodes().size() > 0){
            return FindChildren(parentId, getNodes().toArray(new WSNode[getNodes().size()]));
        }
        return null;
    }
    private WSNode FindChildren(int id, WSNode[] nodes){
        for (WSNode node : nodes){
            if(node == null) continue;
            if(node.getId() == id){
                return node;
            }
            else{
                if(node.hasChildNodes()){
                    WSNode returnVal = FindChildren(id, node.getNodes().toArray(new WSNode[getNodes().size()]));
                    if(returnVal != null){
                        return returnVal;
                    }
                }
            }
        }
        return null;
    }
    private void saveNodesToFiles(List<WSNode> nodes) throws IOException {
        String area = getAreaName();
        String oldDir = "output/" + area;
        File rmDir = new File(oldDir);
        if(rmDir.exists()){
            System.out.print("Removing files from folder " + area + "... ");
            boolean result = false;
            for(File f : rmDir.listFiles()){
                result = f.delete();
                if(!result) break;
            }
            System.out.println((result ? "removed." : "not removed."));
            if(!result) System.exit(1);
            System.out.print("Removing folder " + area + "... ");
            System.out.println((rmDir.delete() ? "removed." : "not removed."));
        }
        boolean newFile = false;
        for (WSNode node : nodes) {
            int x = (int) Math.floor((double) (node.getX() + 8192) / 2048) + 1;
            int y = (int) Math.floor((double) (node.getZ() + 8192) / 2048) + 1;

            String fileName = "output/" + area + "/" + area + "_" + x + "_" + y + "_ws.tab";

            File outFile = new File(fileName);
            if(!outFile.exists()){
                outFile.getParentFile().mkdirs();
                newFile = true;
            }
            Writer writer;
            try {
                FileWriter fw = new FileWriter(outFile, true);
                if(newFile){
                    fw.write("objid\tcontainer\tserver_template_crc\tcell_index\tpx\tpy\tpz\tqw\tqx\tqy\tqz\tscripts\tobjvars\n");
                    fw.write("i\ti\th\ti\tf\tf\tf\tf\tf\tf\tf\ts\tp\n");
                }
                fw.write(node.serialize(0,true));
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            newFile = false;
        }
    }

    public List<WSNode> getAllNodes(){
        List<WSNode> tmp = new ArrayList<>();
        for (WSNode node : _nodes) {
            tmp.add(node);
            if(node.hasChildNodes()){
                tmp.addAll(node.getAllNodes());
            }
        }
        return tmp;
    }
}
