<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="constants.ForwardConst" %>
<%@ page import="constants.AttributeConst" %>

<c:set var="action" value="${ForwardConst.ACT_REP.getValue()}" />
<c:set var="actTop" value="${ForwardConst.ACT_TOP.getValue()}" />
<c:set var="actEmp" value="${ForwardConst.ACT_EMP.getValue()}" />
<c:set var="actRep" value="${ForwardConst.ACT_REP.getValue()}" />
<c:set var="actAuth" value="${ForwardConst.ACT_AUTH.getValue()}" />

<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />
<c:set var="commOut" value="${ForwardConst.CMD_LOGOUT.getValue()}" />
<c:set var="commNew" value="${ForwardConst.CMD_NEW.getValue()}" />

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
    <title><c:out value="日報管理システム" /></title>
    <link rel="stylesheet" href="<c:url value='/css/reset.css' />">
    <link rel="stylesheet" href="<c:url value='/css/style.css' />">
</head>
<body>
    <div id="wrapper">
        <div id="header">
            <div id="header_menu">
                <h1>
                    <a href="<c:url value='/?action=${actTop}&command=${commIdx}' />">
                        日報管理システム
                    </a>
                </h1>&nbsp;
                <c:if test="${sessionScope.login_employee != null}">
                    <h3>
                        <a href="<c:url value='?action=${actRep}&command=${commNew}' />">
                            新規日報の登録
                        </a>
                    </h3>&nbsp;&nbsp;
                    <c:if test="${sessionScope.login_employee.adminFlag == AttributeConst.ROLE_ADMIN.getIntegerValue()}">
                        <h3>
                            <a href="<c:url value='?action=${actEmp}&command=${commNew}' />">
                                新規従業員の登録
                            </a>
                        </h3>
                    </c:if>
                </c:if>
            </div>
        </div>
        <div id="content">${param.content}</div>
        <div id="footer">
            <div id="footer_menu">
                <c:if test="${sessionScope.login_employee != null}">
                    <a href="<c:url value='?action=${action}&command=${commIdx}' />">日報管理</a>&nbsp;
                    <c:if test="${sessionScope.login_employee.adminFlag == AttributeConst.ROLE_ADMIN.getIntegerValue()}">
                        <a href="<c:url value='?action=${actEmp}&command=${commIdx}' />">従業員管理</a>&nbsp;
                    </c:if>
                    <a href="<c:url value='?action=${actAuth}&command=${commOut}' />">ログアウト</a>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>