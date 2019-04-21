/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esp;

import com.sun.deploy.util.SessionState.Client;
import java.awt.Button;
import java.awt.PageAttributes.MediaType;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import static java.sql.JDBCType.NULL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.text.html.parser.Entity;
import javax.xml.ws.Response;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.util.Arrays;
/**
 *
 * @author desktop
 */
public class FXMLDocumentController {
    
    @FXML
    private Label label;
    @FXML
    private TextArea TextArea;
    @FXML
    private TextField address;
    @FXML
    private WebView WebView;
    @FXML
    private TextField port;
    private String choosen;

    private void main() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

public class ZPLConveter {
    @FXML
    private int blackLimit = 380;
    @FXML
    private int total;
    @FXML
    private int widthBytes;
    @FXML
    private boolean compressHex = false;
    
    @FXML
    private Map<Integer, String> mapCode = new HashMap<Integer, String>();
    {
        mapCode.put(1, "G");
        mapCode.put(2, "H");
        mapCode.put(3, "I");
        mapCode.put(4, "J");
        mapCode.put(5, "K");
        mapCode.put(6, "L");
        mapCode.put(7, "M");
        mapCode.put(8, "N");
        mapCode.put(9, "O");
        mapCode.put(10, "P");
        mapCode.put(11, "Q");
        mapCode.put(12, "R");
        mapCode.put(13, "S");
        mapCode.put(14, "T");
        mapCode.put(15, "U");
        mapCode.put(16, "V");
        mapCode.put(17, "W");
        mapCode.put(18, "X");
        mapCode.put(19, "Y");
        mapCode.put(20, "g");
        mapCode.put(40, "h");
        mapCode.put(60, "i");
        mapCode.put(80, "j");
        mapCode.put(100, "k");
        mapCode.put(120, "l");
        mapCode.put(140, "m");
        mapCode.put(160, "n");
        mapCode.put(180, "o");
        mapCode.put(200, "p");
        mapCode.put(220, "q");
        mapCode.put(240, "r");
        mapCode.put(260, "s");
        mapCode.put(280, "t");
        mapCode.put(300, "u");
        mapCode.put(320, "v");
        mapCode.put(340, "w");
        mapCode.put(360, "x");
        mapCode.put(380, "y");        
        mapCode.put(400, "z");            
    }
    public String convertfromImg(BufferedImage image) throws IOException {
        String cuerpo = createBody(image);
        if(compressHex)
           cuerpo = encodeHexAscii(cuerpo);
        return headDoc() + cuerpo + footDoc();        
    }
    private String createBody(BufferedImage orginalImage) throws IOException {
        StringBuffer sb = new StringBuffer();
        Graphics2D graphics = orginalImage.createGraphics();
        graphics.drawImage(orginalImage, 0, 0, null);
        int height = orginalImage.getHeight();
        int width = orginalImage.getWidth();
        int rgb, red, green, blue, index=0;        
        char auxBinaryChar[] =  {'0', '0', '0', '0', '0', '0', '0', '0'};
        widthBytes = width/8;
        if(width%8>0){
            widthBytes= (((int)(width/8))+1);
        } else {
            widthBytes= width/8;
        }
        this.total = widthBytes*height;
        for (int h = 0; h<height; h++)
        {
            for (int w = 0; w<width; w++)
            {
                rgb = orginalImage.getRGB(w, h);
                red = (rgb >> 16 ) & 0x000000FF;
                green = (rgb >> 8 ) & 0x000000FF;
                blue = (rgb) & 0x000000FF;
                char auxChar = '1';
                int totalColor = red + green + blue;
                if(totalColor>blackLimit){
                    auxChar = '0';
                }
                auxBinaryChar[index] = auxChar;
                index++;
                if(index==8 || w==(width-1)){
                    sb.append(fourByteBinary(new String(auxBinaryChar)));
                    auxBinaryChar =  new char[]{'0', '0', '0', '0', '0', '0', '0', '0'};
                    index=0;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    private String fourByteBinary(String binaryStr){
        int decimal = Integer.parseInt(binaryStr,2);
        if (decimal>15){
            return Integer.toString(decimal,16).toUpperCase();
        } else {
            return "0" + Integer.toString(decimal,16).toUpperCase();
        }
    }
    private String encodeHexAscii(String code){
        int maxlinea =  widthBytes * 2;        
        StringBuffer sbCode = new StringBuffer();
        StringBuffer sbLinea = new StringBuffer();
        String previousLine = null;
        int counter = 1;
        char aux = code.charAt(0);
        boolean firstChar = false; 
        for(int i = 1; i< code.length(); i++ ){
            if(firstChar){
                aux = code.charAt(i);
                firstChar = false;
                continue;
            }
            if(code.charAt(i)=='\n'){
                if(counter>=maxlinea && aux=='0'){
                    sbLinea.append(",");
                } else     if(counter>=maxlinea && aux=='F'){
                    sbLinea.append("!");
                } else if (counter>20){
                    int multi20 = (counter/20)*20;
                    int resto20 = (counter%20);
                    sbLinea.append(mapCode.get(multi20));
                    if(resto20!=0){
                        sbLinea.append(mapCode.get(resto20) + aux);    
                    } else {
                        sbLinea.append(aux);    
                    }
                } else {
                    sbLinea.append(mapCode.get(counter) + aux);
                    if(mapCode.get(counter)==null){
                    }
                }
                counter = 1;
                firstChar = true;
                if(sbLinea.toString().equals(previousLine)){
                    sbCode.append(":");
                } else {
                    sbCode.append(sbLinea.toString());
                }                
                previousLine = sbLinea.toString();
                sbLinea.setLength(0);
                continue;
            }
            if(aux == code.charAt(i)){
                counter++;                
            } else {
                if(counter>20){
                    int multi20 = (counter/20)*20;
                    int resto20 = (counter%20);
                    sbLinea.append(mapCode.get(multi20));
                    if(resto20!=0){
                        sbLinea.append(mapCode.get(resto20) + aux);    
                    } else {
                        sbLinea.append(aux);    
                    }
                } else {
                    sbLinea.append(mapCode.get(counter) + aux);
                }
                counter = 1;
                aux = code.charAt(i);
            }            
        }
        return sbCode.toString();
    }
    private String headDoc(){
        String str = "^XA " +
                        "^FO0,0^GFA,"+ total + ","+ total + "," + widthBytes +", ";
        return str;
    }
    private String footDoc(){
        String str = "^FS"+
                        "^XZ";        
        return str;
    }
    public void setCompressHex(boolean compressHex) {
        this.compressHex = compressHex;
    }
    public void setBlacknessLimitPercentage(int percentage){
        blackLimit = (percentage * 768 / 100);
    }
  
}
@FXML
        public void handleButtonAction1(ActionEvent event) throws IOException, ClassNotFoundException, InterruptedException {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		int returnValue = jfc.showOpenDialog(null);
		

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			choosen = (selectedFile.getAbsolutePath());
                        
		}
        }




	
        
@FXML
    @SuppressWarnings("empty-statement")
    public void handleButtonAction(ActionEvent event) throws IOException, ClassNotFoundException, InterruptedException {
        String sentence;
        String address1;
        int port1;
        address1 = address.getText();
        port1 = Integer.parseInt(port.getText());	
        sentence = TextArea.getText();
        
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        BufferedImage orginalImage = ImageIO.read(new File(choosen));
        ZPLConveter zp = new ZPLConveter();
        zp.setCompressHex(true);
        zp.setBlacknessLimitPercentage(50);        
        TextArea.setText(zp.convertfromImg(orginalImage));
        try (Socket clientSocket = new Socket(address1, port1)) {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            byte[] b = sentence.getBytes();
            outToServer.write(b);
           
            Thread.sleep(1000);
            
            clientSocket.close();
            
        }
        }
        }

    
        

            

        

            
            
        
        







    
    
    








