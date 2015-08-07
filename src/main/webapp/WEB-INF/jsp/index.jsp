<%@page contentType="text/html" pageEncoding="UTF-8"%><!DOCTYPE html>
<html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Upload File</title>
    <style>
        .gambar {padding:5px; margin:5px; display:block; float:left;}
        .cl {clear:both;}
    </style>
</head>
<body>
    UPLOAD DIR = ${upload_dir}<br>

    <form method="post" enctype="multipart/form-data" action="<c:url value="/upload"/>" >
        FILE: <input type="file" name="file" ><br>
        <input type="submit" value="Upload Gambar">
    </form>
        
    <c:if test="${not empty error_message}">
        <!-- display jika ada pesan upload gagal -->
        <b>UPLOAD GAGAL</b><br>
        <b>${error_message}</b><br>
    </c:if>
        
    <c:if test="${not empty success_message}">
        <!-- display jika ada pesan upload sukses-->
        <b>${success_message}</b><br>
    </c:if>
        
    <c:if test="${not empty file_name}">
        <!-- display jika ada file yang diupload -->
        <b>FILE TELAH DIUPLOAD</b><br>
        NAMA: ${file_name}<br>
        TIPE: ${file_type}<br>
    </c:if>
        
    
    <c:if test="${not empty files}">
        <!-- list files yang diupload -->
        
        <hr>
        <h2>Image, display menggunakan resources</h2>
        <div id="images">
            <c:forEach items="${files}" var="file" varStatus="loop">
                <img src="<c:url value="/img/${file}"/>" class="gambar" />
            </c:forEach>
            <br class="cl"/>
        </div>
       
        <hr>
        <h2>Image, display menggunakan custom controller</h2>
        <div id="images2">
            <c:forEach items="${files}" var="file" varStatus="loop">
                <img src="<c:url value="/getimg/${file}"/>" class="gambar" />
            </c:forEach>
            <br class="cl"/>
        </div>
    </c:if>

</body>
</html>
