<%@include file="../tags/header.jsp"%>

    <form method="post" name="doTransaction">

        <input type="text" name="amount" id="action-amount" onblur="checkNumber()" required>
        <span id="validation" class="text-danger"></span>
        <input type="submit" value="${action}" onclick="return checkNumber()">

    </form>

<%@include file="../tags/footer.jsp"%>
