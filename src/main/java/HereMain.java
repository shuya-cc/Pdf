import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.json.JSONObject;

public class HereMain {
    static final String TXT_FILE_PATH = "resource/herecategory.txt";
    static final String PDF_FILE_PATH = "resource/Places Extract v1.1.0 Category Guide.pdf";


    public static void main(String[] args) {
        try {
        String pdfPath = PDF_FILE_PATH;
        String textFilePath = TXT_FILE_PATH;
        ParsePdfUtil parsePdfUtil = new ParsePdfUtil();

        String content = parsePdfUtil.getTextFromPdf(pdfPath);
        parsePdfUtil.toTextFile(content, textFilePath);

        ParseTxtToJson temp = new ParseTxtToJson();
        temp.readFromTxt(TXT_FILE_PATH);
        System.out.println("parent size:" + temp.parentList.size());
        System.out.println("food type size:" + temp.foodTypeList.size());
        HereMain main = new HereMain();
        main.saveInMongoDB(temp.printAsJson(), Integer.parseInt(args[0]), args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveInMongoDB(String json, int port, String collectionName) {
        MongoClient mongoClient = new MongoClient("localhost", port);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("cloudcar");
        System.out.println("Connect to database successfully");
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        Document document = Document.parse(json);
        collection.insertOne(document);
    }


}
