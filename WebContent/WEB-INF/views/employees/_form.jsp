<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- エラーがあった場合、create, updateから値(errors)が渡される -->
<c:if test="${errors != null}">
    <div id="flush_error">
        入力内容にエラーがあります。<br />
        <c:forEach var="error" items="${errors}">
            ・<c:out value="${error}" /><br />
        </c:forEach>
    </div>
</c:if>

<!-- フォーム -->
<!-- Servletから値が渡される -->
<label for="code">社員番号</label><br />
<input type="text" name="code" value="${employee.code}" />
<br /><br />

<!-- Servletから値が渡される -->
<label for="name">氏名</label><br />
<input type="text" name="name" value="${employee.name}" />
<br /><br />

<label for="password">パスワード</label><br />
<input type="password" name="password" />
<br /><br />

<!-- Servletから値が渡される -->
<label for="admin_flag">権限</label><br />
<select name="admin_flag">
    <!-- フラグ0 or 1の選択 -->
    <option value="0"<c:if test="${employee.admin_flag == 0}"> selected</c:if>>一般</option>
    <option value="1"<c:if test="${employee.admin_flag == 1}"> selected</c:if>>管理者</option>
</select>
<br /><br />

<!-- セッションIdの隠れデータ、Servletから渡される -->
<input type="hidden" name="_token" value="${_token}" />
<button type="submit">投稿</button>