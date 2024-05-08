package com.kiszka.pdffiller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

@Controller
public class MainController {
    static final int OFFSET_X = 26;
    static final int OFFSET_Y = 30;
    private final static String[] values = new String[80];
    private static PDType0Font font;
    private static PDDocument document;

    static{
        try {
            document = PDDocument.load(new File("src/main/resources/main_pdf.pdf"));
            font = PDType0Font.load(document, new File("src/main/resources/Cambria-Bold.ttf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<ByteArrayResource> uploadFile(@RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try{
            processCSV(file);
            byte[] pdfData = createPDF();
            ByteArrayResource resource = new ByteArrayResource(pdfData);
            return ResponseEntity.ok()
                    .contentLength(pdfData.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    private void processCSV(MultipartFile file) throws Exception{
        int idx = 0;
        Scanner scanner = new Scanner(file.getInputStream());
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] vals = line.split(";");
            for(String val : vals){
                values[idx] = val;
                idx++;
            }
        }
    }
    private byte[] createPDF() throws Exception{

        PDPage page = document.getPage(0);
        PDPageContentStream contentStream = new PDPageContentStream(document,page, PDPageContentStream.AppendMode.APPEND,true);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA,8);
        String text = values[0].replace("\uFEFF", "");
        contentStream.newLineAtOffset(138,338);
        contentStream.showText(text);
        int idx = 0;
        for (int i = 1; i < 80; i++) {
            text = values[i].replace("\uFEFF","");
            if(i % 10 == 0) {
                contentStream.newLineAtOffset(OFFSET_X-278, -OFFSET_Y);
                idx++;
            } else {
                if((i-idx*10)%7==0) {
                    contentStream.newLineAtOffset(OFFSET_X+7,0);
                } else if((i-idx*10)%9==0){
                    contentStream.newLineAtOffset(OFFSET_X+5,0);
                }
                else if((i-idx*10)%8==0){
                    contentStream.newLineAtOffset(OFFSET_X+6,0);
                }else {
                    contentStream.newLineAtOffset(OFFSET_X,0);
                }
            }
            contentStream.showText(text);
        }
        contentStream.endText();
        contentStream.close();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        document.save(output);
        return output.toByteArray();
    }
    @GetMapping("/home")
    public String getHome(){
        return "home";
    }
}
