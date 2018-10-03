<%@include file="../tags/header.jsp"%>

    <form name="login" method="post">

        <label>Username: </label>
        <input type="text" required name="username">
        <br>
        <label>Password: </label>
        <input type="password" required name="password">
        <br>
        <input type="submit" value="Login">

    </form>

<%@include file="../tags/footer.jsp"%>
