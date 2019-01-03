package com.moneymoney.bankapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.SortAccountByBalanceInAscending;
import com.moneymoney.account.SortAccountByHolderNameInAscending;
import com.moneymoney.account.SortAccountByHolderNameInDescending;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;

@WebServlet("*.mm")
public class MMBankWebApp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestDispatcher dispatcher;
	boolean toggle;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
			PreparedStatement preparedStatement = connection
					.prepareStatement("DELETE FROM ACCOUNT");
			preparedStatement.execute();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();
		// PrintWriter out=response.getWriter();
		SavingsAccount savingsAccount = null;
		switch (path) {
		case "/addAccount.mm":
			response.sendRedirect("AddAccount.html");
			break;
		case "/createAccount.mm":
			String name = request.getParameter("name");
			double amount = Double.parseDouble(request.getParameter("amount"));
			boolean salary = request.getParameter("rdsalary").equalsIgnoreCase(
					"n") ? false : true;

			try {
				savingsAccountService.createNewAccount(name, amount, salary);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;
		case "/closeAccount.mm":
			response.sendRedirect("accountDelete.html");
			break;
		case "/deleteAccount.mm":
			int accountNumber = Integer.parseInt(request
					.getParameter("accountNumber"));
			try {
				savingsAccountService.deleteAccount(accountNumber);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "/deposit.mm":
			response.sendRedirect("Deposit.html");
			break;
		case "/depositAmmount.mm":
			int accountNo = Integer.parseInt(request
					.getParameter("accountNumber"));
			double amountToDeposit = Double.parseDouble(request
					.getParameter("amount"));

			try {
				savingsAccount = savingsAccountService
						.getAccountById(accountNo);
				savingsAccountService.deposit(savingsAccount, amountToDeposit);
				DBUtil.commit();
				response.sendRedirect("index.html");
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
		case "/withdraw.mm":
			response.sendRedirect("Withdraw.html");
			break;
		case "/withdrawAmount.mm":
			int accountNum = Integer.parseInt(request
					.getParameter("accountNumber"));
			double amountToWithdraw = Double.parseDouble(request
					.getParameter("amount"));

			try {
				savingsAccount = savingsAccountService
						.getAccountById(accountNum);
				savingsAccountService
						.withdraw(savingsAccount, amountToWithdraw);
				DBUtil.commit();
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {

				e.printStackTrace();
			}
			break;
		case "/check.mm":
			response.sendRedirect("CheckBalance.html");
			break;
		case "/checkBalance.mm":
			int accountNumb = Integer.parseInt(request
					.getParameter("accountNumber"));
			double amountTocheck;
			try {
				amountTocheck = savingsAccountService.checkBalance(accountNumb);
				System.out.println("Current Balance is " + amountTocheck);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {

				e.printStackTrace();
			}
			break;
		case "/trasfer.mm":
			response.sendRedirect("FundTransfer.html");
			break;
		case "/fundTrasfer.mm":
			int senderAccountNumber = Integer.parseInt(request
					.getParameter("senderAccountNumber"));
			int receiverAccountNumber = Integer.parseInt(request
					.getParameter("receiverAccountNumber"));
			double amountToTrasfer = Double.parseDouble(request
					.getParameter("amount"));
			try {
				SavingsAccount senderSavingsAccount = savingsAccountService
						.getAccountById(senderAccountNumber);
				SavingsAccount receiverSavingsAccount = savingsAccountService
						.getAccountById(receiverAccountNumber);
				savingsAccountService.fundTransfer(senderSavingsAccount,
						receiverSavingsAccount, amountToTrasfer);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {

				e.printStackTrace();
			}
			break;
		case "/searchForm.mm":
			response.sendRedirect("SearchForm.jsp");
			break;
		case "/search.mm":
			int accountNumber1 = Integer.parseInt(request
					.getParameter("txtAccountNumber"));
			try {
				SavingsAccount account = savingsAccountService
						.getAccountById(accountNumber1);
				request.setAttribute("account", account);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "/getAll.mm":
			try {
				List<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				request.setAttribute("accounts", accounts);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {

				e.printStackTrace();
			}
			break;
		
		case "/sortByName.mm":
			toggle=!toggle;
			List<SavingsAccount> accountInOrder;
			try {
				
				if(toggle=true){
					accountInOrder = savingsAccountService.getAllSavingsAccount();
					Collections.sort(accountInOrder, new SortAccountByHolderNameInAscending());
					request.setAttribute("accounts", accountInOrder);
					dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
					dispatcher.forward(request, response);
				}
				else if(toggle=false)
				{
					List<SavingsAccount> accountInReverseOrder;
					accountInReverseOrder = savingsAccountService.getAllSavingsAccount();
					Collections.sort(accountInReverseOrder, new SortAccountByHolderNameInDescending());
					request.setAttribute("accounts", accountInReverseOrder);
					dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
					dispatcher.forward(request, response);
				}
				
			} catch (ClassNotFoundException | SQLException e) {
				
				e.printStackTrace();
			}
			break;
		case "/sortByBalance.mm":
			toggle=!toggle;
			List<SavingsAccount> accountOrderByBalance;
			try {
				if(toggle=true){
				accountOrderByBalance = savingsAccountService.getAllSavingsAccount();
				Collections.sort(accountOrderByBalance, new SortAccountByBalanceInAscending());
				request.setAttribute("accounts", accountOrderByBalance);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
				}
			} catch (ClassNotFoundException | SQLException e) {
				
				e.printStackTrace();
			}
			break;
		case"/updateAcc.mm":
			response.sendRedirect("UpdateForm.html");
			break;
		case"/update.mm":
				int accountBal = Integer.parseInt(request.getParameter("currentBal"));
		try {
			SavingsAccount accountUpdate = savingsAccountService.getAccountById(accountBal);
			request.setAttribute("accounts", accountUpdate);
			dispatcher = request.getRequestDispatcher("UpdateDetails.jsp");
			dispatcher.forward(request, response);
		} catch (ClassNotFoundException | SQLException| AccountNotFoundException e) {
			e.printStackTrace();
		}
			break;
		case "/updateAccount.mm":
			int accountId = Integer.parseInt(request.getParameter("txtNum"));
		SavingsAccount accountUpdate;
		try {
			accountUpdate = savingsAccountService.getAccountById(accountId);
			String accHName = request.getParameter("txtAccHn");
			accountUpdate.getBankAccount().setAccountHolderName(accHName);
			double accBal = Double.parseDouble(request.getParameter("txtBal"));
			boolean isSalary = request.getParameter("rdSal").equalsIgnoreCase("no")?false:true;
			accountUpdate.setSalary(isSalary);
			savingsAccountService.updateAccount(accountUpdate);
			response.sendRedirect("getAll.mm");
		} catch (ClassNotFoundException | SQLException
				| AccountNotFoundException e) {
			e.printStackTrace();
		}
			break;
	
			
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
