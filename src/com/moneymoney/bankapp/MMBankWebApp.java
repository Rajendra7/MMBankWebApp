package com.moneymoney.bankapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;


@WebServlet("*.mm")
public class MMBankWebApp extends HttpServlet {
	private static final long serialVersionUID = 1L;

	  @Override
	public void init() throws ServletException {
		super.init();
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection
						("jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
				PreparedStatement preparedStatement = 
						connection.prepareStatement("DELETE FROM ACCOUNT");
				preparedStatement.execute();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();
		PrintWriter out=response.getWriter();
		SavingsAccount savingsAccount = null;
		switch(path){
		case "/addAccount.mm" :
			response.sendRedirect("AddAccount.html");
			break;
		case "/createAccount.mm" :
			String name = request.getParameter("name");
			double amount = Double.parseDouble(request.getParameter("amount"));
			boolean salary = request.getParameter("rdsalary").equalsIgnoreCase("n")?false:true;
			
			try {
				savingsAccountService.createNewAccount(name, amount, salary);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;
		case "/closeAccount.mm" :
			response.sendRedirect("accountDelete.html");
			break;
		case "/deleteAccount.mm" :
			int accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
			try {
				savingsAccountService.deleteAccount(accountNumber);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "/deposit.mm" :
			response.sendRedirect("Deposit.html");
			break;
		case "/depositAmmount.mm" :
			int accountNo = Integer.parseInt( request.getParameter("accountNumber"));
			double amountToDeposit = Double.parseDouble(request.getParameter("amount"));
			
			try {
				savingsAccount = savingsAccountService.getAccountById(accountNo);
				savingsAccountService.deposit(savingsAccount, amountToDeposit);
				DBUtil.commit();
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			break;
		case "/withdraw.mm" :
			response.sendRedirect("Withdraw.html");
			break;
		case "/withdrawAmount.mm" :
			int accountNum = Integer.parseInt( request.getParameter("accountNumber"));
			double amountToWithdraw = Double.parseDouble(request.getParameter("amount"));
			
			try {
				savingsAccount = savingsAccountService.getAccountById(accountNum);
				savingsAccountService.withdraw(savingsAccount, amountToWithdraw);
				DBUtil.commit();
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/check.mm" :
			response.sendRedirect("CheckBalance.html");
			break;
		case "/checkBalance.mm" :
			int accountNumb = Integer.parseInt( request.getParameter("accountNumber"));
			double amountTocheck;
			try {
				amountTocheck = savingsAccountService.checkBalance(accountNumb);
				System.out.println("Current Balance is "+amountTocheck);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/trasfer.mm" :
			response.sendRedirect("FundTransfer.html");
			break;
		case "/fundTrasfer.mm" :
			int senderAccountNumber = Integer.parseInt( request.getParameter("senderAccountNumber"));
			int receiverAccountNumber = Integer.parseInt( request.getParameter("receiverAccountNumber"));
			double amountToTrasfer = Double.parseDouble(request.getParameter("amount"));
			try {
				SavingsAccount senderSavingsAccount = savingsAccountService.getAccountById(senderAccountNumber);
				SavingsAccount receiverSavingsAccount = savingsAccountService.getAccountById(receiverAccountNumber);
				savingsAccountService.fundTransfer(senderSavingsAccount, receiverSavingsAccount, amountToTrasfer);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}