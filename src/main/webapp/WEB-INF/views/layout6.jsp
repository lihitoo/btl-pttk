<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.Html6,util.SessionUser6" %>
<%= Html6.document((String) request.getAttribute("title6"),
    (String) request.getAttribute("content6"),
    (SessionUser6) session.getAttribute("user6"),
    (String) session.getAttribute("csrf6"), request.getContextPath()) %>
