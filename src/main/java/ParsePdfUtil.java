import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;

public class ParsePdfUtil {

    public String getTextFromPdf(String pdfPath) throws Exception{
        PDDocument document = PDDocument.load(new File(pdfPath));
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDFTextStripper tStripper = new PDFTextStripper();

        String pdfFileInText = tStripper.getText(document);
        return pdfFileInText;

    }


    public void toTextFile(String pdfContent, String filePath) {
        File f = new File(filePath);
        try {
            if(!f.exists()) {
                f.createNewFile();
            }
            System.out.println("While PDF Content to text file");
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            BufferedWriter output = new BufferedWriter(osw);
            output.write(pdfContent);
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
