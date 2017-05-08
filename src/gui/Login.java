package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField nameText;
	private JTextField moneyText;
	private JTextField betText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		setResizable(false);
		setTitle("Join game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		nameText = new JTextField();
		nameText.setBounds(180, 120, 190, 27);
		contentPane.add(nameText);
		nameText.setColumns(10);
		
		JLabel lblName = new JLabel("Username:");
		lblName.setBounds(235, 100, 80, 15);
		contentPane.add(lblName);
		
		moneyText = new JTextField();
		moneyText.setBounds(180, 198, 190, 27);
		contentPane.add(moneyText);
		moneyText.setColumns(10);
		
		JLabel lblMoney = new JLabel("Money:");
		lblMoney.setBounds(249, 178, 52, 15);
		contentPane.add(lblMoney);
		
		JLabel lblBet = new JLabel("Beginning Bet:");
		lblBet.setBounds(220, 251, 109, 27);
		contentPane.add(lblBet);
		
		betText = new JTextField();
		betText.setBounds(180, 275, 190, 27);
		contentPane.add(betText);
		betText.setColumns(10);
		
		JButton btnEnter = new JButton("enter");
		btnEnter.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				System.out.println("A player has entered the game!");
				String name = nameText.getText();
				double money = Double.parseDouble(moneyText.getText());
				double bet = Double.parseDouble(betText.getText());
				if(bet > money)
					return;
				addClient(name,money,bet);
			}
		});
		btnEnter.setBounds(216, 340, 117, 25);
		contentPane.add(btnEnter);
		getRootPane().setDefaultButton(btnEnter);
		
		JLabel lblTop = new JLabel("You are about to enter the game, please enter these details");
		lblTop.setBounds(54, 73, 442, 15);
		contentPane.add(lblTop);
		
		JLabel lbleg = new JLabel("(eg. 50)");
		lbleg.setBounds(244, 301, 61, 15);
		contentPane.add(lbleg);
	}
	/**
	 * takes the login GUI and creates a new client to enter the game
	 * @param name the String name of the player 
	 * @param money the total money the player has
	 * @param bet the amount of the initial bet
	 */
	private void addClient(String name, double money, double bet)
	{
		if(money > 0 && bet > 0 && money > bet && !name.equals(""))
		{
			dispose();
			System.out.println(name+" with $"+money+" would like to join the game betting "+bet);
			new GameClient(name,money,bet);
		}
	}
}
