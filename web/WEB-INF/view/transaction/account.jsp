<%@include file="../tags/header.jsp"%>

<table class="table table-striped">

    <thead>

        <tr>
            <th>Balance</th>
            <th></th>
            <th></th>
            <th></th>
        </tr>

    </thead>

    <tbody>

        <tr>
            <td>$${balance}</td>

            <td>
                <a class="btn btn-default" href="transaction?action=deposit">Deposit</a>
            </td>

            <td>
                <a class="btn btn-default" href="transaction?action=withdraw">Withdraw</a>
            </td>

            <td>
                <a class="btn btn-default" href="history">View history</a>
            </td>


        </tr>

    </tbody>

</table>

<%@include file="../tags/footer.jsp"%>
