<%@include file="../tags/header.jsp"%>

<form name="register" method="post">

    <label>Username: </label>
    <input type="text" required name="username" id="username" onblur="checkUsername()">
    <br>
    <span class="text-danger" id="username-validation"></span>
    <br>
    <label>Password: </label>
    <input type="password" required id="password" name="password" onblur="checkPassword()">
    <br>
    <span class="text-danger" id="password-validation"></span>
    <br>
    <label>Repeat password:</label>
    <input type="password" onblur="checkPasswordRepeat()" required id="password-repeat" name="password-repeat">
    <br>
    <span class="pass-error" id="pass-error"></span>
    <br>
    <input type="submit" id="submit" value="Register" onclick="return checkRegistrable()">

</form>

<%@include file="../tags/footer.jsp"%>
