package swg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cekis on 2/18/2017.
 */
public class WSNode {
    private int _id;
    private int _parentId;
    private int _nodeIndex;
    private int _objIndex;
    private float _objX;
    private float _objY;
    private float _objZ;
    private float _objW;
    private float _objScale;
    private float _x;
    private float _y;
    private float _z;
    private float _type;
    private int _POBCRC;
    private WSNode _parent;
    private List<WSNode> _childNodes;
    private String template;

    public WSNode(){}

    public WSNode(int id, int parentId, int objIndex, byte ox, byte oy, byte oz, byte ow, byte scale, byte x, byte y, byte z, byte type, int unknown){
        this._id = id;
        this._parentId = parentId;
        this._objIndex = objIndex;
        this._objX = ox;
        this._objY = oy;
        this._objZ = oz;
        this._objW = ow;
        this._objScale = scale;
        this._type = type;
        this._x = x;
        this._y = y;
        this._z = z;
        this._POBCRC = unknown;
    }

    public int getId(){
        return _id;
    }
    public void setId(int id){
        _id = id;
    }

    public int getParentId() {
        return _parentId;
    }

    public void setParentId(int _parentId) {
        this._parentId = _parentId;
    }

    public int getObjIndex() {
        return _objIndex;
    }

    public void setObjIndex(int _oIndex) {
        this._objIndex = _oIndex;
    }

    public float getObjX() {
        return _objX;
    }

    public void setObjX(float _oX) {
        this._objX = _oX;
    }

    public float getObjY() {
        return _objY;
    }

    public void setObjY(float _objY) {
        this._objY = _objY;
    }

    public float getObjZ() {
        return _objZ;
    }

    public void setObjZ(float _objZ) {
        this._objZ = _objZ;
    }

    public float getObjScale() {
        return _objScale;
    }

    public void setObjScale(float _objScale) {
        this._objScale = _objScale;
    }

    public float getX() {
        return _x;
    }

    public void setX(float _x) {
        this._x = _x;
    }

    public float getY() {
        return _y;
    }

    public void setY(float _y) {
        this._y = _y;
    }

    public float getZ() {
        return _z;
    }

    public void setZ(float _z) {
        this._z = _z;
    }

    public float getObjW() {
        return _objW;
    }

    public void setObjW(float _objW) {
        this._objW = _objW;
    }

    public float getType() {
        return _type;
    }

    public void setType(float _type) {
        this._type = _type;
    }

    public int getPOBCRC() {
        return _POBCRC;
    }

    public void setPOBCRC(int _POBCRC) {
        this._POBCRC = _POBCRC;
    }

    public WSNode getParent() {
        return _parent;
    }

    public void setParent(WSNode _parent) {
        this._parent = _parent;
    }

    public List<WSNode> getNodes() {
        if(_childNodes == null){
            _childNodes = new ArrayList<>();
        }
        return _childNodes;
    }

    public void setNodes(List<WSNode> _nodes) {
        this._childNodes = _nodes;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getNodeIndex() {
        return _nodeIndex;
    }

    public void setNodeIndex(int _nodeIndex) {
        this._nodeIndex = _nodeIndex;
    }

    public boolean hasChildNodes(){
        return getNodes().size() > 0;
    }

    public String serialize(int index, boolean relative){
        String outLine = "";
        outLine += getId() + "\t";
        outLine += getParentId() + "\t";
        outLine += getTemplate() + "\t";
        outLine += index + "\t";
        outLine += (relative ? ((getX() + 8192) % 2048) : getX()) + "\t";
        outLine += getY() + "\t";
        outLine += (relative ? ((getZ() + 8192) % 2048) : getZ()) + "\t";
        outLine += getObjW() + "\t";
        outLine += getObjX() + "\t";
        outLine += getObjY() + "\t";
        outLine += getObjZ() + "\t";
        outLine += "\t";
        outLine += (getPOBCRC() != 0 ? "portalProperty.crc|0|" + getPOBCRC() + "|" : "");
        outLine += "$|\n";

        if(hasChildNodes()){
            int nSize = _childNodes.size();
            for(int j = 0; j < nSize; j++){
                WSNode node = _childNodes.get(j);
                outLine += node.serialize(j + 1, false);
            }
        }

        return outLine;
    }

    public List<WSNode> getAllNodes(){
        List<WSNode> tmp = new ArrayList<>();
        int i = 1;
        for (WSNode node : _childNodes) {
            node.setNodeIndex(i++);
            tmp.add(node);
            if(node.hasChildNodes()){
                tmp.addAll(node.getAllNodes());
            }
        }
        return tmp;
    }
}
