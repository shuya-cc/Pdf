import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class ParseTxtToJson {
    static final String FOOD_TYPES_SPLIT = "Chapter 3: Food Types.......";
    static final String CATEGORY_SYSTEM_SPLIT = "►  Places Category System";
    static final String RESULT_FILE_PATH = "D:/HereCategoryResult.txt";
    static final String RESULT_JSON_PATH = "D:/HereCategoryResult.json";


    List<HereParentCategory> parentList = new ArrayList<HereParentCategory>();
    List<HereChildCategory> foodTypeList = new ArrayList<HereChildCategory>();

    public void printToFile() {
        FileOutputStream fos = null;
        BufferedWriter bw = null;

        try {
            File file = new File(RESULT_FILE_PATH);
            if(!file.exists()) {
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos));

            for(HereParentCategory parent : parentList) {

                for(HereChildCategory child : parent.getChildList()) {

                    for(HereGrandChildCategory grandChild : child.getGrandChildList()) {
                        String parentCtg = parent.getParent();
                        String childCtg = child.getChild();
                        bw.write(appendStrLength(parentCtg));
                        bw.write(appendStrLength(childCtg));
                        bw.write(appendStrLength(grandChild.getGrandChild()));
                        bw.write(appendStrLength(grandChild.getCode()));
                        bw.newLine();
                    }
                }
            }
            System.out.println("write foodTypeList begin......");
            for(HereChildCategory child : foodTypeList) {
                for(HereGrandChildCategory grandChild : child.getGrandChildList()) {
                    String childCtg = child.getChild();
                    bw.write(appendStrLength(""));
                    bw.write(appendStrLength(childCtg));
                    bw.write(appendStrLength(grandChild.getGrandChild()));
                    bw.write(appendStrLength(grandChild.getCode()));
                    bw.newLine();
                }
            }
            System.out.println("write foodTypeList end......");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String printAsJson() {
        try {
            File file = new File(RESULT_JSON_PATH);
            if(!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("provider", "Here");
            JSONArray jsonArray = new JSONArray();
            for(HereParentCategory parent : parentList) {
                JSONObject parentObj = new JSONObject();
                parentObj.put("icon", "");
                parentObj.put("id", parent.getParent());
                parentObj.put("name", parent.getParent());
                parentObj.put("parent", new JSONArray());
                parentObj.put("provider", "here");
                jsonArray.put(parentObj);
            }


            for(HereParentCategory parent : parentList) {
                for(HereChildCategory child : parent.getChildList()) {
                    JSONObject object = new JSONObject();
                    JSONArray array = new JSONArray();
                    array.put(parent.getParent());
                    object.put("icon", "");
                    object.put("id", child.getChild());
                    object.put("name", child.getChild());
                    object.put("parent", array);
                    object.put("provider", "here");
                    jsonArray.put(object);
                }
            }

            for(HereParentCategory parent : parentList) {

                for(HereChildCategory child : parent.getChildList()) {

                    for(HereGrandChildCategory grandChild : child.getGrandChildList()) {
                        String parentCtg = parent.getParent();
                        String childCtg = child.getChild();
                        JSONObject object = new JSONObject();
                        object.put("icon", "");
                        object.put("id", grandChild.getGrandChild());
                        object.put("name", grandChild.getGrandChild());
                        object.put("code", grandChild.getCode());
                        JSONArray array = new JSONArray();
                        array.put(child.getChild());
                        object.put("parent", array);
                        object.put("provider", "here");

                        jsonArray.put(object);
                    }
                }
            }

            for(HereChildCategory child : foodTypeList) {
                for(HereGrandChildCategory grandChild : child.getGrandChildList()) {
                    String childCtg = child.getChild();
                    JSONObject object = new JSONObject();
                    object.put("code", grandChild.getCode());
                    object.put("parent", childCtg);
                    String grandName = grandChild.getGrandChild();
                    int index = -1;
                    if(grandName.contains("-")) {
                        index = grandName.indexOf(" ");
                    }else {
                        index = grandName.indexOf(" Food");
                    }
                    if(index > 0) {
                        object.put("child", grandName.substring(0, index));
                    }else {
                        object.put("child", grandName);
                    }


//                    object.put("parent", "");

                    jsonArray.put(object);
                }
            }

            jsonObject.put("categories", jsonArray);

            fos.write(jsonObject.toString().getBytes("UTF-8"));
            fos.flush();
            fos.close();
            return jsonObject.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String appendStrLength(String str) {
        int remain = 40 - str.length();
        for(int i=0; i< remain; i++) {
            str = str + " ";
        }

        return str;
    }

    public void readFromTxt(String pathName) {
        String str = "";
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(pathName)),"UTF-8");
            BufferedReader bf = new BufferedReader(reader);
            boolean afterFoodType = false;
            boolean isAfterCategory = false;
            while ((str = bf.readLine()) != null){

                if(str.contains(FOOD_TYPES_SPLIT)) {
                    afterFoodType = true;
                }
                if(str.contains(CATEGORY_SYSTEM_SPLIT)) {
                    isAfterCategory = true;
                }
                if(str.length() < 5 /*|| !isNumeric(str)*/) {
                    continue;
                }

                readFromSingleLine(str, afterFoodType, isAfterCategory);
            }

            bf.close();
            reader.close();




        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("line:" + str + " is error, please check");
        }
    }

    public void readFromSingleLine(String line, boolean isAfterFoodType, boolean isAfterCategory) {
        int ctgIdx = line.indexOf(" ");
        int endIdx = line.indexOf("...");

        if(endIdx > 0) {
            if(!isAfterFoodType) {
                if(ctgIdx == 3) {
                    String parentCode = line.substring(0, 3);
                    String parentCtg = line.substring(6, endIdx);
                    HereParentCategory parent = getExistParent(parentCode);
                    if(parent == null) {
                        parent = new HereParentCategory(parentCode, parentCtg);
                        parentList.add(parent);
                    }
                }
                if(ctgIdx == 4) {
                    String childCode = line.substring(0, 4);
                    String childCtg = line.substring(7, endIdx);
                    HereParentCategory parent = getExistParentByChildCode(childCode);
                    if(parent == null) {
                        throw new RuntimeException("childCode:"+ childCode + " to find parent not exist");
                    }
                    HereChildCategory child = new HereChildCategory(childCode, childCtg);
                    parent.addChild(child);
                }
            }else {
//            int endIdx = line.indexOf("...");
                String childCode = line.substring(0, 3);
                String childCtg = line.substring(6, endIdx);
                HereChildCategory category = new HereChildCategory(childCode, childCtg);
                foodTypeList.add(category);
            }
        }else {
            if(line.length() <= 14 || line.contains("Updated") || !isAfterCategory) {
                return;
            }
            String grandChildPattern = "\\d{3}\\-\\d{4}\\-\\d{4}";
            String foodTypePattern = "\\d{3}\\-\\d{3}";

            String grand = line.substring(0, 13);
            String food = line.substring(0, 7);
            Pattern grandPattern = Pattern.compile(grandChildPattern);
            Matcher grandMatcher = grandPattern.matcher(grand);
            Pattern foodPattern = Pattern.compile(foodTypePattern);
            Matcher foodMatcher = foodPattern.matcher(food);



            if(grandMatcher.matches()) {

                int spaceIdx = line.indexOf(" A ");
                if(spaceIdx < 0) {
                    spaceIdx = line.indexOf(" An ");
                    if(spaceIdx < 0) {
                        spaceIdx = 0;
                        int preIdx = 0;
                        for(int i=0; i<3;i++) {
                            spaceIdx = line.indexOf(" ", spaceIdx + 1);
                            if(spaceIdx < 0) {
                                spaceIdx = preIdx;
                                break;
                            }
                            preIdx = spaceIdx;
                        }
                    }
                }
                String ctg = line.substring(14, spaceIdx);
                HereGrandChildCategory grandChildCtg = new HereGrandChildCategory(grand, ctg);
                String parentCode = line.substring(0, 3);
                String childCode = line.substring(4, 8);
                HereParentCategory parentCtg = getExistParent(parentCode);
                if(parentCtg != null) {
                    HereChildCategory childCtg = parentCtg.getChildCtg(childCode);
                    if(childCtg != null) {
                        childCtg.addGrandChild(grandChildCtg);
                    }
                }


            }else if(foodMatcher.matches()) {
                int spaceIdx = 0;
                for(int i=0; i<3;i++) {
                    spaceIdx = line.indexOf(" ", spaceIdx + 1);
                }
                String ctg = line.substring(8, spaceIdx);
                HereGrandChildCategory grandChildCtg = new HereGrandChildCategory(food, ctg);
                String childCode = line.substring(0, 3);
                HereChildCategory childCtg = getExistFoodCtg(childCode);
                if(childCtg != null) {
                    childCtg.addGrandChild(grandChildCtg);
                }
            }else {
                int _4index = line.indexOf("4");
                if(_4index < 0) {
                    return;
                }
                if(_4index + 7 > line.length()) {
                    return;
                }
                String code = line.substring(_4index, _4index + 7);
                String codeFalse = line.substring(_4index, _4index + 8);
                String regex = "\\d{3}\\-\\d{3}";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(code);

                String falseRegex = "\\d{3}\\-\\d{4}";
                Pattern patternFalse = Pattern.compile(falseRegex);
                Matcher matcherFalse = patternFalse.matcher(codeFalse);

                if(!matcher.matches() || matcherFalse.matches()) {
                    return;
                }

                String ctg = line.substring(0 , _4index - 1);
                HereGrandChildCategory grandChildCtg = new HereGrandChildCategory(code, ctg);
                String childCode = line.substring(_4index, _4index + 3);
                HereChildCategory childCtg = getExistFoodCtg(childCode);
                if(childCtg != null) {
                    childCtg.addGrandChild(grandChildCtg);
                }
            }

        }

    }

    public HereParentCategory getExistParent(String code) {
        for(HereParentCategory temp : parentList) {
            if(temp.getCode().equals(code)) {
                return temp;
            }
        }
        return null;
    }

    public HereParentCategory getExistParentByChildCode(String childCode) {

        for(HereParentCategory temp : parentList) {
            int endIdx = 1;
            if(childCode.startsWith("35") || childCode.startsWith("55")) {
                endIdx = 2;
            }
            if(temp.getCode().substring(0, endIdx).equals(childCode.substring(0, endIdx))) {
                return temp;
            }
        }
        return null;
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str.substring(0, 3));
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public HereChildCategory getExistFoodCtg(String code) {
        for(HereChildCategory temp : foodTypeList) {
            if(temp.getCode().substring(0, 1).equals(code.substring(0, 1))) {
                return temp;
            }
        }
        return null;
    }



}
