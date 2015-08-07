/*
 * Copyright (c) Herdian Ferdianto <herdian-at-ferdianto.com>
 * 
 * Everyone is permitted to copy and distribute verbatim or modified 
 * copies of this license document, and changing it is allowed as long 
 * as the name is changed. 
 * 
 */
package com.ferdianto.web.uploadfoto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller untuk menampilkan image secara dinamis (tidak memakai mvc:resources)
 * @author ferdhie
 */
@Controller
public class DisplayImageController {
    //inject dari config.properties
    @Value("${uploaddir}")
    private String uploadDir;


    /**
     * Handle file response secara dinamis, cek index.jsp untuk contoh penggunaan
     * @param filename
     * @param request
     * @param response
     * @throws IOException 
     */
    @RequestMapping(value = "/getimg/{filename:.*}")
    public void getImg(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!uploadDir.endsWith("/"))
            uploadDir = uploadDir + "/";

        int dotIndex = filename.lastIndexOf('.');
        String basename = filename.substring(0, dotIndex);
        String extension = filename.substring(dotIndex);
        basename = basename.replaceAll("[^a-zA-Z0-9_\\-\\.]+", "");
        filename = basename + extension;
        
        System.out.println(">>>>> FILE + " + filename);
        
        String s = filename.toLowerCase();
        if (s.endsWith(".jpg") || s.endsWith(".gif") || s.endsWith(".png")) {
            File file = new File( uploadDir + filename );
            if (file.exists()) {
                InputStream in = null;
                OutputStream os = null;
                try {
                    String contentType = "application/octet-stream";
                    if ( s.endsWith(".jpg") ) {
                        contentType = "image/jpeg";
                    } else if ( s.endsWith(".gif") ) {
                        contentType = "image/gif";
                    } else if ( s.endsWith(".png") ) {
                        contentType = "image/png";
                    }
                    
                    response.setContentType(contentType);
                    response.setHeader("Content-length", Long.toString(file.length()));
                    in = new BufferedInputStream(new FileInputStream(file));
                    os = response.getOutputStream();
                    StreamUtils.copy(in, os);
                    return;
                } finally {
                    if (in!=null)try{in.close();} catch(Exception ex){}
                    if (os!=null)try{os.flush(); os.close();} catch(Exception ex){}
                }
            }
        }
        
        response.setStatus(404);
        Writer w = response.getWriter();
        w.write("404 not found");       
        w.flush();
    }
    
}
