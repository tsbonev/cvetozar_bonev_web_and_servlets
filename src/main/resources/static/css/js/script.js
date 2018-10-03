function checkPasswordRepeat() {

    var password = document.getElementById("password").value;
    var repeat = document.getElementById("password-repeat").value;
    var errorLabel = document.getElementById("pass-error");
    var submit = document.getElementById("submit");

    if(password != repeat){
        errorLabel.innerHTML = "Passwords must match!";
        submit.disabled = true;
    }
    else {
        errorLabel.innerHTML = "";
        submit.disabled = false;
    }
    
}

function checkUsername() {

    var value = document.getElementById("username").value;

    var usernameRegex = /^[\w]{5,15}$/;

    var result = usernameRegex.test(value);

    if(!result){
        document.getElementById("username-validation").innerHTML = "Invalid username, must be " +
            "between 5 and 15 alphanumeric characters!";
        return false;
    }
    else {
        document.getElementById("username-validation").innerHTML = "";
        return true;
    }

}

function checkPassword() {

    var value = document.getElementById("password").value;

    var passwordRegex = /^[\w]{8,20}$/;

    var result = passwordRegex.test(value);

    if(!result){
        document.getElementById("password-validation").innerHTML = "Invalid password, must be " +
            "between 8 and 20 alphanumeric characters!";
        return false;
    }
    else {
        document.getElementById("password-validation").innerHTML = "";
        return true;
    }

}

function checkRegistrable() {

    return (checkUsername() && checkPassword());

}

function checkNumber() {

    var value = document.getElementById("action-amount").value;

    var fraction = /^[1-9]{1}[0-9]{0,9}[.,]{1}[0-9]{1,5}$/,
        wholeNumber = /^[1-9]{1}[0-9]{0,9}$/,
        leadingZeroFraction = /^[0]{1}[.,]{1}[0-9]{0,4}[1-9]{1}$/,
        trailingZeroFraction = /^[0]{1}[.,]{1}[1-9]{0,4}[1-9]{1}$/;

    var result = fraction.test(value)
        || wholeNumber.test(value)
        || leadingZeroFraction.test(value)
        || trailingZeroFraction.test(value);


    if(!result){
        document.getElementById("validation").innerHTML = "Invalid number!";
        return false;
    }
    else {
        document.getElementById("validation").innerHTML = "";
        return true;
    }
}