package az.code.Tour032.utils;

import az.code.Tour032.dtos.ImageDTO;
import gui.ava.html.image.generator.HtmlImageGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Utils {

    public static boolean regexForData(String text, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static String generateImageBasedOnText(ImageDTO imageDTO, String companyName) throws IOException {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\"><head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "<title>Document</title></head>\n" +
                "<body style=\"display: flex; justify-content: center;\">" +
                "<div style=\"width: 500px; background-color: rgb(152, 240, 140); padding-top: 20px; padding-left: 10px;padding-right: 10px;padding-bottom: 20px;\">\n" +
                "<h2 style='color: blue; text-align: center; margin-top: 10px; '>"+companyName+"</h2>"+
                "<h3 style=\"text-align: center;font-family: Arial, Helvetica, sans-serif;\">DATE-RANGE: "+imageDTO.getDateRange()+"</h3>\n" +
                "<h3 style=\"text-align: center;\">NOTE: "+imageDTO.getNotes() +"</h3>"+
                "<h3 style=\"text-align: center;\">DESCRIPTION: "+imageDTO.getDescription()+"</h3>"+
                "<h3 style=\"text-align: center;\">PLACE: "+imageDTO.getPlace()+"</h3>\n" +
                "<h3 style=\"text-align: center;\">PRICE:"+ imageDTO.getPrice()+" AZN</h3>\n" +
                "</div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        HtmlImageGenerator hig = new HtmlImageGenerator();
        hig.loadHtml(html);
        BufferedImage image = hig.getBufferedImage();
        Random r = new Random();
        int fileName = r.nextInt(50000);
        ImageIO.write(image, "png", new File("image/"+fileName+".png"));
        System.out.println(image.getWidth()+" width");
        return "image/"+fileName+".png";
    }

}
