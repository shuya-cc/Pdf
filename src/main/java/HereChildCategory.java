import java.util.ArrayList;
import java.util.List;

public class HereChildCategory {
    private String code;
    private String child;
    private List<HereGrandChildCategory> grandChildList = new ArrayList<HereGrandChildCategory>();

    public HereChildCategory(String code, String child) {
        this.code = code;
        this.child = child;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChild() {
        return child;
    }

    public List<HereGrandChildCategory> getGrandChildList() {
        return grandChildList;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public void addGrandChild(HereGrandChildCategory grandChild) {
        grandChildList.add(grandChild);
    }





}
