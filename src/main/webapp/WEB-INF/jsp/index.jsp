<%@page contentType="text/html" pageEncoding="UTF-8"%><!DOCTYPE html>
<html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Upload File</title>
    <style>
        .gambar {padding:5px; margin:5px; display:block; float:left;}
        
    </style>
</head>
<body>
    UPLOAD DIR = ${upload_dir}<br>

    <form method="post" enctype="multipart/form-data" action="<c:url value="/upload"/>" >
        FILE: <input type="file" name="file" ><br>
        <input type="submit" value="Upload Gambar">
    </form>
        
    <c:if test="${not empty file_name}">
        <b>FILE TELAH DIUPLOAD</b><br>
        NAMA: ${file_name}<br>
        TIPE: ${file_type}<br>
    </c:if>
        
    
    <c:if test="${not empty files}">
        <hr>
        <div id="images">
            <c:forEach items="${files}" var="file" varStatus="loop">
                <img src="<c:url value="/img/${file}"/>" class="gambar" />
            </c:forEach>
        </div>
    </c:if>

</body>
</html>
