<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body style="overflow: hidden;">
       
        <form action ="updateAccount.mm">
	           	<h1>UPDATE AN ACCOUNT</h1>
	           	<label>Account Number : <br><input type="text" name="txtNum" readonly="readonly" value="${requestScope.accounts.bankAccount.accountNumber}"></label><br><br></label> 
	           	<label>Name :<br><input type="text" name="txtAccHn" value="${requestScope.accounts.bankAccount.accountHolderName}"/></label><br><br>
				<label>AccountBalance :<br><input type="text" name="txtBal" readonly="readonly" value="${requestScope.accounts.bankAccount.accountBalance}"></label><br><br>
				<label>Salaried :</label>
				<label><input type="radio" name="rdSal" ${requestScope.accounts.salary==true?"checked":""}>YES</label>
				<label><input type="radio" name="rdSal" ${requestScope.accounts.salary==true?"":"checked"}>NO</label><br><br>
				<label><input type="submit" name="submit" value="Submit"></label>
				<label><input type="reset" name="reset" value="Reset"></label>
   
        </form>
      
</body>
</html>