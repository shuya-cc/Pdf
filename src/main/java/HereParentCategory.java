import java.util.ArrayList;
import java.util.List;

public class HereParentCategory {
    private String code;
    private String parent;
    private List<HereChildCategory> childList = new ArrayList<HereChildCategory>();

    public HereParentCategory(String code, String parent) {
        this.code = code;
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void addChild(HereChildCategory child) {
        childList.add(child);
    }

    public List<HereChildCategory> getChildList() {
        return childList;
    }

    public HereChildCategory getChildCtg(String code) {
        for(HereChildCategory temp : childList) {
            if(temp.getCode().equals(code)) {
                return temp;
            }
        }
        return null;
    }

}
