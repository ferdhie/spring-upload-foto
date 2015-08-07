/*
 * Copyright (c) Herdian Ferdianto <herdian-at-ferdianto.com>
 * 
 * Everyone is permitted to copy and distribute verbatim or modified 
 * copies of this license document, and changing it is allowed as long 
 * as the name is changed. 
 * 
 */
package com.ferdianto.web.uploadfoto;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller untuk upload file dan list semua file yang terupload
 * @author ferdhie
 */
@Controller
public class UploadController {

    //inject dari config.properties
    @Value("${uploaddir}")
    private String uploadDir;
    
    //inject dari config.properties
    @Value("${image.maxheight:300}")
    private int maxImageHeight;

    /**
     * Index controller
     * @param request cek variabel OK di querystring
     * @param model map variable utl template
     * @return template jsp string
     */
    @RequestMapping("/")
    public String welcome(HttpServletRequest request, Map<String,Object> model) {
        if (request.getParameter("ok") != null) {
            model.put("success_message", "Upload sukses");
        }
        
        List<String> fileList = listUploadedFiles();
        
        model.put("files", fileList);
        model.put("upload_dir", uploadDir);
        return "index";
    }

    /**
     * Upload file controller
     * @param file file yg diupload
     * @param model template
     * @return jsp template file name
     */
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file, Map<String,Object> model) {
        String filename = file.getOriginalFilename().toLowerCase();
        String filetype = file.getContentType();
        model.put("file_name", filename);
        model.put("file_type", filetype);
        model.put("upload_dir", uploadDir);
        
        //validasi
        if (!validasiFileUpload(file)) {
            model.put("error_message", "File yang diupload tidak valid");
        } else {
            
            try {
                resizeImageAndSave( file );
                return "redirect:/?ok=1";
            } catch(IOException ioe) {
                model.put("error_message", "Error: " + ioe);
            }
            
        }
        
        return "index";
    }
    
    /**
     * Menampilkan semua file yang terupload
     * @return List<String> nama file yang telah diupload
     */
    private List<String> listUploadedFiles() {
        File dir = new File(uploadDir);
        String[] filenames = dir.list(new FilenameFilter() {

            //hanya filter file yang berakhiran jpg,gif,png
            @Override
            public boolean accept(File dir, String name) {
                String n = name.toLowerCase();
                return n.endsWith(".jpg") || n.endsWith(".gif") || n.endsWith(".png") ? true : false;
            }
            
        });
        return Arrays.asList(filenames);
    }
    
    /**
     * Resize image sesuai dengan ukuran secara proporsional
     * @param file MultipartFile post multipart dari controller / servlet
     * @throws IOException kalau error
     */
    private void resizeImageAndSave( MultipartFile file ) throws IOException {
        String filename = file.getOriginalFilename().toLowerCase();
        int dotIndex = filename.lastIndexOf('.');
        String basename = filename.substring(0, dotIndex);
        String extension = filename.substring(dotIndex);
        basename = basename.replaceAll("[^a-zA-Z0-9_\\-]", ""); //nama file tidak boleh mengandung sesuatu
        
        //cari apakah user pernah upload file yg sama, jika iya, rename filenya
        if (!uploadDir.endsWith("/"))
            uploadDir = uploadDir + "/";
        
        for(int i=0; true ; i++) {
            File cek = new File(uploadDir + basename + extension);
            if (!cek.exists()) break;
            basename = basename + Integer.toString(i);
        }
        
        String imgFileType = extension.substring(1);
        
        //baca file ke bufferedImage
        byte[] uploadData = file.getBytes();
        
//https://docs.oracle.com/javase/1.5.0/docs/api/java/awt/image/BufferedImage.html
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream( uploadData ));
        int imageType = (originalImage.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        //konversi ke double biar ga jadi 0 hasilnya
        double maxHeight = (double)maxImageHeight;
        double ratio = 300.0 / (double)height;
        int newWidth = (int)(width * ratio);
        
        System.out.println( ">>>> UPLOAD IMAGE FILE width="+width+",height="+height+",ratio="+ratio+",newWidth="+newWidth );
        
        //resize image nya
        BufferedImage resizedImage = new BufferedImage( newWidth, maxImageHeight, imageType );
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, 300, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //simpan ke file
        File output = new File(uploadDir + basename + extension);
        FileOutputStream outputStream = new FileOutputStream(output);
 
        //https://docs.oracle.com/javase/6/docs/api/javax/imageio/ImageIO.html
        ImageIO.write(resizedImage, imgFileType, outputStream);
        outputStream.close();
        
    }
    
    /**
     * Cek ekstensi dan mimetype file yang diupload
     * @param file post multipart file
     * @return 
     */
    private boolean validasiFileUpload(MultipartFile file) {
        String filename = file.getOriginalFilename().toLowerCase();
        String filetype = file.getContentType();
        
        System.out.println(">>>> FILE UPLOAD CEK NAME="+filename+"; " + filetype);
        
        if ( ( filename.endsWith(".jpg") || filename.endsWith(".gif") || filename.endsWith(".png") )  &&
              ( filetype.equals("image/jpeg") || filetype.equals("image/png") || filetype.equals("image/gif") )  ) {
            return true;
        }
        
        return false;
        
    }
    
    
    
}
